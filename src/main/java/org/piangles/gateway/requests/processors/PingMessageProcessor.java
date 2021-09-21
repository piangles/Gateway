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

import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.Ping;
import org.piangles.gateway.requests.dto.Pong;
import org.piangles.gateway.requests.dto.Request;

public final class PingMessageProcessor extends AbstractRequestProcessor<Ping, Pong>
{
	public PingMessageProcessor()
	{
		super(Endpoints.Ping, Ping.class, Pong.class);
	}

	@Override
	protected Pong processRequest(ClientDetails clientDetails, Request request, Ping ping) throws Exception
	{
		return new Pong(ping.getSequenceNo(), ping.getTimestamp());
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
