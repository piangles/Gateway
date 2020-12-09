package org.piangles.gateway.handling.requests.processors;

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.events.EventProcessingManager;
import org.piangles.gateway.handling.requests.RequestProcessingThread;
import org.piangles.gateway.handling.requests.RequestProcessor;
import org.piangles.gateway.handling.requests.dto.EmptyRequest;
import org.piangles.gateway.handling.requests.dto.Request;
import org.piangles.gateway.handling.requests.dto.Response;

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
	//There should not be any instance specific variables
	//there will only one instance of the derived class per server
	private String endpoint;
	private boolean asyncProcessor;
	private Class<AppReq> requestClass = null;
	
	public AbstractRequestProcessor(String endpoint, Class<AppReq> requestClass)
	{
		this(endpoint, true, requestClass);
	}

	public AbstractRequestProcessor(String endpoint, boolean asyncHandler, Class<AppReq> requestClass)
	{
		this.endpoint = endpoint;
		this.asyncProcessor = asyncHandler; 
		this.requestClass = requestClass;
	}
	
	@Override
	public final Response processRequest(ClientDetails clientDetails, Request request) throws Exception
	{
		AppReq appRequest = null;
		
		if (!requestClass.equals(EmptyRequest.class) && request.getAppRequestAsString() != null)
		{
			appRequest = JSON.getDecoder().decode(request.getAppRequestAsString().getBytes(), requestClass);
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

		AppResp appResponse = processRequest(clientDetails, request, appRequest);
		
		String appResponseAsStr = new String(JSON.getEncoder().encode(appResponse));
		
		return new Response(request.getTraceId(), request.getEndpoint(), true, appResponseAsStr);
	}
	
	@Override
	public final String getEndpoint()
	{
		return endpoint;
	}
	
	@Override
	public final Class<?> getAppReqClass()
	{
		return requestClass;
	}

	@Override
	public final boolean isAsyncProcessor()
	{
		return asyncProcessor;
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
