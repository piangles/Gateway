package org.piangles.gateway.handling.notifcations;

import java.util.HashMap;
import java.util.Map;

import org.piangles.gateway.handling.requests.RequestProcessor;
import org.piangles.gateway.handling.requests.processors.ConfigRequestProcessor;
import org.piangles.gateway.handling.requests.processors.GetUserPreferenceRequestProcessor;
import org.piangles.gateway.handling.requests.processors.KeepSessionAliveRequestProcessor;
import org.piangles.gateway.handling.requests.processors.LoginRequestProcessor;
import org.piangles.gateway.handling.requests.processors.LogoutRequestProcessor;
import org.piangles.gateway.handling.requests.processors.PingMessageProcessor;
import org.piangles.gateway.handling.requests.processors.SetUserPreferenceRequestProcessor;
import org.piangles.gateway.handling.requests.processors.SubscribeRequestRequestProcessor;

public class ControlChannelMessageRouter
{
	private static ControlChannelMessageRouter self = null;
	private Map<String, ControlChannelMessageHandler> ccMessageHandlerMap;

	private ControlChannelMessageRouter()
	{
		ccMessageHandlerMap = new HashMap<String, ControlChannelMessageHandler>();

		registerHandler(new PingMessageProcessor());
	}

	public static ControlChannelMessageRouter getInstance()
	{
		if (self == null)
		{
			synchronized (RequestProcessor.class)
			{
				if (self == null)
				{
					self = new ControlChannelMessageRouter();
				}
			}
		}

		return self;
	}

	public RequestProcessor getRequestProcessor(String endpoint)
	{
		return endpointRequestProcessorMap.get(endpoint);
	}

	public void registerHandler(ControlChannelMessageHandler ccMessageHandler)
	{
		if (endpointRequestProcessorMap.containsKey(rp.getEndpoint()))
		{
			throw new RuntimeException("Request Router already has a registered endpoint : " + rp.getEndpoint());
		}
		endpointRequestProcessorMap.put(rp.getEndpoint(), rp);
	}
}
