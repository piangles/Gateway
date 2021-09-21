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

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleResponse
{
	private static final String SUCCESS_MESSAGE = "Request was successfully processed.";
	private static final String FAILURE_MESSAGE = "Request failed to be processed.";
	
	@JsonProperty(required = true)
	private boolean requestSuccessful;
	
	@JsonProperty(required = true)
	private String message;
	
	public SimpleResponse(boolean requestSuccessful)
	{
		this(requestSuccessful, requestSuccessful?SUCCESS_MESSAGE:FAILURE_MESSAGE);
	}

	public SimpleResponse(boolean requestSuccessful, String message)
	{
		this.requestSuccessful = requestSuccessful;
		this.message = message;
	}

	public boolean isRequestSuccessful()
	{
		return requestSuccessful;
	}

	public String getMessage()
	{
		return message;
	}

	@Override
	public String toString()
	{
		return "SimpleResponse [requestSuccessful=" + requestSuccessful + ", message=" + message + "]";
	}
}
