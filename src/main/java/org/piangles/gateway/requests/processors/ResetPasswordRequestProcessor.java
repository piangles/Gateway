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
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.core.expt.NotFoundException;
import org.piangles.core.expt.ServiceRuntimeException;
import org.piangles.core.expt.ValidationException;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.ClientStateDeterminator;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.AuthenticationDetails;
import org.piangles.gateway.requests.dto.ChangePasswordRequest;
import org.piangles.gateway.requests.dto.Request;

public class ResetPasswordRequestProcessor extends AbstractRequestProcessor<ChangePasswordRequest, AuthenticationDetails>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public ResetPasswordRequestProcessor()
	{
		super(Endpoints.ResetPassword, CommunicationPattern.RequestResponse, ChangePasswordRequest.class, AuthenticationDetails.class);
	}
	
	@Override
	protected AuthenticationDetails processRequest(ClientDetails clientDetails, Request request, ChangePasswordRequest chgPassRequest) throws Exception
	{
		AuthenticationDetails authDetails = null;
		AuthenticationResponse authResponse = authService.changePassword(clientDetails.getSessionDetails().getUserId(), 
																	chgPassRequest.getOldPassword(), chgPassRequest.getNewPassword());
		
		if (RequestRouter.getInstance().getCommunicator() != null)
		{
			RequestRouter.getInstance().getCommunicator().sendPasswordChangeAttemptCommunication(clientDetails, authResponse);
		}
		
		if (authResponse.isRequestSuccessful())
		{
			String userId = clientDetails.getSessionDetails().getUserId();
			String sessionId = clientDetails.getSessionDetails().getSessionId();
			
			BasicUserProfile userProfile = profileService.getProfile(clientDetails.getSessionDetails().getUserId());
			String authenticationState = ClientStateDeterminator.determine(false, userProfile).name();
			
			authDetails = new AuthenticationDetails(true, 
													authenticationState, 
													userId, 
													sessionId,
													userProfile.getPhoneNo(), 
													clientDetails.getInactivityExpiryTimeInSeconds(), clientDetails.getLastLoggedInTimestamp());
			
			sessionMgmtService.updateAuthenticationState(userId, sessionId, authenticationState);
		}
		else
		{
			StringBuffer sb = new StringBuffer(authResponse.getFailureReason().name());
			authResponse.getFailureMessages().stream().map(msg -> sb.append(msg).append("\n"));
		
			switch(authResponse.getFailureReason())
			{
			case AccountDoesNotExist:
				throw new NotFoundException(sb.toString());
			case PasswordDoesNotMeetStrength:
				throw new ValidationException(sb.toString());
			case OldPasswordDoesNotMatch:
				throw new ValidationException(sb.toString());
			default:
				throw new ServiceRuntimeException("Unhandled FailureReason : " + authResponse.getFailureReason() + "\n" + sb.toString());
			}
		}
		
		return authDetails;
	}
}
