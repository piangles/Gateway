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

import org.piangles.core.expt.ValidationException;
import org.piangles.core.util.validate.Validator;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

public final class DefaultGatewayRequestValidator<EndpointReq> implements Validator
{
	private String name = null;
	private Class<EndpointReq> requestClass = null;
	
	public DefaultGatewayRequestValidator(Class<EndpointReq> requestClass)
	{
		this.name = "GatewayRequest";
		this.requestClass = requestClass;
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@SuppressWarnings({"unchecked", "unused"})
	@Override
	public void validate(Object ... objects) throws ValidationException
	{
		ClientDetails clientDetails = (ClientDetails)objects[0]; 
		Request request = (Request)objects[1];
		EndpointReq epRequest = (EndpointReq)objects[2];
		
		/**
		 * One of the checks here should be SessionId the client sends
		 * should be the same as the one assigned by the server.
		 * This may seems double check at this point because we are already
		 * checking in Service call in org.piangles.core.services.remoting.rabbit.RequestProcessingThread
		 * In reality, Services are very important to be avaiable and validation can prevent
		 * any calls to Service the better.
		 * 
		 */
		if (!EmptyRequest.class.equals(requestClass) && epRequest == null)
		{
			throw new ValidationException("EndpointRequest cannot be empty(null/blanks) for this endpoint.");
		}
	}
}
