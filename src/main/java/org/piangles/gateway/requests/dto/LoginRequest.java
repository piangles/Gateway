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

public final class LoginRequest
{
	@JsonProperty(required = true, defaultValue="Default")
	private String authenticationType; //Could be one of [Default,TokenBased,Google]
	
	@JsonProperty(required = true)
	private String id; //Could be one of [emailId or phoneNumber, tokenId(Proprietary or from SSO Providers), userId]
	
	@JsonProperty(required = true)
	private String password; 
	
	@JsonProperty(required = false)
	private String sessionId; //When client app reconnects on disconnect this can be used in combo with userId
	
	public LoginRequest(String authenticationType, String id, String password, String sessionId)
	{
		this.authenticationType = authenticationType;
		this.id = id;
		this.password = password;
		this.sessionId = sessionId;
	}

	public String getAuthenticationType()
	{
		return authenticationType;
	}
	
	public String getId()
	{
		return id;
	}

	public String getPassword()
	{
		return password;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	@Override
	public String toString()
	{
		return "LoginRequest [authenticationType=" + authenticationType + ", id=" + id + ", sessionId=" + sessionId + "]";
	}
}
