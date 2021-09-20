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

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.requests.dto.LoginRequest;
import org.piangles.gateway.requests.dto.LoginResponse;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.SystemInfo;

public class HeadlessClientHelper //Rename
{
	// open websocket
	protected WebsocketClientEndpoint clientEndpoint;
	private String sessionId = null;
	
	public HeadlessClientHelper() throws Exception
	{
		String hostName = null;
		boolean useLocal = false;
		if (useLocal)
		{
			hostName = "localhost";
		}
		else
		{
			hostName = "ec2-52-23-185-3.compute-1.amazonaws.com";
		}
		clientEndpoint = new WebsocketClientEndpoint(new URI("ws://" + hostName + ":8080/"));
		
		// add listener
		clientEndpoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler()
		{
			public void handleMessage(String message)
			{
				System.out.println("Received Response:" + message);
				try
				{
					Response response = JSON.getDecoder().decode(message.getBytes(), Response.class);
					if (response.isRequestSuccessful())
					{
						switch (response.getEndpoint())
						{
						case "Login":
							LoginResponse loginResponse = JSON.getDecoder().decode(response.getEndpointResponse().getBytes(), LoginResponse.class);
							sessionId = loginResponse.getSessionId();
							if (sessionId != null)
							{
								System.out.println("Finished logging in.");
							}
							break;
							
						case "Logout":
							System.out.println("Finished logging out = " + response.getEndpointResponse());
							break;
							
						default:
							System.out.println("HeadlessClientHelper does not currently handle Response for Endpoint : " + response.getEndpoint());
						}
					}
					else
					{
						System.err.println("Request was not successfully processed because of : " + response.getErrorMessage());
					}
				}
				catch(Exception expt)
				{
					expt.printStackTrace();
				}
			}
		});
	}
	
	protected final void login() throws Exception
	{
		LoginRequest loginRequest = new LoginRequest("Default", "testuser@testmail.com", "password", null);

		createRequestAndSend("Login", loginRequest);
	}

	protected final void loginWithSession() throws Exception
	{
		LoginRequest loginRequest = new LoginRequest("Default", "testuser@testmail.com", null, sessionId);

		createRequestAndSend("Login", loginRequest);
	}

	protected final WebsocketClientEndpoint getClientEndpoint()
	{
		return clientEndpoint;
	}

	protected final void createRequestAndSend(String endpoint) throws Exception
	{
		createRequestAndSend(endpoint, null);
	}
	
	protected final void createRequestAndSend(String endpoint, Object appRequest) throws Exception
	{
		String appReqAsStr = null;
		if (appRequest != null)
		{
			appReqAsStr = new String(JSON.getEncoder().encode(appRequest));
		}
		
		Request request = new Request(sessionId, endpoint, appReqAsStr, new SystemInfo("Win64", "Chrome", "92"));
		System.out.println("For Endpoint : " + endpoint + "  the TRACE ID :" + request.getTraceId());
		String requestAsStr = new String(JSON.getEncoder().encode(request));	
		
		System.out.println("Sending Request:" + requestAsStr);
		
		clientEndpoint.sendMessage(requestAsStr);
	}
}
