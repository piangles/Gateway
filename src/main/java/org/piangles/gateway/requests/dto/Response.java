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

public final class Response
{
	private long issuedTime;
	private long requestTransitTime;
	private long requestProcessingTime;
	private UUID traceId = null;

	/**
	 * Endpoint is required in response because when the client
	 * receieves the reponse back, it will make it easier for the 
	 * client to decode the appResponseAsString message to the client's
	 * implementation of the class.
	 */
	private String endpoint = null;
	
	private StatusCode statusCode = null;

	/**
	 * requestSucessfull is a reflection of is the request was processed
	 * successfully without any exception. Not if the actual service accepted
	 * the request. Ex : LoginRequest even if failed authentication will still
	 * return requestSuccessful = true.
	 */
	private boolean requestSuccessful = false;

	private String errorMessage = null;
	
	private String endpointResponse = null;

	public Response(StatusCode statusCode, String endpointResponse)
	{
		this(null, null, 0, 0, statusCode, endpointResponse);
	}
	
	public Response(UUID traceId, String endpoint, long requestReceiptTime, long requestTransitTime, StatusCode statusCode, String endpointResponse)
	{
		this.issuedTime = System.currentTimeMillis();
		this.requestTransitTime = requestTransitTime;
		this.requestProcessingTime = System.currentTimeMillis() - requestReceiptTime;
		
		this.traceId = traceId;
		this.endpoint = endpoint;

		this.statusCode = statusCode;
		this.requestSuccessful = (this.statusCode == StatusCode.Success)? true : false;
		if (requestSuccessful)
		{
			this.endpointResponse = endpointResponse; 
		}
		else
		{
			this.errorMessage = endpointResponse; 
		}
	}
	
	public long getIssuedTime()
	{
		return issuedTime;
	}

	public long getRequestTransitTime()
	{
		return requestTransitTime;
	}

	public long getRequestProcessingTime()
	{
		return requestProcessingTime;
	}
	
	public UUID getTraceId()
	{
		return traceId;
	}
	
	public String getEndpoint()
	{
		return endpoint;
	}
	
	public StatusCode getStatusCode()
	{
		return statusCode;
	}

	public boolean isRequestSuccessful()
	{
		return requestSuccessful;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public String getEndpointResponse()
	{
		return endpointResponse;
	}

	@Override
	public String toString()
	{
		return "Response [issuedTime=" + issuedTime + ", requestTransitTime=" + requestTransitTime + ", requestProcessingTime=" + requestProcessingTime + ", traceId=" + traceId + ", endpoint="
				+ endpoint + ", requestSuccessful=" + requestSuccessful + ", statusCode=" + statusCode + ", errorMessage=" + errorMessage + "]";
	}
}
