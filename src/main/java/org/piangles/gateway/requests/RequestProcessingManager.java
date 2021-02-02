/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.gateway.requests;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.StringUtils;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.geo.GeoLocation;
import org.piangles.backbone.services.geo.GeoLocationService;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.ClientEndpoint;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.client.ClientState;
import org.piangles.gateway.client.Location;
import org.piangles.gateway.events.EventProcessingManager;
import org.piangles.gateway.requests.dto.LoginResponse;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.SimpleResponse;

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
public final class RequestProcessingManager
{
	private LoggingService logger = null;
	private GeoLocationService geolocationService = null;

	private ClientState state = ClientState.PreAuthentication;
	private ClientDetails clientDetails = null;
	private EventProcessingManager epm = null;

	public RequestProcessingManager(InetSocketAddress remoteAddr, ClientEndpoint clientEndpoint)
	{
		/*
		 * UserId initially is the combination of the address and the port. But
		 * will change later through the transformation of loginId to
		 * syntheticUserId. SessionId will also be null
		 */
		logger = Locator.getInstance().getLoggingService();
		geolocationService = Locator.getInstance().getGeoLocationService();
		String userId = remoteAddr.getAddress().getHostName() + ":" + remoteAddr.getPort();

		clientDetails = new ClientDetails(remoteAddr, clientEndpoint, new SessionDetails(userId, null), null);

		logger.info(String.format("New connection from : [Host=%s & Port=%d ]", clientDetails.getHostName(), clientDetails.getPort()));
	}

	public void onClose(int statusCode, String reason)
	{
		logger.info(String.format("Close received for UserId=%s with StatusCode=%d and Reason=%s", clientDetails.getSessionDetails().getUserId(), statusCode, reason));
		if (epm != null)
		{
			epm.stop();
		}
	}

	public void onError(Throwable t)
	{
		logger.error(String.format("Error received for UserId=%s with Message=%s", clientDetails.getSessionDetails().getUserId(), t.getMessage()), t);
		if (epm != null)
		{
			epm.stop();
		}
	}

	/**
	 * @param message
	 */
	public void onMessage(String message)
	{
		Request request = null;
		Response response = null;

		// Step 1 : Log the receipt of the Raw Message, not the message itself.
		logger.info("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId());

		// Step 2 : Decode the raw message to Request.
		String endpoint = null;
		RequestProcessor requestProcessor = null;
		try
		{
			request = JSON.getDecoder().decode(message.getBytes(), Request.class);
			request.markTransitTime();
			endpoint = request.getEndpoint();
			requestProcessor = RequestRouter.getInstance().getRequestProcessor(endpoint);
		}
		catch (Exception e)
		{
			logger.warn("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId() + " could not be decoded.", e);
			response = new Response(null, null, 0, false, "Request could not be decoded because of : " + e.getMessage());
		}

		// Step 3 : Request was able to decoded and found a requestProcessor
		if (request != null && requestProcessor != null)
		{
			response = processRequest(request, requestProcessor);
		}
		else if (request != null)
		{
			String errorMessage = "This endpoint " + request.getEndpoint() + " is not supported.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getTransitTime(), false, errorMessage);
		}

		/**
		 * This is the Exception Response
		 */
		if (response != null)
		{
			ResponseSender.sendResponse(clientDetails, response);
		}
	}

	private Response processRequest(Request request, RequestProcessor requestProcessor)
	{
		Response response = null;
		if (state == ClientState.PreAuthentication && !RequestRouter.getInstance().isPreAuthenticationEndpoint(request.getEndpoint()))
		{
			String errorMessage = "This endpoint " + request.getEndpoint() + " requires authentication.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getTransitTime(), false, errorMessage);
		}
		else if (state == ClientState.MidAuthentication && !Endpoints.ChangePassword.name().equals(request.getEndpoint()))
		{
			// This is the part that makes sure we only accept ChangePassword
			String errorMessage = "This endpoint " + request.getEndpoint() + " requires password to be updated.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getTransitTime(), false, errorMessage);
		}
		else if (!(state == ClientState.PreAuthentication && RequestRouter.getInstance().isPreAuthenticationEndpoint(request.getEndpoint()))
				&& (clientDetails.getSessionDetails() != null && !StringUtils.equals(clientDetails.getSessionDetails().getSessionId(), request.getSessionId())))
		{
			String errorMessage = "SessionId between client and server does not match.";
			logger.warn(errorMessage + " SessionIds ClientDetails["+clientDetails.getSessionDetails().getSessionId()+"] Request[" + request.getSessionId() + "]");
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getTransitTime(), false, errorMessage);
		}

		// Step 4 : Process the request only if the above conditions have not
		// passed
		if (response == null && isAsyncProcessor(requestProcessor))
		{
			processRequestASynchronously(request);
		}
		else if (response == null && !isAsyncProcessor(requestProcessor))
		{
			//Request will be processed synchronously
			try
			{
				response = processRequestSynchronously(request);

				/**
				 * Post the Request being processed Synchronously
				 */
				switch (state)
				{
				case PreAuthentication:
					Response errResponse = null;
					if (Endpoints.Login.name().equals(request.getEndpoint()) && response.isRequestSuccessful())
					{
						try
						{
							LoginResponse loginResponse = JSON.getDecoder().decode(response.getAppResponseAsString().getBytes(), LoginResponse.class);
							if (loginResponse.isAuthenticated())
							{
								if (loginResponse.isAuthenticatedByToken())
								{
									state = ClientState.MidAuthentication;
								}
								else
								{
									state = ClientState.PostAuthentication;
								}

								/**
								 * Now create a new client details from the original one but 
								 * with new SessionDetails. ClientDetails and SessionDetails are
								 * immutable. ClientDetails construction is only visible to this 
								 * package for security reasons. 
								 */
								GeoLocation geoLocation = geolocationService.getGeoLocation(clientDetails.getIPAddress());
								clientDetails = new ClientDetails(clientDetails.getRemoteAddress(), clientDetails.getClientEndpoint(),
										new SessionDetails(loginResponse.getUserId(), loginResponse.getSessionId()),
										Location.convert(geoLocation, false));

								/**
								 * Now that client is authenticated, create the MessageProcessingManager
								 */
								logger.info("Creating EventProcessingManager for: " + clientDetails);
								epm = new EventProcessingManager(clientDetails);
							}
							
							/**
							 * Responses for Synchronous Requests already goes through RequestProcessingThread
							 */
							response = null;
						}
						catch (Exception e)
						{
							// Probability is zero
							logger.error("InternalError-LoginResponse could not be decoded for client: " + clientDetails.getSessionDetails().getUserId(), e);
							errResponse = new Response(request.getTraceId(), request.getEndpoint(), request.getTransitTime(), false, "InternalError - LoginResponse could not be decoded.");
						}
					}
					
					response = errResponse;
					break;
				case MidAuthentication:
					if (Endpoints.ChangePassword.name().equals(request.getEndpoint()) && response.isRequestSuccessful())
					{
						try
						{
							SimpleResponse simpleResponse = JSON.getDecoder().decode(response.getAppResponseAsString().getBytes(), SimpleResponse.class);
							if (simpleResponse.isAppRequestSuccessful())
							{
								state = ClientState.PostAuthentication;
							}
							else
							{
								// Log and keep it as is.
								logger.info("ChangePassword was not successful because of:" + simpleResponse.getAppResponseMessage());
							}
							response = null;
						}
						catch (Exception e)
						{
							// Probability is zero
							logger.error("InternalError-LoginResponse could not be decoded for client: " + clientDetails.getSessionDetails().getUserId(), e);
							response = new Response(request.getTraceId(), request.getEndpoint(), request.getTransitTime(), false, "InternalError - ChangePassword could not be decoded.");
						}
					}
					break;
				case PostAuthentication:
					if (request.getEndpoint().equals("Logout"))
					{
						state = ClientState.PreAuthentication;
					}
					break;
				}
			}
			catch (Exception e)
			{
				// Probability is low
				logger.error("Error in RequestProcessingThread because of : " + e.getMessage(), e);
				response = new Response(request.getTraceId(), request.getEndpoint(), request.getTransitTime(), false, "Could not process request because of : " + e.getMessage());
			}
		}
		
		return response;
	}
	
	private boolean isAsyncProcessor(RequestProcessor requestProcessor)
	{
		return !CommunicationPattern.RequestResponse.equals(requestProcessor.getCommunicationPattern());
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
		RequestProcessingThread reqProcThread = new RequestProcessingThread(clientDetails, request, rp, epm);
		reqProcThread.start();

		return reqProcThread;
	}
}
