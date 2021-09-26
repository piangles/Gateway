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
 
 
 
package org.piangles.gateway.requests.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Request
{
	@JsonProperty(required = true)
	private long issuedTime;
	
	@JsonIgnore
	private long receiptTime;
	@JsonIgnore
	private long transitTime;
	
	@JsonProperty(required = true)
	private UUID traceId = null;

	/**
	 * Every request needs a sessionId in the event the client disconnects 
	 * and needs to reconnect with a different server. Possibly we could also
	 * need a clientId.
	 */
	@JsonProperty(required = true)
	private String sessionId = null;
	
	@JsonProperty(required = true)
	private String endPoint = null;
	
	@JsonProperty(required = true)
	private String endpointRequest = null;
	
	@JsonProperty(required = false)
	private SystemInfo systemInfo = null;
	
	public Request(String sessionId, String endPoint, String endpointRequest, SystemInfo systemInfo)
	{
		this.issuedTime = System.currentTimeMillis();
		this.traceId = UUID.randomUUID();

		this.sessionId = sessionId;
		
		this.endPoint = endPoint;
		this.endpointRequest = endpointRequest;
		
		this.systemInfo = systemInfo;
	}

	public long getIssuedTime()
	{
		return issuedTime;
	}
	
	public long getReceiptTime()
	{
		return receiptTime;
	}

	public void markTransitTime()
	{
		receiptTime = System.currentTimeMillis();
		transitTime = receiptTime - issuedTime; 
	}
	
	public long getTransitTime()
	{
		return transitTime;
	}

	public UUID getTraceId()
	{
		return traceId;
	}
	
	public String getSessionId()
	{
		return sessionId;
	}
	
	public String getEndpoint()
	{
		return endPoint;
	}
	
	public String getEndpointRequest()
	{
		return endpointRequest;
	}

	public SystemInfo getSystemInfo()
	{
		return systemInfo;
	}
	
	@Override
	public String toString()
	{
		return "Request [issuedTime=" + issuedTime + ", traceId=" + traceId + ", sessionId=" + sessionId + ", systemInfo=" + systemInfo + ", endPoint=" + endPoint + "]";
	}
}
