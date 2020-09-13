package com.TBD.app.gateway.it;

import java.net.URI;
import java.util.UUID;

import com.TBD.app.gateway.dto.LoginRequest;
import com.TBD.app.gateway.dto.Request;
import com.TBD.app.gateway.dto.SystemInfo;
import com.TBD.core.util.coding.JSON;

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
			LoginRequest loginRequest = new LoginRequest("saradhivs", "password", null);
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