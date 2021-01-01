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

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.backbone.services.auth.AuthenticationType;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SignUpRequest;
import org.piangles.gateway.requests.dto.SimpleResponse;

public final class SignUpRequestProcessor extends AbstractRequestProcessor<SignUpRequest, SimpleResponse>
{
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();

	public SignUpRequestProcessor()
	{
		super(Endpoints.SignUp, CommunicationPattern.RequestResponse, SignUpRequest.class, SimpleResponse.class);
	}

	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, SignUpRequest signupRequest) throws Exception
	{
		SimpleResponse response = null;
		AuthenticationResponse authResponse = null;

		if (StringUtils.isBlank(signupRequest.getEmailId()) || StringUtils.isBlank(signupRequest.getPassword()))
		{
			throw new Exception("Invalid SignUp request, mandatory fields are absent.");
		}

		 if (signupRequest.getEmailId().equals(signupRequest.getPassword()))
		 {
			 authResponse = new AuthenticationResponse(FailureReason.PasswordDoesNotMeetStrength, new ArrayList<String>());
			 authResponse.getFailureMessages().add("LoginId and Password cannot be the same.");
		 }
		 else
		{
			authResponse = authService.validatePasswordStrength(signupRequest.getPassword());
			if (authResponse.isRequestSuccessful())
			{
				String userId = profileService.createProfile(new BasicUserProfile(signupRequest.getFirstName(), signupRequest.getLastName(), signupRequest.getEmailId(), signupRequest.getPhoneNo()));

				authResponse = authService.createAuthenticationEntry(AuthenticationType.Default, userId, new Credential(signupRequest.getEmailId(), signupRequest.getPassword()));

				response = new SimpleResponse(authResponse.isRequestSuccessful());
			}
			else
			{
				StringBuffer sb = new StringBuffer();
				authResponse.getFailureMessages().stream().map(msg -> sb.append(msg).append("\n"));
				response = new SimpleResponse(false, sb.toString());
			}
		}
		return response;
	}

	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
