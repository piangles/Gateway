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

public final class EndpointMetadata
{
	private String endpoint;
	private String description;
	private String communicationPattern;
	private boolean validSessionNeeded;
	private String requestSchema;
	private String responseSchema; //TODO Would need Stream /Event details 
	
	public EndpointMetadata(String endpoint, String description, String communicationPattern, boolean validSessionNeeded, String requestSchema, String responseSchema)
	{
		this.endpoint = endpoint;
		this.description = description;
		this.communicationPattern = communicationPattern;
		this.validSessionNeeded = validSessionNeeded;
		this.requestSchema = requestSchema;
		this.responseSchema = responseSchema;
	}

	public String getEndpoint()
	{
		return endpoint;
	}

	public String getDescription()
	{
		return description;
	}

	public String getCommunicationPattern()
	{
		return communicationPattern;
	}

	public boolean isValidSessionNeeded()
	{
		return validSessionNeeded;
	}

	public String getRequestSchema()
	{
		return requestSchema;
	}

	public String getResponseSchema()
	{
		return responseSchema;
	}
}
