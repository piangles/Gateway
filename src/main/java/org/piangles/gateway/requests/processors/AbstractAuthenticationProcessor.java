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
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.backbone.services.session.SessionDetails;
import org.piangles.backbone.services.session.SessionManagementException;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.ClientStateDeterminator;
import org.piangles.gateway.requests.dao.UserDeviceInfo;
import org.piangles.gateway.requests.dto.LoginResponse;
import org.piangles.gateway.requests.dto.SystemInfo;

public abstract class AbstractAuthenticationProcessor<EndpointReq, EndpointResp> extends AbstractRequestProcessor<EndpointReq, EndpointResp>
{
	private static final String MAX_ACTIVE_SESSION_MESAGE_1 = "already has an active session";
	private static final String MAX_ACTIVE_SESSION_MESAGE_2 = "has reached maximum active sessions";
	
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public AbstractAuthenticationProcessor(Enum<?> endpoint, Class<EndpointReq> requestClass, Class<EndpointResp> responseClass)
	{
		super(endpoint, CommunicationPattern.RequestResponse, requestClass, responseClass);
	}
	
	public LoginResponse processGuestSuccessfulLogin(String userId, ClientDetails clientDetails, SystemInfo systemInfo) throws Exception
	{
		boolean authenticatedByToken = false;
		boolean loggedInAsGuest = true;
		
		return processRegularSuccessfulLogin(userId, authenticatedByToken, loggedInAsGuest, System.currentTimeMillis(), clientDetails, systemInfo);
	}

	public LoginResponse processRegularSuccessfulLogin(String userId, boolean authenticatedByToken, boolean loggedInAsGuest, long lastLoggedInTimestamp, ClientDetails clientDetails, SystemInfo systemInfo) throws Exception
	{
		LoginResponse loginResponse = null;
		
		UserDeviceInfo userDeviceInfo = new UserDeviceInfo(userId, clientDetails.getHostName(), clientDetails.getIPAddress(), systemInfo);
		getGatewayDAO().insertUserDeviceInfo(userDeviceInfo);

		try
		{
			/**
			 * We only get here if the user has 
			 * 1. Typed Password or TempPassword/Token.
			 * 2. Choose to Proceed as a Guest.
			 * Either which way => authenticatedBySession is false and authenticatedByMultiFactor is also false.
			 */
			SessionDetails sessionDetails = sessionMgmtService.register(userId);
			setSessionForCurrentThread(sessionDetails);

			BasicUserProfile userProfile = profileService.getProfile(userId);

			String authenticationState = ClientStateDeterminator.determine(authenticatedByToken, userProfile).name();

			loginResponse = new LoginResponse(	authenticationState, 
												loggedInAsGuest, userId, sessionDetails.getSessionId(),
												userProfile.getPhoneNo(),
												sessionDetails.getInactivityExpiryTimeInSeconds(), lastLoggedInTimestamp);
			
			sessionMgmtService.updateAuthenticationState(userId, sessionDetails.getSessionId(), authenticationState);
		}
		catch (SessionManagementException e)
		{
			String message = e.getMessage();
			// TODO THIS HAS TO BE FIXED - Need a better way to figure this out.
			if (message.contains(MAX_ACTIVE_SESSION_MESAGE_1) || message.contains(MAX_ACTIVE_SESSION_MESAGE_2))
			{
				String authenticationState = ClientStateDeterminator.determine().name();
				
				loginResponse = new LoginResponse(0, FailureReason.MaximumSessionCountReached, authenticationState);
			}
			else
			{
				throw e;
			}
		}
		
		return loginResponse;
	}
	
	@Override
	public final boolean shouldValidateSession()
	{
		return false;
	}
}
