package org.piangles.gateway.service.impl.jetty;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.piangles.gateway.ClientEndpoint;
import org.piangles.gateway.handling.RequestProcessingManager;

@WebSocket
public final class WebSocketLifecycleEventHandler
{
	private RequestProcessingManager rpm = null;
	
	@OnWebSocketClose
	public void onClose(int statusCode, String reason)
	{
		rpm.onClose(statusCode, reason);
	}

	@OnWebSocketError
	public void onError(Throwable t)
	{
		rpm.onError(t);
	}

	@OnWebSocketConnect
	public void onConnect(Session session)
	{
		ClientEndpoint clientEndpoint = (text) -> {
			session.getRemote().sendString(text);
		};
		try
		{
			rpm = new RequestProcessingManager(session.getRemoteAddress(), clientEndpoint);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			t.printStackTrace(System.out);
		}
	}

	@OnWebSocketMessage
	public void onMessage(String message)
	{
		rpm.onMessage(message);
	}
}