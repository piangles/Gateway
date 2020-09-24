package org.piangles.gateway.dto;

import java.util.Date;
import java.util.UUID;

public final class Request
{
	private Date createdTime = null;
	private UUID traceId = null;

	/**
	 * Every request needs a sessionId in the event the client disconnects 
	 * and needs to reconnect with a different server. Possibly we could also
	 * need a clientId.
	 */
	private String sessionId = null;
	private SystemInfo systemInfo = null;
	
	private String endPoint = null;
	private String appRequestAsString = null;
	
	public Request(String sessionId, SystemInfo systemInfo, String endPoint, String appRequestAsString)
	{
		this.createdTime = new Date();
		this.traceId = UUID.randomUUID();

		this.sessionId = sessionId;
		this.systemInfo = systemInfo;
		
		this.endPoint = endPoint;
		this.appRequestAsString = appRequestAsString;
	}

	public Date getCreatedTime()
	{
		return createdTime;
	}

	public UUID getTraceId()
	{
		return traceId;
	}
	
	public String getSessionId()
	{
		return sessionId;
	}
	
	public SystemInfo getSystemInfo()
	{
		return systemInfo;
	}
	
	public String getEndpoint()
	{
		return endPoint;
	}
	
	public String getAppRequestAsString()
	{
		return appRequestAsString;
	}

	@Override
	public String toString()
	{
		return "Request [createdTime=" + createdTime + ", traceId=" + traceId + ", sessionId=" + sessionId + ", systemInfo=" + systemInfo + ", endPoint=" + endPoint + "]";
	}
}
