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
 
 
 
package org.piangles.app.gateway.it;

import java.net.URI;
import java.util.UUID;

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.requests.dto.LoginRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SystemInfo;

public class HeadlessClientFail
{

	public static void main(String[] args)
	{
		try
		{
			// open websocket
			final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI("ws://localhost:8080/"));

			// add listener
			clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler()
			{
				public void handleMessage(String message)
				{
					System.out.println(message);
				}
			});
			
			String sessionId = UUID.randomUUID().toString();
			SystemInfo systemInfo = new SystemInfo("HeadlessClient", "12345");
			LoginRequest loginRequest = new LoginRequest("Default", "saradhivs", "password", null);
			String loginReqAsStr = new String(JSON.getEncoder().encode(loginRequest));
			Request request = new Request(sessionId, systemInfo.cloneAndCopy("threadId-123"), "Dummy", loginReqAsStr);
			
			System.out.println(new String(JSON.getEncoder().encode(request)));
			
			clientEndPoint.sendMessage(new String(JSON.getEncoder().encode(request)));
			
			Thread.sleep(5000);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.err.println("Exception because of : " + ex.getMessage());
		}
	}
}
