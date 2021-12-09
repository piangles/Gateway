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

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.core.expt.BadRequestException;
import org.piangles.core.util.coding.JSON;
import org.piangles.core.util.validate.ValidationManager;
import org.piangles.core.util.validate.Validator;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.events.EventProcessingManager;
import org.piangles.gateway.requests.RequestProcessingThread;
import org.piangles.gateway.requests.RequestProcessor;
import org.piangles.gateway.requests.dao.GatewayDAO;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.StatusCode;
import org.piangles.gateway.requests.validators.DefaultGatewayRequestValidator;

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
public abstract class AbstractRequestProcessor<EndpointReq,EndpointResp> implements RequestProcessor  
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	/**
	 * There should not be any instance specific variables
	 * there will only one instance of the derived class per server
	 */
	private Enum<?> endpoint;
	private CommunicationPattern communicationPattern;
	private Class<EndpointReq> endpointRequestClass = null;
	private Class<EndpointResp> endpointResponseClass = null;
	private GatewayDAO gatewayDAO = null;
	private DefaultGatewayRequestValidator<EndpointReq> gatewayRequestValidator = null;

	public AbstractRequestProcessor(Enum<?> endpoint, Class<EndpointReq> requestClass, Class<EndpointResp> responseClass)
	{
		this(endpoint, CommunicationPattern.RequestAsynchronousResponse, requestClass, responseClass);
	}

	public AbstractRequestProcessor(Enum<?> endpoint, CommunicationPattern communicationPattern, Class<EndpointReq> endpointRequestClass, Class<EndpointResp> endpointResponseClass)
	{
		this.endpoint = endpoint;
		this.communicationPattern = communicationPattern;
		this.endpointRequestClass = endpointRequestClass;
		this.endpointResponseClass = endpointResponseClass;
		this.gatewayRequestValidator = new DefaultGatewayRequestValidator<EndpointReq>(endpointRequestClass);
	}
	
	@Override
	public final Response processRequest(ClientDetails clientDetails, Request request) throws Exception
	{
		Response response = null;
		EndpointReq epRequest = null;
		
		if (!EmptyRequest.class.equals(endpointRequestClass) && request.getEndpointRequest() != null)
		{
			try
			{
				epRequest = JSON.getDecoder().decode(request.getEndpointRequest().getBytes(), endpointRequestClass);
			}
			catch (Exception e)
			{
				String message = "EndpointRequest for: " + request.getEndpoint() + " could not be decoded."; 
				logger.error(message + " Reason: " + e.getMessage(), e);
				throw new BadRequestException(message);
			}
		}

		/**
		 * Validate the GatewayRequest
		 */
		gatewayRequestValidator.validate(clientDetails, request, epRequest);
		
		/**
		 * Validate the Endpoint Request
		 */
		Validator validator = ValidationManager.getInstance().getValidator(request.getEndpoint());
		if (validator != null) //Validate the EndpointRequest itself
		{
			validator.validate(clientDetails, request, epRequest);
		}
		
		/**
		 * Finally process the Request
		 * ---------------------------
		 */
		EndpointResp epResponse = processRequest(clientDetails, request, epRequest);

		//appResponse cannot be null
		String epResponseAsStr = new String(JSON.getEncoder().encode(epResponse));

		response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(),
								request.getTransitTime(), StatusCode.Success, epResponseAsStr);
		
		return response;
	}
	
	@Override
	public final Enum<?> getEndpoint()
	{
		return endpoint;
	}
	
	@Override
	public final Class<?> getEndpointRequestClass()
	{
		return endpointRequestClass;
	}

	@Override
	public final Class<?> getEndpointResponseClass()
	{
		return endpointResponseClass;
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
	
	@Override
	public final void setGatewayDAO(GatewayDAO gatewayDAO)
	{
		this.gatewayDAO = gatewayDAO;
	}
	
	protected final GatewayDAO getGatewayDAO()
	{
		return gatewayDAO;
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
	
	protected abstract EndpointResp processRequest(ClientDetails clientDetails, Request request, EndpointReq endpointRequest) throws Exception;
}
