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
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.GenerateTokenRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public final class GenerateTokenRequestProcessor extends AbstractRequestProcessor<GenerateTokenRequest, SimpleResponse>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public GenerateTokenRequestProcessor()
	{
		super(Endpoints.GenerateResetToken, CommunicationPattern.RequestResponse, GenerateTokenRequest.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, GenerateTokenRequest tokenRequest) throws Exception
	{
		boolean result = true;
		String message = "Please check your registered email for the token.";
		
		AuthenticationResponse response = authService.generateResetToken(tokenRequest.getEmailId());
		result = response.isRequestSuccessful();
		if (!result)
		{
			message = "Could not generate a reset token.";
		}
		
		return new SimpleResponse(result, message);
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}