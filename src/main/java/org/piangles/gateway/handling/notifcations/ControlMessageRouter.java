package org.piangles.gateway.handling.notifcations;

import java.util.HashMap;
import java.util.Map;

import org.piangles.gateway.handling.requests.RequestProcessor;

public class ControlMessageRouter
{
	private static ControlMessageRouter self = null;
	private Map<String, ControlMessageHandler> ccMessageHandlerMap;

	private ControlMessageRouter()
	{
		ccMessageHandlerMap = new HashMap<String, ControlMessageHandler>();

		//registerHandler(new PingMessageProcessor());
	}

	public static ControlMessageRouter getInstance()
	{
		if (self == null)
		{
			synchronized (RequestProcessor.class)
			{
				if (self == null)
				{
					self = new ControlMessageRouter();
				}
			}
		}

		return self;
	}

	public ControlMessageHandler getHandler(String type)
	{
		return ccMessageHandlerMap.get(type);
	}

	public void registerHandler(ControlMessageHandler ccMessageHandler)
	{
		if (ccMessageHandlerMap.containsKey(ccMessageHandler.getType()))
		{
			throw new RuntimeException("Request Router already has a registered endpoint : " + ccMessageHandler.getType());
		}
		ccMessageHandlerMap.put(ccMessageHandler.getType(), ccMessageHandler);
	}
}
