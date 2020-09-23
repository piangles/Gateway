package com.TBD.gateway.handling.notifcations;

import java.util.HashMap;
import java.util.Map;

import com.TBD.gateway.handling.requests.RequestProcessor;
import com.TBD.gateway.handling.requests.processors.ConfigRequestProcessor;
import com.TBD.gateway.handling.requests.processors.GetUserPreferenceRequestProcessor;
import com.TBD.gateway.handling.requests.processors.KeepSessionAliveRequestProcessor;
import com.TBD.gateway.handling.requests.processors.LoginRequestProcessor;
import com.TBD.gateway.handling.requests.processors.LogoutRequestProcessor;
import com.TBD.gateway.handling.requests.processors.PingMessageProcessor;
import com.TBD.gateway.handling.requests.processors.SetUserPreferenceRequestProcessor;
import com.TBD.gateway.handling.requests.processors.SubscribeRequestRequestProcessor;

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
