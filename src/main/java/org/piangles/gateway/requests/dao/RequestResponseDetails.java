package org.piangles.gateway.requests.dao;

import java.io.Serializable;
import java.util.UUID;

import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.StatusCode;

public class RequestResponseDetails implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private UUID traceId;
	
	private String userId;
	private String sessionId;
	
	private String endpoint;
	
	private long requestIssuedTime;
	private long requestTransitTime;

	private long requestReceiptTime;

	private long responseIssuedTime;
	
	private long requestProcessingTime;
	
	private StatusCode statusCode = null;
	private boolean requestSuccessful = false;
	
	/**
	 * README
	 * ------
	 * Even though Request and Response are passed in as parameters,
	 * they are only used to construct the RequestResponseDetails and nothing more.
	 * This is important because sensitive information can be present in Request and Response
	 * that we do not want to capture in DB.
	 *  
	 * @param userId
	 * @param sessionId
	 * @param request
	 * @param response
	 */
	public RequestResponseDetails(ClientDetails clientDetails, Request request, Response response)
	{
		this.traceId = request.getTraceId();
		
		if (clientDetails != null && clientDetails.getSessionDetails() != null)
		{
			this.userId = clientDetails.getSessionDetails().getUserId();
			this.sessionId = clientDetails.getSessionDetails().getSessionId();
		}
		
		this.endpoint = request.getEndpoint();
		
		this.requestIssuedTime = request.getIssuedTime();
		this.requestTransitTime = response.getRequestTransitTime();
		
		this.requestReceiptTime = request.getReceiptTime();

		this.responseIssuedTime = response.getIssuedTime();
		
		this.requestProcessingTime = response.getRequestProcessingTime();
		
		this.statusCode = response.getStatusCode();
		
		this.requestSuccessful = response.isRequestSuccessful();
	}
	
	public UUID getTraceId()
	{
		return traceId;
	}
	
	public String getUserId()
	{
		return userId;
	}
	
	public String getSessionId()
	{
		return sessionId;
	}
	
	public String getEndpoint()
	{
		return endpoint;
	}
	
	public long getRequestIssuedTime()
	{
		return requestIssuedTime;
	}
	
	public long getRequestTransitTime()
	{
		return requestTransitTime;
	}
	
	public long getRequestReceiptTime()
	{
		return requestReceiptTime;
	}
	
	public long getResponseIssuedTime()
	{
		return responseIssuedTime;
	}
	
	public long getRequestProcessingTime()
	{
		return requestProcessingTime;
	}
	
	public StatusCode getStatusCode()
	{
		return statusCode;
	}
	
	public boolean isRequestSuccessful()
	{
		return requestSuccessful;
	}
}
