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
 
 
package org.piangles.gateway.requests.validators;

import org.apache.commons.lang3.StringUtils;
import org.piangles.core.expt.ValidationException;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.LoginRequest;
import org.piangles.gateway.requests.dto.Request;

public class LoginRequestValidator extends AbstractRequestValidator<LoginRequest>
{
	public LoginRequestValidator()
	{
		super(Endpoints.Login);
	}
	
	@Override
	public void validate(ClientDetails clientDetails, Request request, LoginRequest loginRequest) throws ValidationException
	{
		if (StringUtils.isBlank(loginRequest.getId()) || StringUtils.isAllBlank(loginRequest.getPassword(), loginRequest.getSessionId()))
		{
			throw new ValidationException("Invalid LoginRequest, mandatory fields (id and [password or sessionId]) are missing.");
		}
	}
}
