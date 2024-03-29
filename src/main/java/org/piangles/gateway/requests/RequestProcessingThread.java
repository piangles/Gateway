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

import org.apache.commons.lang3.StringUtils;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.session.SessionManagementException;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.expt.ServiceRuntimeException;
import org.piangles.core.services.remoting.AbstractContextAwareThread;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.events.EventProcessingManager;
import org.piangles.gateway.events.KafkaConsumerManager;
import org.piangles.gateway.requests.dao.RequestResponseDetails;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.StatusCode;
import org.piangles.gateway.requests.hooks.AlertDetails;

public final class RequestProcessingThread extends AbstractContextAwareThread
{
	private static final String INTERNAL_ERR_MESSAGE = "Oh no! Something went wrong on our end. TraceId: %s";
	
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
					clientDetails.markLastAccessed();
				}
				else
				{
					response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), request.getTransitTime(), StatusCode.Unauthenticated, "Invalid sessionId.");
				}
			}
			catch (SessionManagementException e)
			{
				String message = "Unable to validate Session because of : " + e.getMessage();
				logger.error(message, e);
				validSession = false;
				response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), request.getTransitTime(), StatusCode.InternalError, processInternalErrorMessage(e, message));
			}
		}
		else if (clientDetails.getSessionDetails().getSessionId() != null)
		{
			validSession = true;
			logger.warn("RequestProcessor for endpoint " + requestProcessor.getEndpoint() + " has a sessionId but is coded for not validating session.");
		}

		try
		{
			/**
			 * FINALLY if the Session isValid make the call to the
			 * actual call to the RequestProcessor.
			 */
			if (validSession)
			{
				response = requestProcessor.processRequest(clientDetails, request);
			}
		}
		catch(ServiceRuntimeException e)
		{
			response = processServiceRuntimeException(e);
		}
		catch(Exception e)
		{
			logger.warn("Exception while processing request because of : " + extractThrowableMessage(e), e);
			response = new Response(getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
									request.getTransitTime(), StatusCode.InternalError, processInternalErrorMessage(e, extractThrowableMessage(e)));
		}
		catch(Throwable e) //The Top Level in Exception hierarchy. Next level has Error and Exception and we are covered. 
		{
			logger.error("Unhandled Exception while processing request because of : " + extractThrowableMessage(e), e);
			response = new Response(getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
					request.getTransitTime(), StatusCode.InternalError, processInternalErrorMessage(e, extractThrowableMessage(e)));
		}
		
		ResponseSender.sendResponse(clientDetails, response);
		
		try
		{
			RequestResponseDetails reqRespDetails = new RequestResponseDetails(clientDetails, request, response);

			RequestRouter.getInstance().getGatewayDAO().insertRequestResponseDetails(reqRespDetails);
		}
		catch (Throwable e)
		{
			logger.warn("IGNORING: Exception while persisting RequestResponseDetails. Reason: " + e.getMessage(), e);
		}
	}

	public Response getResponse()
	{
		return response;
	}
	
	public EventProcessingManager getMessageProcessingManager()
	{
		return mpm;
	}
	
	/**
	 * TODO : Remove this Hack, this was introduced because of
	 * SignUpRequestProcessor needing to setSessionDetails in Zuro.
	 */
	public void setSessionDetails(SessionDetails sessionDetails)
	{
		super.init(sessionDetails, getTraceId());
	}
	
	private Response processServiceRuntimeException(ServiceRuntimeException sre)
	{
		Response response = null;
		StatusCode statusCode = StatusCodeMapper.getInstance().getStatusCode(sre);

		String errorMessage = null;
		if (statusCode == null)
		{
			logger.warn("Unable to find StatusCode for ServiceRuntimeException class " + sre.getClass().getSimpleName() + " in sreStatusCodeMap, Defaulting to:" + StatusCode.InternalError);
			String message = sre.getClass().getSimpleName() + " while processing request because of : " + sre.getMessage();
			logger.error(message, sre);
			
			statusCode = StatusCode.InternalError;
			errorMessage = processInternalErrorMessage(sre, message);
		}
		else
		{
			logger.warn(sre.getClass().getSimpleName() + " while processing request because of : " + sre.getMessage(), sre);
			errorMessage = sre.getMessage();
		}
		
		response = new Response(getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
				request.getTransitTime(), statusCode, errorMessage);
		
		return response;
	}
	
	private String processInternalErrorMessage(Throwable throwable, String message)
	{
		String internalErrorMessage = String.format(INTERNAL_ERR_MESSAGE, getTraceId().toString());
		
		if (RequestRouter.getInstance().getAlertManager() != null)
		{
			AlertDetails alertDetails = new AlertDetails(request.getEndpoint(), getTraceId(), throwable, message, request.getIssuedTime());
			RequestRouter.getInstance().getAlertManager().process(alertDetails, clientDetails);
		}
		
		return internalErrorMessage;
	}
	
	private String extractThrowableMessage(Throwable e)
	{
		String exptMessage = e.getMessage();
		if (StringUtils.isBlank(exptMessage))
		{
			exptMessage = e.getClass().getCanonicalName();
		}
		
		return exptMessage;
	}
}
