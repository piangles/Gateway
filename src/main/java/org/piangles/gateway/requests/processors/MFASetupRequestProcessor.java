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
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.BooleanResponse;
import org.piangles.gateway.requests.dto.MFASetupRequest;
import org.piangles.gateway.requests.dto.Request;

public class MFASetupRequestProcessor extends AbstractRequestProcessor<MFASetupRequest, BooleanResponse>
{
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public MFASetupRequestProcessor()
	{
		super(Endpoints.MFASetup, MFASetupRequest.class, BooleanResponse.class);
	}

	@Override
	protected BooleanResponse processRequest(ClientDetails clientDetails, Request request, MFASetupRequest mfaSetupRequest) throws Exception
	{
		BooleanResponse booleanResponse = null;
		
		BasicUserProfile userProfile = profileService.getProfile(clientDetails.getSessionDetails().getUserId());
		
		if (mfaSetupRequest.isEnabled())
		{
			if (RequestRouter.getInstance().getMFAManager() != null)
			{
				boolean validation = RequestRouter.getInstance().getMFAManager().validateMFAToken(clientDetails, mfaSetupRequest.getToken());
				
				userProfile = new BasicUserProfile(	userProfile.getUserId(), userProfile.getFirstName(), userProfile.getLastName(), 
													userProfile.getEMailId(), userProfile.isEmailIdVerified(),
													userProfile.getPhoneNo(), validation,
													validation
													);	

				if (validation)
				{
					sessionMgmtService.markAuthenticatedByMFA(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());
				}
				
				booleanResponse = new BooleanResponse(validation, validation? "MFA Enabled" : "Invalid MFA Token");
			}
			else
			{
				throw new UnsupportedMediaException("Multi-Factor Authentication has not been setup.");
			}
		}
		else
		{
			if (RequestRouter.getInstance().getMFAManager() != null)
			{
				boolean validation = RequestRouter.getInstance().getMFAManager().validateMFAToken(clientDetails, mfaSetupRequest.getToken());
				
				if (validation)
				{
					userProfile = new BasicUserProfile(	userProfile.getUserId(), userProfile.getFirstName(), userProfile.getLastName(), 
							userProfile.getEMailId(), userProfile.isEmailIdVerified(),
							userProfile.getPhoneNo(), false,
							false
							);	

					booleanResponse = new BooleanResponse(true, "MFA Disabled");
				}
				else
				{
					booleanResponse = new BooleanResponse(false, "Invalid MFA Token");
				}
			}
			else
			{
				throw new UnsupportedMediaException("Multi-Factor Authentication has not been setup.");
			}
		}
		
		if (booleanResponse.isCriteriaSatisfied())
		{
			profileService.updateProfile(clientDetails.getSessionDetails().getUserId(), userProfile);
			
			if (RequestRouter.getInstance().getCommunicator() != null)
			{
				RequestRouter.getInstance().getCommunicator().sendMFASetupCommunication(clientDetails, userProfile);
			}
		}
		
		return booleanResponse;
	}
}
