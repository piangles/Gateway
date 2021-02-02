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
 
 
 
package org.piangles.gateway.requests;

import javax.xml.bind.ValidationException;

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
 * Should even the request have SessionId?
 */
public class RequestValidator
{
	/**
	 * Client needs to send
	 * 1. traceId
	 * 2. sessionId for every request except LoginRequest
	 * 
	 * Move ValidationException or validation interface and Exception to Core.
	 * 
	 * DO NOT Check in this class till validation is complete
	 */
	public static void validate(ClientDetails clientDetails, Request request) throws ValidationException
	{
		
	}
}
