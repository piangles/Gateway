package com.TBD.gateway.handling;

import java.net.InetSocketAddress;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.logging.LoggingService;
import com.TBD.core.services.remoting.SessionDetails;
import com.TBD.core.util.coding.JSON;
import com.TBD.gateway.ClientEndpoint;
import com.TBD.gateway.Constants;
import com.TBD.gateway.dto.LoginResponse;
import com.TBD.gateway.dto.Request;
import com.TBD.gateway.dto.Response;
import com.TBD.gateway.handling.notifcations.ClientNotifier;
import com.TBD.gateway.handling.requests.RequestProcessingThread;
import com.TBD.gateway.handling.requests.RequestProcessor;
import com.TBD.gateway.handling.requests.RequestRouter;
import com.TBD.gateway.handling.requests.ResponseProcessor;
import org.apache.commons.lang3.StringUtils;

/***
 * This is the entry point for any communication related with client. This Class
 * and any other classes here after should not have any references to Jetty.
 * 
 * All logging here has to come from the perspective of the client. This class
 * itself is not a thread but runs in a Thread created by Jetty. So keeping
 * track of traceId needs to be done through LoggerService.record.
 * 
 * There will be need for 2 types of Requests A : Synchronous B : Asynchronous
 * 
 * Both are executed on RequestProcessingThread the former however holds up the
 * queue of requests.
 */
public final class ClientHandler
{
	private LoggingService logger = null;

	private ClientHandlerState state = ClientHandlerState.PreAuthentication;
	private ClientDetails clientDetails = null;
	private ClientNotifier clientNotifier = null;

	public ClientHandler(InetSocketAddress remoteAddr, ClientEndpoint clientEndpoint)
	{
		/*
		 * UserId initially is the combination of the address and the port. But
		 * will change later through the transformation of loginId to
		 * syntheticUserId. SessionId will also be null
		 */
		logger = Locator.getInstance().getLoggingService();
		String userId = remoteAddr.getAddress().getHostName() + ":" + remoteAddr.getPort();

		clientDetails = new ClientDetails(remoteAddr, clientEndpoint, new SessionDetails(userId, null));

		logger.info(String.format("New connection from : [Host=%s & Port=%d ]", clientDetails.getHostName(), clientDetails.getPort()));
	}

	// TODO When do we get this?
	public void onClose(int statusCode, String reason)
	{
		logger.info(String.format("Close received for UserId=%s with StatusCode=%d and Reason=%s", clientDetails.getSessionDetails().getUserId(), statusCode, reason));
		clientNotifier.stop();
	}

	// TODO When do we get this?
	public void onError(Throwable t)
	{
		logger.error(String.format("Error received for UserId=%s with Message=%s", clientDetails.getSessionDetails().getUserId(), t.getMessage()), t);
		clientNotifier.stop();
		
		//
		// try
		// {
		// sessionMgmtService.unregister(clientDetails.getSessionDetails().getUserId(),
		// clientDetails.getSessionDetails().getSessionId());
		// }
		// catch (SessionManagementException e)
		// {
		// logger.error("Exception unregistering session for User [" +
		// clientDetails + "]", e);
		// }
	}

	/**
	 * @param message
	 */
	public void onMessage(String message)
	{
		Request request = null;
		Response response = null;

		//Step 1 : Log the receipt of the Raw Message, not the message itself.
		logger.info("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId());

		//Step 2 : Decode the raw message to Request.
		String endpoint = null;
		RequestProcessor requestProcessor = null;
		try
		{
			request = JSON.getDecoder().decode(message.getBytes(), Request.class);
			endpoint = request.getEndpoint(); 
			requestProcessor = RequestRouter.getInstance().getRequestProcessor(endpoint);
		}
		catch (Exception e)
		{
			logger.warn("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId() + " could not be decoded.", e);
			response = new Response(null, null, false, "Request could not be decoded because of : " + e.getMessage());
		}

		//Step 3 : Validate the request - so we not losing precious CPU/IO resources
		if (requestProcessor == null)
		{
			String errorMessage = "This endpoint " + request.getEndpoint() + " is not supported.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), false, errorMessage);
		}
		else if (state == ClientHandlerState.PreAuthentication && !Constants.ENDPOINT_LOGIN.equals(request.getEndpoint()))
		{
			String errorMessage = "This endpoint " + request.getEndpoint() + " requires authentication.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), false, errorMessage);
		}
		else if (clientDetails.getSessionDetails() != null && !StringUtils.equals(clientDetails.getSessionDetails().getSessionId(), request.getSessionId()))
		{
			String errorMessage = "SessionId between client and server does not match.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), false, errorMessage);
		}
		
		//Step 4 : Process the request only if the above conditions have not passed
		if (response == null && requestProcessor.isAsyncProcessor())
		{
			processRequestASynchronously(request);
		}
		else if (response == null && !requestProcessor.isAsyncProcessor()) //Request will be processed synchronously
		{
			try
			{
				response = processRequestSynchronously(request);
				
				switch (state)
				{
				case PreAuthentication:
					if (Constants.ENDPOINT_LOGIN.equals(request.getEndpoint()) && response.isRequestSuccessful())
					{
						try
						{
							LoginResponse loginResponse = JSON.getDecoder().decode(response.getAppResponseAsString().getBytes(), LoginResponse.class);
							if (loginResponse.isAuthenticated())
							{
								state = ClientHandlerState.PostAuthentication;
								
								//Now create a new client details from the original one but with new SessionDetails.
								//ClientDetails and SessionDetails are immutable. ClientDetails construction is only
								//visible to this package for security reasons.
								clientDetails = new ClientDetails(clientDetails.getRemoteAddress(), 
										clientDetails.getClientEndpoint(), 
										new SessionDetails(loginResponse.getUserId(), loginResponse.getSessionId()));

								//Now that client is authenticated, create the client notifier
								clientNotifier = new ClientNotifier(clientDetails);
								clientNotifier.start();
							}
							//Response for Login already goes through RequestProcessingThread
							response = null;
						}
						catch (Exception e)
						{
							//Probability is zero
							logger.error("InternalError-LoginResponse could not be decoded for client: " + clientDetails.getSessionDetails().getUserId(), e);
							response = new Response(request.getTraceId(), request.getEndpoint(), false, "InternalError - LoginResponse could not be decoded.");
						}
					}

					break;
				case PostAuthentication:
					if (request.getEndpoint().equals("Logout"))
					{
						state = ClientHandlerState.PreAuthentication;
					}
					break;
				}
			}
			catch (Exception e)
			{
				// Probability is low
				logger.error("Error in RequestProcessingThread because of : " + e.getMessage(), e);
				response = new Response(request.getTraceId(), request.getEndpoint(), false, "Could not process request because of : " + e.getMessage());
			}
		}
		
		/**
		 * This is the Exception response
		 */
		if (response != null)
		{
			ResponseProcessor.processResponse(clientDetails, response);
		}
	}

	private Response processRequestSynchronously(Request request) throws Exception
	{
		RequestProcessingThread reqProcThread = processRequestASynchronously(request);
		reqProcThread.join();
		return reqProcThread.getResponse();
	}

	private RequestProcessingThread processRequestASynchronously(Request request)
	{
		RequestProcessor rp = RequestRouter.getInstance().getRequestProcessor(request.getEndpoint());
		RequestProcessingThread reqProcThread = new RequestProcessingThread(clientDetails, request, rp, clientNotifier);
		reqProcThread.start();

		return reqProcThread;
	}
}
