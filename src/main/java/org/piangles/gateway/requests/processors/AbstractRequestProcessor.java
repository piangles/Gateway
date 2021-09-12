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
 
 
 
package org.piangles.gateway.requests.processors;

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.events.EventProcessingManager;
import org.piangles.gateway.requests.RequestProcessingThread;
import org.piangles.gateway.requests.RequestProcessor;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.StatusCode;

/**
 * 
 * How do the implementing classes get access to 
 * 1. Over all request Object
 * 2. The ClientDetails Object
 * 3. Client Details does not contain SessionId 
 * 4. What if the RequestProcessor needs SessionId which it will.
 *  In the minimum RequestProcessingThread will need when there will be Authenticated Session 
 *  And calls to Services need Authenticated Sessions.
 * 5. RequesProcess should not ask Client to provide the userId in the calls, the userId should be picked from
 * the ClientDetails.
 * 
 */
public abstract class AbstractRequestProcessor<AppReq,AppResp> implements RequestProcessor  
{
	private static final String EMPTY_APP_REQUEST_ERR = "App Request cannot be null for this endpoint.";
	/**
	 * There should not be any instance specific variables
	 * there will only one instance of the derived class per server
	 */
	private Enum<?> endpoint;
	private CommunicationPattern communicationPattern;
	private Class<AppReq> requestClass = null;
	private Class<AppResp> responseClass = null;
	
	public AbstractRequestProcessor(Enum<?> endpoint, Class<AppReq> requestClass, Class<AppResp> responseClass)
	{
		this(endpoint, CommunicationPattern.RequestAsynchronousResponse, requestClass, responseClass);
	}

	public AbstractRequestProcessor(Enum<?> endpoint, CommunicationPattern communicationPattern, Class<AppReq> requestClass, Class<AppResp> responseClass)
	{
		this.endpoint = endpoint;
		this.communicationPattern = communicationPattern;
		this.requestClass = requestClass;
		this.responseClass = responseClass;
	}
	
	@Override
	public final Response processRequest(ClientDetails clientDetails, Request request) throws Exception
	{
		AppReq appRequest = null;
		
		if (!requestClass.equals(EmptyRequest.class) && request.getEndpointRequest() != null)
		{
			appRequest = JSON.getDecoder().decode(request.getEndpointRequest().getBytes(), requestClass);
		}

//		try
//		{
//			//TODO Validate the Request 
//			RequestValidator.validate(clientDetails, request);
//		}
//		catch (ValidationException e)
//		{
//			logger.warn("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId() + " is not valid.", e);
//			response = new Response(request.getTraceId(), request.getEndpoint(), false, "Request failed validation because of : " + e.getMessage());
//		}

		Response response = null;
		if (!EmptyRequest.class.equals(requestClass) && appRequest == null)
		{
			response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(), 
									request.getTransitTime(), StatusCode.BadRequest, EMPTY_APP_REQUEST_ERR);
		}
		else
		{
			AppResp appResponse = processRequest(clientDetails, request, appRequest);

			//appResponse cannot be null
			String appResponseAsStr = new String(JSON.getEncoder().encode(appResponse));

			response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(),
									request.getTransitTime(), StatusCode.Success, appResponseAsStr);
		}
		
		return response;
	}
	
	@Override
	public final Enum<?> getEndpoint()
	{
		return endpoint;
	}
	
	@Override
	public final Class<?> getRequestClass()
	{
		return requestClass;
	}

	@Override
	public final Class<?> getResponseClass()
	{
		return responseClass;
	}

	@Override
	public final CommunicationPattern getCommunicationPattern()
	{
		return communicationPattern;
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return true;
	}
	
	protected final EventProcessingManager getEventProcessingManager()
	{
		EventProcessingManager npm = null;
		Object currentThread = Thread.currentThread();
		if (currentThread instanceof RequestProcessingThread)
		{
			npm = ((RequestProcessingThread)currentThread).getMessageProcessingManager();
		}
		return npm;
	}
	
	protected abstract AppResp processRequest(ClientDetails clientDetails, Request request, AppReq appRequest) throws Exception;
}
