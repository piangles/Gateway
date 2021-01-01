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

public final class LoginResponse
{
	private boolean authenticated = false;
	private boolean authenticatedByToken = false; 
	private String userId;
	private String sessionId;
	private int noOfAttemptsRemaining = 0;
	private String failureReason = null;

	public LoginResponse(int noOfAttemptsRemaining, String failureReason)
	{
		this.noOfAttemptsRemaining = noOfAttemptsRemaining;
		this.failureReason = failureReason;
	}
	
	public LoginResponse(String userId, String sessionId, boolean authenticatedByToken)
	{
		this.authenticated = true;
		this.userId = userId;
		this.sessionId = sessionId;
		this.authenticatedByToken = authenticatedByToken;
	}

	public boolean isAuthenticated()
	{
		return authenticated;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getSessionId()
	{
		return sessionId;
	}
	
	public boolean isAuthenticatedByToken()
	{
		return authenticatedByToken;
	}

	public int getNoOfAttemptsRemaining()
	{
		return noOfAttemptsRemaining;
	}

	public String getFailureReason()
	{
		return failureReason;
	}

	@Override
	public String toString()
	{
		return "LoginResponse [authenticated=" + authenticated + ", authenticatedByToken=" + authenticatedByToken + ", userId=" + userId + ", sessionId=" + sessionId + ", noOfAttemptsRemaining="
				+ noOfAttemptsRemaining + ", failureReason=" + failureReason + "]";
	}
}
