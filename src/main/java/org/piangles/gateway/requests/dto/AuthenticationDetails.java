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

public class AuthenticationDetails
{
	@JsonProperty(required = true)
	private boolean authenticated = false;

	@JsonProperty(required = true)
	private String authenticationState = null;

	@JsonProperty(required = true)
	private boolean authenticatedBySession = false;

	@JsonProperty(required = true)
	private String userId;
	
	@JsonProperty(required = true)
	private String sessionId;
	
	@JsonProperty(required = false)
	private String phoneNo = null;
	
	@JsonProperty(required = true)
	private long inactivityExpiryTimeInSeconds = 0L;

	@JsonProperty(required = true)
	private long lastLoggedInTimestamp = 0L;

	public AuthenticationDetails()
	{
		
	}

	public AuthenticationDetails(	boolean authenticated, 
									String authenticationState,
									String userId, String sessionId, String phoneNo,
									long inactivityExpiryTimeInSeconds, long lastLoggedInTimestamp)
	{
		this.authenticated = authenticated;
		this.authenticationState = authenticationState;
		this.userId = userId;
		this.sessionId = sessionId;
		this.phoneNo = phoneNo;
		this.inactivityExpiryTimeInSeconds = inactivityExpiryTimeInSeconds;
		this.lastLoggedInTimestamp = lastLoggedInTimestamp;
	}

	public final boolean isAuthenticated()
	{
		return authenticated;
	}

	public String getAuthenticationState()
	{
		return authenticationState;
	}

	public boolean isAuthenticatedBySession()
	{
		return authenticatedBySession;
	}
	
	public final String getUserId()
	{
		return userId;
	}

	public final String getSessionId()
	{
		return sessionId;
	}
	
	public String getPhoneNo()
	{
		return phoneNo;
	}
	
	public long getInactivityExpiryTimeInSeconds()
	{
		return inactivityExpiryTimeInSeconds;
	}
	
	public long getLastLoggedInTimestamp()
	{
		return lastLoggedInTimestamp;
	}

	public void markAuthenticatedBySession()
	{
		authenticatedBySession = true;
	}

	protected final void setAuthenticationState(String authenticationState)
	{
		this.authenticationState = authenticationState;
	}
}
