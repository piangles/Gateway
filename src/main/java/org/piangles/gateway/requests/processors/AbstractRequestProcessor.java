package org.piangles.gateway.requests.processors;

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.events.EventProcessingManager;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.RequestProcessingThread;
import org.piangles.gateway.requests.RequestProcessor;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;

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
