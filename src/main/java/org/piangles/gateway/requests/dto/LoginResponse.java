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

import org.piangles.backbone.services.auth.FailureReason;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class LoginResponse extends AuthenticationDetails
{
	@JsonProperty(required = true)
	private int noOfAttemptsRemaining = 0;
	
	@JsonProperty(required = false)
	private FailureReason failureReason = null;
	
	@JsonProperty(required = true)
	private boolean loggedInAsGuest = false; 

	public LoginResponse(	String authenticationState, 
							boolean loggedInAsGuest,
							String userId, String sessionId, String phoneNo, 
							long inactivityExpiryTimeInSeconds, long lastLoggedInTimestamp)
	{
		super(	true,  
				authenticationState,
				userId, sessionId, phoneNo, inactivityExpiryTimeInSeconds, lastLoggedInTimestamp);
		this.loggedInAsGuest = loggedInAsGuest;
	}

	public LoginResponse(int noOfAttemptsRemaining, FailureReason failureReason, String authenticationState)
	{
		this.noOfAttemptsRemaining = noOfAttemptsRemaining;
		this.failureReason = failureReason;
		
		super.setAuthenticationState(authenticationState);
	}
	
	public int getNoOfAttemptsRemaining()
	{
		return noOfAttemptsRemaining;
	}

	public FailureReason getFailureReason()
	{
		return failureReason;
	}
	
	public boolean isLoggedInAsGuest()
	{
		return loggedInAsGuest;
	}

	@Override
	public String toString()
	{
		return "LoginResponse [authenticated=" + isAuthenticated() + ", authenticationState=" + getAuthenticationState() + ", userId=" + getUserId() + ", sessionId=" + getSessionId() + ", noOfAttemptsRemaining="
				+ noOfAttemptsRemaining + ", failureReason=" + failureReason + "]";
	}
}
