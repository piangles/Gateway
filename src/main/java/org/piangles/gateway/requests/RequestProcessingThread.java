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

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.session.SessionManagementException;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.services.remoting.AbstractContextAwareThread;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.events.EventProcessingManager;
import org.piangles.gateway.events.KafkaConsumerManager;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.StatusCode;

public final class RequestProcessingThread extends AbstractContextAwareThread
{
	private ClientDetails clientDetails = null;
	private Request request = null;
	private Response response = null;
	private EventProcessingManager mpm = null;

	private RequestProcessor requestProcessor = null;
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	protected LoggingService logger = Locator.getInstance().getLoggingService();
	
	public RequestProcessingThread(ClientDetails clientDetails, Request request, RequestProcessor requestProcessor, EventProcessingManager mpm)
	{
		super.init(clientDetails.getSessionDetails(), request.getTraceId());

		this.clientDetails = clientDetails;
		this.request = request;
		this.requestProcessor = requestProcessor;
		this.mpm = mpm;
	}
	
	@Override
	public void run()
	{
		logger.info("Processing request for Endpoint : " + request.getEndpoint());
		
		/**
		 * Close any Consumer that is marked for closing which was created on this thread.
		 */
		KafkaConsumerManager.getInstance().closeAnyMarked();

		boolean validSession = false;
		/**
		 * LoginRequest should be processed without checking for session,
		 * since session will not exist for login.
		 * 
		 * And PingMessage processing should be the only one that is allowed 
		 * without session validation for performance.
		 * 
		 */
		
		if (RequestRouter.getInstance().isPreAuthenticationEndpoint(requestProcessor.getEndpoint().name()))
		{
			validSession = true;
		}
		else if (requestProcessor.shouldValidateSession())
		{
			try
			{
				validSession = sessionMgmtService.isValid(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());
				/**
				 * The reason we are at this point in code is because the user took some manual action.
				 * And if the session is valid the last accessed should be made current.
				 */
				if (validSession)
				{
					sessionMgmtService.makeLastAccessedCurrent(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());
				}
				else
				{
					response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), request.getTransitTime(), StatusCode.UnAuthenticated, "Invalid sessionId.");
				}
			}
			catch (SessionManagementException e)
			{
				logger.error("Unable to validate Session because of : " + e.getMessage(), e);
				validSession = false;
				response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), request.getTransitTime(), StatusCode.InternalError, "Unable to authenticate validate session.");
			}
		}
		else if (clientDetails.getSessionDetails().getSessionId() != null)
		{
			validSession = true;
			logger.warn("RequestProcessor for endpoint " + requestProcessor.getEndpoint() + " has a sessionId but is coded for not validating session.");
		}

		try
		{
			if (validSession)
			{
				//Finally the actual call to the RequestProcessor
				response = requestProcessor.processRequest(clientDetails, request);
			}
		}
		catch(Exception e)
		{
			logger.warn("Exception while processing request because of : " + e.getMessage(), e);
			response = new Response(getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
									request.getTransitTime(), StatusCode.InternalError, StatusCode.InternalError.getMessage());
		}
		catch(Throwable e)
		{
			logger.error("Unhandled Exception while processing request because of : " + e.getMessage(), e);
			response = new Response(getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
					request.getTransitTime(), StatusCode.InternalError, StatusCode.InternalError.getMessage());
		}
		ResponseSender.sendResponse(clientDetails, response);
	}

	public Response getResponse()
	{
		return response;
	}
	
	public EventProcessingManager getMessageProcessingManager()
	{
		return mpm;
	}
}
