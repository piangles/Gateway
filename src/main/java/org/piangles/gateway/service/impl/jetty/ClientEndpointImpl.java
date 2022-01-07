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
 
 
package org.piangles.gateway.service.impl.jetty;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.ClientEndpoint;
import org.piangles.gateway.Message;

class ClientEndpointImpl implements ClientEndpoint
{
	private Session session = null;
	
	public ClientEndpointImpl(Session session)
	{
		this.session = session;
	}
	
	@Override
	public synchronized void sendMessage(Message message) throws IOException
	{
		String text = null;
		try
		{
			text = new String(JSON.getEncoder().encode(message));
		}
		catch (Exception e)
		{
			throw new IOException(e.getMessage(), e);
		}
		try
		{
			/**
			 * This error happens
			 * java.lang.IllegalStateException: Blocking message pending 10000 for BLOCKING
			 * 
			 * https://stackoverflow.com/questions/26264508/websocket-async-send-can-result-in-blocked-send-once-queue-filled
			 * 
			 * session.getRemote().sendStringByFuture(text);
			 */
			session.getRemote().sendString(text);
		}
		catch (Exception e)
		{
			session.close();
			throw e;
		}
	}

	@Override
	public void close()
	{
		session.close();
	}

}
