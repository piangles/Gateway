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
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.BooleanResponse;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.UserProfileExistsRequest;

import software.amazon.awssdk.utils.StringUtils;

public class UserProfileExistsRequestProcessor extends AbstractRequestProcessor<UserProfileExistsRequest, BooleanResponse>
{
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public UserProfileExistsRequestProcessor()
	{
		super(Endpoints.UserProfileExists, UserProfileExistsRequest.class, BooleanResponse.class);
	}
	
	@Override
	protected BooleanResponse processRequest(ClientDetails clientDetails, Request request, UserProfileExistsRequest userProfileExistsRequest) throws Exception
	{
		BooleanResponse response = null;
		
		String userId = profileService.searchProfile(new BasicUserProfile(null, null, userProfileExistsRequest.getEmailId(), userProfileExistsRequest.getPhoneNo()));
		
		if (StringUtils.isNotBlank(userId))
		{
			response = new BooleanResponse(true, "Email/Phone is already registered."); 	
		}
		else
		{
			response = new BooleanResponse(false, "Email/Phone is not registered.");
		}
		
		return response; 
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}