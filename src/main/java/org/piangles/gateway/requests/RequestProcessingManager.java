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
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.session.SessionManagementException;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.expt.BadRequestException;
import org.piangles.core.expt.ServiceRuntimeException;
import org.piangles.core.expt.UnsupportedMediaException;
import org.piangles.core.resources.ResourceException;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.ClientEndpoint;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.GatewayConfiguration;
import org.piangles.gateway.Message;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.client.ClientState;
import org.piangles.gateway.events.EventProcessingManager;
import org.piangles.gateway.requests.dto.AuthenticationDetails;
import org.piangles.gateway.requests.dto.Ping;
import org.piangles.gateway.requests.dto.Pong;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.StatusCode;

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
	private SessionManagementService sessionService = null;  
	//private GeoLocationService geolocationService = null;

	private ClientState state = ClientState.PreAuthentication;
	private ClientDetails clientDetails = null;
	private EventProcessingManager epm = null;
	private boolean debugEnabled = false;
	private TraceIdStore traceIdStore;

	public RequestProcessingManager(InetSocketAddress remoteAddr, ClientEndpoint clientEndpoint, GatewayConfiguration gatewayConfiguration)
	{
		logger = Locator.getInstance().getLoggingService();
		sessionService = Locator.getInstance().getSessionManagementService();
		
		try 
		{
			if (gatewayConfiguration.isCacheTraceIdStoreEnabled()) 
			{
				traceIdStore = new CacheTraceIdStore();
			} 
			else 
			{
				//default to in-memory traceIdStore
				traceIdStore = new InMemoryTraceIdStore();
			}
		}
		catch (Exception e) 
		{
			logger.error("RequestProcessingManager->Error creating TraceIdStore", e);
			throw new ServiceRuntimeException(e);
		}
		/*
		 * UserId initially is the combination of the address and the port. But
		 * will change later through the transformation of loginId to
		 * syntheticUserId. SessionId will also be null
		 */
		//TODO : Use the IP Address of the client and get the geolocation. 
		//geolocationService = Locator.getInstance().getGeoLocationService();
		String userId = remoteAddr.getAddress().getHostName() + ":" + remoteAddr.getPort();

		clientDetails = new ClientDetails(remoteAddr, clientEndpoint, false, false, new SessionDetails(userId, null), 0L, 0L, null);

		logger.info(String.format("New connection from : [Host=%s & Port=%d ]", clientDetails.getHostName(), clientDetails.getPort()));
	}

	public void onClose(int statusCode, String reason)
	{
		logger.info(String.format("Close received for UserId=%s with StatusCode=%d and Reason=%s", clientDetails.getSessionDetails().getUserId(), statusCode, reason));
		try
		{
			if (StringUtils.isNoneBlank(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId()))
			{
				sessionService.markForUnregister(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());
			}
		}
		catch (SessionManagementException e)
		{
			logger.error("RequestProcessingManager->onClose: Unable to markForUnregister Session for UserId:" + clientDetails.getSessionDetails().getUserId(), e);
		}
		if (epm != null)
		{
			epm.stop();
		}
	}

	public void onError(Throwable t)
	{
		logger.error(String.format("Error received for UserId=%s with Message=%s", clientDetails.getSessionDetails().getUserId(), t.getMessage()), t);
		try
		{
			if (StringUtils.isNoneBlank(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId()))
			{
				sessionService.markForUnregister(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());
			}
		}
		catch (SessionManagementException e)
		{
			logger.error("RequestProcessingManager->onError: Unable to markForUnregister Session for UserId:" + clientDetails.getSessionDetails().getUserId(), e);
		}
		if (epm != null)
		{
			epm.stop();
		}
	}

	/**
	 * @param message
	 */
	public void onMessage(String messageAsString)
	{
		Message message = null;
		Request request = null;
		Response response = null;

		//Step 1 : Log the receipt of the Raw Message, not the message itself only if debugEnabled, else Ping will kill the logs.
		if (debugEnabled)
		{
			logger.debug("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId());
		}

		String endpoint = null;
		RequestProcessor requestProcessor = null;
		try
		{
			//Step 2 : Decode the raw message to Request.
			if (StringUtils.isBlank(messageAsString))
			{
				String errorMessage = "GatewayMessage cannot be empty.";
				logger.warn("Hacker->Alert: " + errorMessage + " ClientDetails: " + clientDetails);
				
				throw new BadRequestException(errorMessage);
			}
			
			try
			{
				message = JSON.getDecoder().decode(messageAsString.getBytes(), Message.class);
			}
			catch(Exception e)
			{
				logger.warn("Hacker->Alert: Unable to deserialize Message object. ClientDetails: " + clientDetails);
				
				clientDetails.getClientEndpoint().close();
				return;
			}
			
			//Step 3 : Decode the Gateway Request from the Message.
			if (StringUtils.isBlank(message.getPayload()))
			{
				String errorMessage = "GatewayMessage Payload cannot be empty.";
				logger.warn("Hacker->Alert: " + errorMessage + " ClientDetails: " + clientDetails);
				
				throw new BadRequestException(errorMessage);
			}
			
			try
			{
				request = JSON.getDecoder().decode(message.getPayload().getBytes(), Request.class);
			}
			catch(Exception e)
			{
				logger.warn("Hacker->Alert: Unable to deserialize Request object." + " ClientDetails: " + clientDetails);
				
				clientDetails.getClientEndpoint().close();
				return;
			}
			
			if (request.getTraceId() == null)
			{
				logger.warn("Hack->Alert: Request object has null TraceId.");
				
				closeOutHacker(request);
				return;
			}
			
			//Step 4: Gateway Request was decoded successfully, mark TransitTime 
			request.markTransitTime();
			
			//Step 5.1: Do Endpoint validation
			endpoint = request.getEndpoint();
			requestProcessor = RequestRouter.getInstance().getRequestProcessor(endpoint);
			
			if (requestProcessor == null && !Endpoints.Ping.name().equals(endpoint))//Step 5.2 : Endpoint not found.
			{
				String errorMessage = "This endpoint " + request.getEndpoint() + " is not supported.";
				logger.warn(errorMessage);
				response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
										request.getTransitTime(), StatusCode.NotFound, errorMessage);
			}
			else //Step 5.3 Endpoint found or it is Ping
			{
				//Step 6.1 : Validate Request Against the Current State
				response = validateRequestAgainstState(request, requestProcessor);
				
				if (response == null) //Request is Valid for the current State
				{
					if (Endpoints.Ping.name().equals(endpoint))//Step 6.2 Process Ping and send response
					{
						if (clientDetails.hasSessionExpired())
						{
							logger.info("Ping Received from User: " + clientDetails.getSessionDetails().getUserId() + " but session has expired. Closing connection.");
							clientDetails.getClientEndpoint().close();
						}
						else
						{
							clientDetails.getMetrics().increment(endpoint);
							
							Ping ping = JSON.getDecoder().decode(request.getEndpointRequest().getBytes(), Ping.class);
							Pong pong = new Pong(ping.getSequenceNo(), ping.getTimestamp());
							String epResponseAsStr = new String(JSON.getEncoder().encode(pong));

							response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(),
													request.getTransitTime(), StatusCode.Success, epResponseAsStr);
						}
					}
					else//Step 6.3 Process regular request
					{
						if (isTraceIdValid(request))
						{
							clientDetails.getMetrics().increment(endpoint);
							
							processRequestAndSendResponse(request, requestProcessor);
						}
					}
				}
			}
		}
		catch (InterruptedException e)//Thrown if Request is Synchronous and Join fails on Thread. 
		{
			// Probability is low
			logger.error("Error in RequestProcessingThread because of : " + e.getMessage(), e);
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(),
									request.getTransitTime(), StatusCode.InternalError, "Could not process request because of Internal Error.");
		}
		catch (ResourceException e)//On successful Login, EventProcessingManager could not be created
		{
			logger.error("EventProcessingManager Creation Failed: " + clientDetails.getSessionDetails().getUserId(), e);
			response = new Response(request.getTraceId(), request.getEndpoint(),request.getReceiptTime(), 
										request.getTransitTime(), StatusCode.InternalError, e.getMessage());
			
		}
		catch (BadRequestException e)
		{
			logger.warn("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId() + " could not be decoded.", e);
			response = new Response(StatusCode.BadRequest, "Request could not be decoded because of : " + e.getMessage());
		}
		catch (Exception e)
		{
			logger.warn("RequestProcessingManager Exception processing message from userId : " + clientDetails.getSessionDetails().getUserId(), e);
			response = new Response(request.getTraceId(), request.getEndpoint(),request.getReceiptTime(), 
										request.getTransitTime(), StatusCode.InternalError, e.getMessage());
		}

		/**
		 * This is the Pong (Ping Response) or an Exception Response
		 */
		if (response != null)
		{
			ResponseSender.sendResponse(clientDetails, response);
		}
	}

	private boolean isTraceIdValid(Request request) throws Exception 
	{
		boolean validity = false;
		
		if (request.getTraceId() != null)
		{
			String traceId = request.getTraceId().toString();

			//check if the traceId is present in Redis cache
			boolean found = traceIdStore.exists(traceId);
			if (found)
			{
				logger.warn("Hacker->Alert TraceId: " + request.getTraceId() + " is being reused.");
				closeOutHacker(request);
			}
			else
			{
				//store the TraceId in Redis
				//logger.debug("Adding TraceId: " + request.getTraceId() + " for: " + clientDetails);
				traceIdStore.put(traceId);
				validity = true;
			}
		}
		else 
		{
			logger.warn("Hacker->Alert TraceId for request for SessionId: " + request.getSessionId() + " is null.");
			closeOutHacker(request);
		}
		
		return validity;
	}

	private Response validateRequestAgainstState(Request request, RequestProcessor requestProcessor)
	{
		Response response = null;
		if (state == ClientState.PreAuthentication && !RequestRouter.getInstance().isPreAuthenticationEndpoint(request.getEndpoint()))
		{
			/**
			 * The Client is in PreAuthentication State, so only endpoints supported are that are registered in
			 * PreAuthenticationEndpoint Map in RequestRouter.
			 */
			String errorMessage = "This endpoint " + request.getEndpoint() + " requires authentication.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
									request.getTransitTime(), StatusCode.Unauthenticated, errorMessage);
		}
		else if (	Endpoints.Ping.name().equals(request.getEndpoint()) || 
					Endpoints.KeepSessionAlive.name().equals(request.getEndpoint()))
		{
			//Nothing to do here
		}
		else if (ClientStateDeterminator.isMidAuthentication(state) && !RequestRouter.getInstance().isMidAuthenticationEndpoint(request.getEndpoint()))
		{
			/**
			 * MidAuthenticatin State is when we have sent a GeneratedToken, the only endpoint allowed is 
			 * for user to change ChangePassword.
			 */
			String errorMessage = "This endpoint " + request.getEndpoint() + " requires further authentication actions.";
			logger.warn(errorMessage);
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
									request.getTransitTime(), StatusCode.ValidationFailure, errorMessage);
		}
		else if (!(state == ClientState.PreAuthentication && RequestRouter.getInstance().isPreAuthenticationEndpoint(request.getEndpoint()))
				&& !doesSessionIdsMatch(request.getSessionId()))
		{
			String errorMessage = "SessionId between client and server does not match.";
			logger.warn(errorMessage + " SessionIds ClientDetails["+clientDetails.getSessionDetails().getSessionId()+"] Request[" + request.getSessionId() + "]");
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), request.getTransitTime(), 
									StatusCode.Unauthenticated, errorMessage);
		}
		
		return response;
	}

	private void processRequestAndSendResponse(Request request, RequestProcessor requestProcessor) throws Exception
	{
		/**
		 * Since we have several communication patters, anything other than RequestResponse
		 * is Asynchronously processed.
		 */
		boolean asyncProcessor = !CommunicationPattern.RequestResponse.equals(requestProcessor.getCommunicationPattern());
		
		if (asyncProcessor)
		{
			processRequestASynchronously(request);
		}
		else
		{
			processRequestSynchronously(request);
		}
	}

	private void processRequestSynchronously(Request request) throws Exception
	{
		RequestProcessingThread reqProcThread = processRequestASynchronously(request);
		reqProcThread.join();
		
		Response response = reqProcThread.getResponse();
		
		performPostSynchronousRequestProcessingActions(request, response);
	}

	private RequestProcessingThread processRequestASynchronously(Request request)
	{
		RequestProcessor rp = RequestRouter.getInstance().getRequestProcessor(request.getEndpoint());
		
		RequestProcessingThread reqProcThread = new RequestProcessingThread(clientDetails, request, rp, epm);
		reqProcThread.start();

		return reqProcThread;
	}
	
	private void performPostSynchronousRequestProcessingActions(Request request, Response response) throws ResourceException, Exception
	{
		AuthenticationDetails authDetails = null;
		switch (state)
		{
		case PreAuthentication:
			if (RequestRouter.getInstance().isAuthenticationEndpoint(request.getEndpoint()) && response.isRequestSuccessful())
			{
				authDetails = JSON.getDecoder().decode(response.getEndpointResponse().getBytes(), AuthenticationDetails.class);
				if (authDetails.isAuthenticated())
				{
					/**
					 * Now create a new client details from the original one but 
					 * with new SessionDetails. ClientDetails and SessionDetails are
					 * immutable. ClientDetails construction is only visible to this 
					 * package for security reasons. 
					 */
					//GeoLocation geoLocation = geolocationService.getGeoLocation(clientDetails.getIPAddress());
					clientDetails = new ClientDetails(clientDetails.getRemoteAddress(), clientDetails.getClientEndpoint(),
							true, authDetails.isAuthenticatedBySession(),
							new SessionDetails(authDetails.getUserId(), authDetails.getSessionId()),
							authDetails.getInactivityExpiryTimeInSeconds(), authDetails.getLastLoggedInTimestamp(), null);
					clientDetails.markLastAccessed();
					//Location.convert(geoLocation, false));

					state = ClientState.valueOf(authDetails.getAuthenticationState());
					
					/**
					 * Now that client is authenticated, create the MessageProcessingManager
					 */
					logger.info("Creating EventProcessingManager for: " + clientDetails);
					epm = new EventProcessingManager(clientDetails);
				}
			}
			break;
		case MidAuthenticationResetPasswordRequired:
			if (RequestRouter.getInstance().isMidAuthenticationEndpoint(request.getEndpoint()) && response.isRequestSuccessful())
			{
				authDetails = JSON.getDecoder().decode(response.getEndpointResponse().getBytes(), AuthenticationDetails.class);

				logger.info("ResetPassword was successful moving to " + authDetails.getAuthenticationState() + " state for: " + clientDetails);
				
				state = ClientState.valueOf(authDetails.getAuthenticationState());
			}
			break;
		case MidAuthenticationMFARequired:
			if (RequestRouter.getInstance().isMidAuthenticationEndpoint(request.getEndpoint()) && response.isRequestSuccessful()) 
			{
				authDetails = JSON.getDecoder().decode(response.getEndpointResponse().getBytes(), AuthenticationDetails.class);

				logger.info("MultiFactorAuthentication was successful moving to " + authDetails.getAuthenticationState() + " state for: " + clientDetails);
				
				state = ClientState.valueOf(authDetails.getAuthenticationState());
			}
			break;
		case PostAuthentication:
			if (Endpoints.Logout.name().equals(request.getEndpoint()))
			{
				state = ClientState.PreAuthentication;
				clientDetails.getClientEndpoint().close();
			}
			break;
		}
		
		HookProcessor hookProcessor = null;
		if (ClientStateDeterminator.isMidAuthentication(state))
		{
			if (ClientState.MidAuthenticationMFARequired.equals(state))
			{
				sendMFAToken();
			}

			if (RequestRouter.getInstance().getMidAuthenticationHook() != null)
			{
				logger.info("Calling registered MidAuthenticationHook for: " + clientDetails);
				hookProcessor = new HookProcessor(request.getTraceId(), clientDetails.getSessionDetails(), ()->{
					RequestRouter.getInstance().getMidAuthenticationHook().process(request.getEndpoint(), clientDetails);
				});
			}
		}
		else if (state == ClientState.PostAuthentication && RequestRouter.getInstance().getPostAuthenticationHook() != null)
		{
			logger.info("Calling registered PostAuthenticationHook for: " + clientDetails);
			hookProcessor = new HookProcessor(request.getTraceId(), clientDetails.getSessionDetails(), ()->{
				RequestRouter.getInstance().getPostAuthenticationHook().process(request.getEndpoint(), clientDetails);
			});
		}
		
		if (hookProcessor != null)
		{
			hookProcessor.start();
			hookProcessor.join();
			logger.info("HookProcessing completed for: " + clientDetails);
		}
	}
	
	private boolean doesSessionIdsMatch(String sessionId)
	{
		return clientDetails.getSessionDetails() != null && StringUtils.equals(clientDetails.getSessionDetails().getSessionId(), sessionId);
	}
	
	private void sendMFAToken()
	{
		if (RequestRouter.getInstance().getMFAManager() != null)
		{
			RequestRouter.getInstance().getMFAManager().sendMFAToken(clientDetails);
		}
		else
		{
			throw new UnsupportedMediaException("Multi-Factor Authentication has not been setup.");
		}
	}
	
	private void closeOutHacker(Request request) throws SessionManagementException
	{
		String userId = clientDetails.getSessionDetails().getUserId();
		String sessionId = clientDetails.getSessionDetails().getSessionId();

		logger.warn("Hacker->Alert userId: " + userId + " sessionId: " + sessionId + " remoteAddress: " + clientDetails.getRemoteAddress());
		//un-reqister the session
		if (StringUtils.isNoneBlank(userId, sessionId))
		{
			sessionService.unregister(userId, sessionId);
		}
		clientDetails.getClientEndpoint().close();
	}
}
