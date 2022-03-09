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
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileException;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.core.expt.UnsupportedMediaException;
import org.piangles.core.expt.ValidationException;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

import software.amazon.awssdk.utils.StringUtils;

public class SendMFATokenRequestProcessor extends AbstractRequestProcessor<BasicUserProfile, SimpleResponse>
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public SendMFATokenRequestProcessor()
	{
		super(Endpoints.SendMFAToken, BasicUserProfile.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, BasicUserProfile upRequest) throws Exception
	{
		SimpleResponse simpleResponse = null;
		
		if (RequestRouter.getInstance().getMFAManager() != null)
		{
			BasicUserProfile userProfile = profileService.getProfile(clientDetails.getSessionDetails().getUserId());
			
			if (StringUtils.isBlank(userProfile.getPhoneNo()))
			{
				logger.info("User with UserId: " + userProfile.getUserId() + " requesting a Token for setting up MFA.");
				
				updateUserProfile(userProfile, upRequest.getPhoneNo());
			}
			else if (StringUtils.isNotBlank(upRequest.getPhoneNo()))
			{
				if (!userProfile.getPhoneNo().equals(upRequest.getPhoneNo()))
				{
					logger.info("User with UserId: " + userProfile.getUserId() + " requesting a Token for setting up MFA for updated phone number.");

					updateUserProfile(userProfile, upRequest.getPhoneNo());
				}
				else
				{
					logger.info("User with UserId: " + userProfile.getUserId() + " requesting a MFA Token for a previously registered phone number.");
				}
			}
			else
			{
				logger.info("User with UserId: " + userProfile.getUserId() + " requesting a Resend->MFAToken.");
			}


			RequestRouter.getInstance().getMFAManager().sendMFAToken(clientDetails);
			
			simpleResponse = new SimpleResponse("Please check your registered device for the token.");
		}
		else
		{
			throw new UnsupportedMediaException("Multi-Factor Authentication has not been setup.");
		}

		return simpleResponse; 
	}
	
	private void updateUserProfile(BasicUserProfile userProfile, String phoneNo) throws UserProfileException
	{
		if (StringUtils.isBlank(phoneNo))
		{
			throw new ValidationException(Endpoints.SendMFAToken.name() + " Phone Number is blank.");
		}
		
		phoneNo = phoneNo.trim();
		
		userProfile = new BasicUserProfile(	userProfile.getUserId(), userProfile.getFirstName(), userProfile.getLastName(), 
											userProfile.getEMailId(), userProfile.isEmailIdVerified(),
											phoneNo, false,
											false
											);	

		profileService.updateProfile(userProfile.getUserId(), userProfile);
	}
}
