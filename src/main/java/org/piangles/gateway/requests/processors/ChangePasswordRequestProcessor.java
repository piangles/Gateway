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
import org.piangles.core.expt.NotFoundException;
import org.piangles.core.expt.ServiceRuntimeException;
import org.piangles.core.expt.ValidationException;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.ChangePasswordRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public class ChangePasswordRequestProcessor extends AbstractRequestProcessor<ChangePasswordRequest, SimpleResponse>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public ChangePasswordRequestProcessor()
	{
		super(Endpoints.ChangePassword, CommunicationPattern.RequestResponse, ChangePasswordRequest.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, ChangePasswordRequest chgPassRequest) throws Exception
	{
		SimpleResponse simpleResponse = null;
		AuthenticationResponse authResponse = authService.changePassword(clientDetails.getSessionDetails().getUserId(), 
																	chgPassRequest.getOldPassword(), chgPassRequest.getNewPassword());
		
		if (RequestRouter.getInstance().getCommunicator() != null)
		{
			RequestRouter.getInstance().getCommunicator().sendPasswordChangeAttemptCommunication(clientDetails, authResponse);
		}
		
		if (authResponse.isRequestSuccessful())
		{
			simpleResponse = new SimpleResponse("Password changed successfully.");
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			authResponse.getFailureMessages().stream().map(msg -> sb.append(msg).append("\n"));
		
			switch(authResponse.getFailureReason())
			{
			case AccountDoesNotExist:
				throw new NotFoundException(sb.toString());
			case PasswordDoesNotMeetStrength:
				throw new ValidationException(sb.toString());
			default:
				throw new ServiceRuntimeException("Unhandled FailureReason : " + authResponse.getFailureReason() + "\n" + sb.toString());
			}
		}
		
		return simpleResponse;
	}
}
