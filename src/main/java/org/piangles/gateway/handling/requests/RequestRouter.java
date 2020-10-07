package org.piangles.gateway.handling.requests;

import java.util.HashMap;
import java.util.Map;

import org.piangles.gateway.handling.requests.processors.ConfigRequestProcessor;
import org.piangles.gateway.handling.requests.processors.GetUserPreferenceRequestProcessor;
import org.piangles.gateway.handling.requests.processors.KeepSessionAliveRequestProcessor;
import org.piangles.gateway.handling.requests.processors.LoginRequestProcessor;
import org.piangles.gateway.handling.requests.processors.LogoutRequestProcessor;
import org.piangles.gateway.handling.requests.processors.PingMessageProcessor;
import org.piangles.gateway.handling.requests.processors.SetUserPreferenceRequestProcessor;
import org.piangles.gateway.handling.requests.processors.SubscribeRequestProcessor;

public class RequestRouter
{
	private static RequestRouter self = null;
	private Map<String, RequestProcessor> endpointRequestProcessorMap;

	private RequestRouter()
	{
		endpointRequestProcessorMap = new HashMap<String, RequestProcessor>();

		registerRequestProcessor(new PingMessageProcessor());
		registerRequestProcessor(new LoginRequestProcessor());
		registerRequestProcessor(new LogoutRequestProcessor());
		registerRequestProcessor(new KeepSessionAliveRequestProcessor());
		
		registerRequestProcessor(new ConfigRequestProcessor());
		registerRequestProcessor(new GetUserPreferenceRequestProcessor());
		registerRequestProcessor(new SetUserPreferenceRequestProcessor());
		registerRequestProcessor(new SubscribeRequestProcessor());
	}

	public static RequestRouter getInstance()
	{
		if (self == null)
		{
			synchronized (RequestProcessor.class)
			{
				if (self == null)
				{
					self = new RequestRouter();
				}
			}
		}

		return self;
	}

	public RequestProcessor getRequestProcessor(String endpoint)
	{
		return endpointRequestProcessorMap.get(endpoint);
	}

	public void registerRequestProcessor(RequestProcessor rp)
	{
		if (endpointRequestProcessorMap.containsKey(rp.getEndpoint()))
		{
			throw new RuntimeException("Request Router already has a registered endpoint : " + rp.getEndpoint());
		}
		endpointRequestProcessorMap.put(rp.getEndpoint(), rp);
	}
}
