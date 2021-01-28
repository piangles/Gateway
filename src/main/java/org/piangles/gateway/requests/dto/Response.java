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

import java.util.Date;
import java.util.UUID;

public final class Response
{
	private Date issuedTime = null;
	private long requestTransitTime;
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

	public Response(UUID traceId, String endpoint, long requestTransitTime, boolean requestSuccessful, String payload)
	{
		this.issuedTime = new Date();
		this.requestTransitTime = requestTransitTime;
		
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
	
	public Date getIssuedTime()
	{
		return issuedTime;
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
	
	public long getRequestTransitTime()
	{
		return requestTransitTime;
	}

	@Override
	public String toString()
	{
		return "Response [issuedTime=" + issuedTime + ", requestTransitTime=" + requestTransitTime + ", traceId=" + traceId + ", requestSuccessful=" + requestSuccessful + ", errorMessage=" + errorMessage + "]";
	}
}
