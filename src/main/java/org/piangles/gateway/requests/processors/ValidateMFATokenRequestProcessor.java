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
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.expt.UnsupportedMediaException;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.ClientStateDeterminator;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.AuthenticationDetails;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.ValidateMFATokenRequest;

public class ValidateMFATokenRequestProcessor extends AbstractRequestProcessor<ValidateMFATokenRequest, AuthenticationDetails>
{
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public ValidateMFATokenRequestProcessor()
	{
		super(Endpoints.ValidateMFAToken, CommunicationPattern.RequestResponse, ValidateMFATokenRequest.class, AuthenticationDetails.class);
	}
	
	@Override
	protected AuthenticationDetails processRequest(ClientDetails clientDetails, Request request, ValidateMFATokenRequest validateMFARequest) throws Exception
	{
		AuthenticationDetails authDetails = null;
		
		if (RequestRouter.getInstance().getMFAManager() != null)
		{
			boolean validation = RequestRouter.getInstance().getMFAManager().validateMFAToken(clientDetails, validateMFARequest.getMFAToken());

			String userId = clientDetails.getSessionDetails().getUserId();
			String sessionId = clientDetails.getSessionDetails().getSessionId();

			BasicUserProfile userProfile = profileService.getProfile(userId);

			String authenticationState = ClientStateDeterminator.determine(validation).name();

			authDetails = new AuthenticationDetails(validation, 
													authenticationState, 
													userId, 
													sessionId,
													userProfile.getPhoneNo(), 
													clientDetails.getInactivityExpiryTimeInSeconds(), clientDetails.getLastLoggedInTimestamp());
			
			sessionMgmtService.updateAuthenticationState(userId, sessionId, authenticationState);
		}
		else
		{
			throw new UnsupportedMediaException("Multi-Factor Authentication has not been setup.");
		}

		return authDetails; 
	}
}
