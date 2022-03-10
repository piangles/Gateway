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
	
	public LoginResponse processGuestLogin(String userId, ClientDetails clientDetails, SystemInfo systemInfo) throws Exception
	{
		return processRegularLogin(userId, false, true, System.currentTimeMillis(), clientDetails, systemInfo);
	}

	public LoginResponse processRegularLogin(String userId, boolean validatedByToken, boolean loggedInAsGuest, long lastLoggedInTimestamp, ClientDetails clientDetails, SystemInfo systemInfo) throws Exception
	{
		LoginResponse loginResponse = null;
		
		UserDeviceInfo userDeviceInfo = new UserDeviceInfo(userId, clientDetails.getHostName(), clientDetails.getIPAddress(), systemInfo);
		getGatewayDAO().insertUserDeviceInfo(userDeviceInfo);

		SessionDetails sessionDetails = null;
		try
		{
			sessionDetails = sessionMgmtService.register(userId);

			setSessionForCurrentThread(sessionDetails);

			BasicUserProfile userProfile = profileService.getProfile(userId);

			loginResponse = new LoginResponse(userProfile.isMFAEnabled(), validatedByToken, loggedInAsGuest, userId, sessionDetails.getSessionId(),
					userProfile.getPhoneNo(),
					sessionDetails.getInactivityExpiryTimeInSeconds(), lastLoggedInTimestamp);

		}
		catch (SessionManagementException e)
		{
			String message = e.getMessage();
			// TODO THIS HAS TO BE FIXED - Need a better way to figure this out.
			if (message.contains(MAX_ACTIVE_SESSION_MESAGE_1) || message.contains(MAX_ACTIVE_SESSION_MESAGE_2))
			{
				loginResponse = new LoginResponse(0, FailureReason.MaximumSessionCountReached);
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
