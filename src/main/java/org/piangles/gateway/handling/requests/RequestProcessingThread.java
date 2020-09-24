package org.piangles.gateway.handling.requests;

import java.util.UUID;

import org.piangles.gateway.dto.Request;
import org.piangles.gateway.dto.Response;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.notifcations.ClientNotifier;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.logging.LoggingService;
import com.TBD.backbone.services.session.SessionManagementException;
import com.TBD.backbone.services.session.SessionManagementService;
import com.TBD.core.services.remoting.SessionAwareable;
import com.TBD.core.services.remoting.SessionDetails;
import com.TBD.core.services.remoting.Traceable;

public final class RequestProcessingThread extends Thread implements Traceable, SessionAwareable
{
	private ClientDetails clientDetails = null;
	private Request request = null;
	private Response response = null;
	private ClientNotifier clientNotifier = null;

	private RequestProcessor requestProcessor = null;
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	protected LoggingService logger = Locator.getInstance().getLoggingService();
	
	public RequestProcessingThread(ClientDetails clientDetails, Request request, RequestProcessor requestProcessor, ClientNotifier clientNotifier)
	{
		this.clientDetails = clientDetails;
		this.request = request;
		this.requestProcessor = requestProcessor;
		this.clientNotifier = clientNotifier;
	}
	
	@Override
	public void run()
	{
		logger.info("Processing request for Endpoint : " + request.getEndpoint());

		boolean validSession = false;
		/**
		 * LoginRequest should be processed without checking for session,
		 * since session will not exist for login.
		 * 
		 * And PingMessage processing should be the only one that is allowed 
		 * without session validation for performance.
		 * 
		 */
		if (Endpoints.Ping.name().equals(requestProcessor.getEndpoint()) || 
				Endpoints.Login.name().equals(requestProcessor.getEndpoint())
			)
		{
			validSession = true;
		}
		else if (requestProcessor.shouldValidateSession())
		{
			try
			{
				validSession = sessionMgmtService.isValid(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());
			}
			catch (SessionManagementException e)
			{
				logger.error("Unable to validate Session because of : " + e.getMessage(), e);
				response = new Response(request.getTraceId(), request.getEndpoint(), false, "Unable to validate Session because of : " + e.getMessage());
			}
		}
		else if (clientDetails.getSessionDetails().getSessionId() != null)
		{
			String errorMessage = "RequestProcessor for endpoint " + requestProcessor.getEndpoint() + " has a sessionId but is coded for not validating session.";
			response = new Response(request.getTraceId(), request.getEndpoint(), false, errorMessage);
		}

		try
		{
			if (validSession && response == null)
			{
				//Finally the actual call to the RequestProcessor
				response = requestProcessor.processRequest(clientDetails, request);
			}
			else
			{
				response = new Response(request.getTraceId(), request.getEndpoint(), false, "Invalid sessionId.");
			}
		}
		catch(Exception e)
		{
			logger.warn("Exception while processing request because of : " + e.getMessage(), e);
			response = new Response(getTraceId(), request.getEndpoint(), false, e.getMessage());
		}
		ResponseProcessor.processResponse(clientDetails, response);
	}

	@Override
	public UUID getTraceId()
	{
		return request.getTraceId();
	}
	
	public Response getResponse()
	{
		return response;
	}
	
	public ClientNotifier getClientNotifier()
	{
		return clientNotifier;
	}

	@Override
	public SessionDetails getSessionDetails()
	{
		return clientDetails.getSessionDetails();
	}
}
