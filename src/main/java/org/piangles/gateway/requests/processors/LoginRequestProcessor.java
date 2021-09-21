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
 
 
 
package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.backbone.services.auth.AuthenticationType;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.session.SessionDetails;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.LoginRequest;
import org.piangles.gateway.requests.dto.LoginResponse;
import org.piangles.gateway.requests.dto.Request;

public final class LoginRequestProcessor extends AbstractRequestProcessor<LoginRequest, LoginResponse>
{
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public LoginRequestProcessor()
	{
		super(Endpoints.Login, CommunicationPattern.RequestResponse, LoginRequest.class, LoginResponse.class);
	}
	
	/**
	 * The user can login in 2 ways.
	 * 
	 * 1. Traditional Approach : LoginId and Password. Also create a new Session and the SessionManagementService should invalidate any old sessions.
	 * 	However the Authentication Service does a few more features to the mix.
	 * 
	 * 2. Enhanced Approach : LoginId and SessionId. In this approach the SessionId is validated with SessionManagementService. However we do need to 
	 * return AuthResponse and that functionality should not be duplicated here, call needs to be made to Authentication Service. Or we could as well just
	 * pass the Request to AuthenticationService and let it deal with the steps there.
	 * 
	 * AuthenticationResponse
	 * PasswordMismatch,
	 * AccountDisabled,
	 * TooManyTries;
	 * 
	 * Also we need to take care of ChangePassword on first login after user is setup. Not sure where that code is to be.
	 * 
	 */
	@Override
	protected LoginResponse processRequest(ClientDetails clientDetails, Request request, LoginRequest loginRequest) throws Exception
	{
		LoginResponse loginResponse = null;
		if (loginRequest.getId() == null || (loginRequest.getPassword() == null && loginRequest.getSessionId() == null))
		{
			throw new Exception("Invalid LoginRequest request, mandatory fields are absent.");
		}

		if (loginRequest.getPassword() != null) //Authenticate using login and password
		{
			AuthenticationType type = AuthenticationType.valueOf(loginRequest.getAuthenticationType());
			AuthenticationResponse authResponse = authService.authenticate(type, new Credential(loginRequest.getId(), loginRequest.getPassword()));
			
			if (authResponse.isAuthenticated())
			{
				SessionDetails sessionDetails = sessionMgmtService.register(authResponse.getUserId());
				loginResponse = new LoginResponse(authResponse.getUserId(), sessionDetails.getSessionId(), authResponse.IsValidatedByToken());
			}
			else
			{
				loginResponse = new LoginResponse(authResponse.getNoOfAttemptsRemaining(), authResponse.getFailureReason());
			}
		}
		else //Authenticate using login and sessionId
		{
			boolean isSessionValid = sessionMgmtService.isValid(loginRequest.getId(), loginRequest.getSessionId());
			if (isSessionValid)
			{
				loginResponse = new LoginResponse(loginRequest.getId(), loginRequest.getSessionId(), false); 
			}
			else
			{
				loginResponse = new LoginResponse(0, FailureReason.InvalidSession);
			}
		}

		return loginResponse;
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
