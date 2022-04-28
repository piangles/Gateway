package org.piangles.gateway.requests.hooks;

import java.io.Serializable;
import java.util.UUID;

public final class AlertDetails implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String endpoint = null;
	private UUID traceId = null;
	private Throwable throwable = null;
	private String message = null;
	private long requestIssuedTime = 0L;
	
	public AlertDetails(String endpoint, UUID traceId, Throwable throwable, String message, long requestIssuedTime)
	{
		this.endpoint = endpoint;
		this.traceId = traceId;
		this.throwable = throwable;
		this.message = message;
		this.requestIssuedTime = requestIssuedTime;
	}

	public String getEndpoint()
	{
		return endpoint;
	}

	public UUID getTraceId()
	{
		return traceId;
	}

	public Throwable getThrowable()
	{
		return throwable;
	}
	
	public String getMessage()
	{
		return message;
	}

	public long getRequestIssuedTime()
	{
		return requestIssuedTime;
	}

	@Override
	public String toString()
	{
		return "AlertDetails [endpoint=" + endpoint + ", traceId=" + traceId + ", throwable=" + throwable + ", message=" + message + ", requestIssuedTime=" + requestIssuedTime + "]";
	}
}
