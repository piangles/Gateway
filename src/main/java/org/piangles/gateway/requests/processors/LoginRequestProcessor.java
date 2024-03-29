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

import org.apache.commons.lang3.StringUtils;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.backbone.services.auth.AuthenticationType;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.backbone.services.session.SessionDetails;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.ClientStateDeterminator;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.LoginRequest;
import org.piangles.gateway.requests.dto.LoginResponse;
import org.piangles.gateway.requests.dto.Request;

public final class LoginRequestProcessor extends AbstractAuthenticationProcessor<LoginRequest, LoginResponse>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public LoginRequestProcessor()
	{
		super(Endpoints.Login, LoginRequest.class, LoginResponse.class);
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

		if (StringUtils.isNotBlank(loginRequest.getPassword())) //Authenticate using login and password
		{
			AuthenticationType type = AuthenticationType.valueOf(loginRequest.getAuthenticationType());
			AuthenticationResponse authResponse = authService.authenticate(type, new Credential(loginRequest.getId(), loginRequest.getPassword()));
			
			if (authResponse.isAuthenticated())
			{
				loginResponse = processRegularSuccessfulLogin(authResponse.getUserId(), authResponse.IsValidatedByToken(), false, authResponse.getLastLoggedInTimestamp(),clientDetails, loginRequest.getSystemInfo());
			}
			else
			{
				String authenticationState = ClientStateDeterminator.determine().name();
				
				loginResponse = new LoginResponse(authResponse.getNoOfAttemptsRemaining(), authResponse.getFailureReason(), authenticationState);
			}
		}
		else //Authenticate using userId and sessionId
		{
			boolean isSessionValid = sessionMgmtService.isValid(loginRequest.getId(), loginRequest.getSessionId());
			if (isSessionValid)
			{
				sessionMgmtService.makeLastAccessedCurrent(loginRequest.getId(), loginRequest.getSessionId());

				SessionDetails sessionDetails = sessionMgmtService.getSessionDetails(loginRequest.getId(), loginRequest.getSessionId());
				setSessionForCurrentThread(sessionDetails);
				
				BasicUserProfile userProfile = profileService.getProfile(loginRequest.getId());
				boolean authEntryExists = authService.doesAuthenticationEntryExist(loginRequest.getId());
				boolean loggedInAsGuest = !authEntryExists;

				loginResponse = new LoginResponse(	sessionDetails.getAuthenticationState(), 
													loggedInAsGuest, loginRequest.getId(), loginRequest.getSessionId(), userProfile.getPhoneNo(),
													sessionDetails.getInactivityExpiryTimeInSeconds(), 
													0); //It is Zero here because, this is not Login it is Authentication via userId and sessionId
				
				loginResponse.markAuthenticatedBySession();
			}
			else
			{
				String authenticationState = ClientStateDeterminator.determine().name();
				
				loginResponse = new LoginResponse(0, FailureReason.InvalidSession, authenticationState);
			}
		}

		return loginResponse;
	}
}
