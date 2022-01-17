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
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.backbone.services.session.SessionDetails;
import org.piangles.backbone.services.session.SessionManagementException;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dao.UserDeviceInfo;
import org.piangles.gateway.requests.dto.LoginRequest;
import org.piangles.gateway.requests.dto.LoginResponse;
import org.piangles.gateway.requests.dto.Request;

public final class LoginRequestProcessor extends AbstractRequestProcessor<LoginRequest, LoginResponse>
{
	private static final String MAX_ACTIVE_SESSION_MESAGE_1 = "already has an active session";
	private static final String MAX_ACTIVE_SESSION_MESAGE_2 = "has reached maximum active sessions";
	
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
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

		if (loginRequest.getPassword() != null) //Authenticate using login and password
		{
			AuthenticationType type = AuthenticationType.valueOf(loginRequest.getAuthenticationType());
			AuthenticationResponse authResponse = authService.authenticate(type, new Credential(loginRequest.getId(), loginRequest.getPassword()));
			
			if (authResponse.isAuthenticated())
			{
				UserDeviceInfo userDeviceInfo = new UserDeviceInfo(authResponse.getUserId(), 
																	clientDetails.getHostName(), clientDetails.getIPAddress(),
																	loginRequest.getSystemInfo());
				getGatewayDAO().insertUserDeviceInfo(userDeviceInfo);
				
				SessionDetails sessionDetails = null;
				try
				{
					sessionDetails = sessionMgmtService.register(authResponse.getUserId());
					
					setSessionForCurrentThread(sessionDetails);
					
					BasicUserProfile userProfile = profileService.getProfile(authResponse.getUserId());
					
					loginResponse = new LoginResponse(userProfile.isMFAEnabled(), authResponse.IsValidatedByToken(), authResponse.getUserId(), 
							sessionDetails.getSessionId(), 
							sessionDetails.getInactivityExpiryTimeInSeconds(), authResponse.getLastLoggedInTimestamp());
					
					
				}
				catch(SessionManagementException e)
				{
					String message = e.getMessage();
					//TODO THIS HAS TO BE FIXED - Need a better way to figure this out.
					if (message.contains(MAX_ACTIVE_SESSION_MESAGE_1) || message.contains(MAX_ACTIVE_SESSION_MESAGE_2))
					{
						loginResponse = new LoginResponse(authResponse.getNoOfAttemptsRemaining(), FailureReason.MaximumSessionCountReached);
					}
					else
					{
						throw e;
					}
				}
			}
			else
			{
				loginResponse = new LoginResponse(authResponse.getNoOfAttemptsRemaining(), authResponse.getFailureReason());
			}
		}
		else //Authenticate using userId and sessionId
		{
			boolean isSessionValid = sessionMgmtService.isValid(loginRequest.getId(), loginRequest.getSessionId());
			if (isSessionValid)
			{
				sessionMgmtService.makeLastAccessedCurrent(loginRequest.getId(), loginRequest.getSessionId());
				//Do not have to do MFA on userId/sessionId authentication
				loginResponse = new LoginResponse(false, false, loginRequest.getId(), loginRequest.getSessionId(), 
													900, 0);  //TODO THIS HAS TO BE FIXED 
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
