package org.piangles.gateway.dto;

import java.util.Date;
import java.util.UUID;

public final class Response
{
	private Date createdTime = null;
	private UUID traceId = null;

	/**
	 * Endpoint is required in response because when the client
	 * receieves the reponse back, it will make it easier for the 
	 * client to decode the appResponseAsString message to the client's
	 * implementation of the class.
	 */
	private String endpoint;
	/**
	 * requestSucessfull is a reflection of is the request was processed
	 * successfully without any exception. Not if the actual service accepted
	 * the request. Ex : LoginRequest even if failed authentication will still
	 * return requestSuccessful = true.
	 */
	private boolean requestSuccessful;

	private int httpStatusCode; //TODO
	private String errorMessage;
	private String appResponseAsString = null;

	public Response(UUID traceId, String endpoint, boolean requestSuccessful, String payload)
	{
		this.createdTime = new Date();
		this.traceId = traceId;
		this.endpoint = endpoint;

		this.requestSuccessful = requestSuccessful;
		if (requestSuccessful)
		{
			this.appResponseAsString = payload; 
		}
		else
		{
			this.errorMessage = payload; 
		}
	}
	
	public UUID getTraceId()
	{
		return traceId;
	}
	
	public String getEndpoint()
	{
		return endpoint;
	}

	public boolean isRequestSuccessful()
	{
		return requestSuccessful;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public String getAppResponseAsString()
	{
		return appResponseAsString;
	}

	@Override
	public String toString()
	{
		return "Response [createdTime=" + createdTime + ", traceId=" + traceId + ", requestSuccessful=" + requestSuccessful + ", errorMessage=" + errorMessage + "]";
	}
}
