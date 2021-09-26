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
import org.piangles.gateway.requests.dto.Request;

/**
 * One of the checks here should be SessionId the client sends
 * should be the same as the one assigned by the server.
 * This may seems double check at this point because we are already
 * checking in Service call in org.piangles.core.services.remoting.rabbit.RequestProcessingThread
 * In reality, Services are very important to be avaiable and validation can prevent
 * any calls to Service the better.
 * 
 */
public abstract class AbstractRequestValidator<EndpointReq> implements Validator
{
	private String name = null;
	
	public AbstractRequestValidator(Enum<?> name)
	{
		this.name = name.name();
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void validate(Object ... objects) throws ValidationException
	{
		validate((ClientDetails)objects[0], (Request)objects[1], (EndpointReq)objects[2]);
	}
	public abstract void validate(ClientDetails clientDetails, Request request, EndpointReq epRequest) throws ValidationException;
}
