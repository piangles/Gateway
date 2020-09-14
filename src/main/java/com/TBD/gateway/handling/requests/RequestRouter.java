package com.TBD.gateway.handling.requests;

import java.util.HashMap;
import java.util.Map;

import com.TBD.gateway.handling.requests.processors.ConfigRequestProcessor;
import com.TBD.gateway.handling.requests.processors.GetUserPreferenceRequestProcessor;
import com.TBD.gateway.handling.requests.processors.LoginRequestProcessor;
import com.TBD.gateway.handling.requests.processors.LogoutRequestProcessor;
import com.TBD.gateway.handling.requests.processors.SetUserPreferenceRequestProcessor;

public class RequestRouter
{
	private static RequestRouter self = null;
	private Map<String, RequestProcessor> endpointRequestProcessorMap;

	private RequestRouter()
	{
		endpointRequestProcessorMap = new HashMap<String, RequestProcessor>();

		registerRequestProcessor(new LoginRequestProcessor());
		registerRequestProcessor(new LogoutRequestProcessor());
		registerRequestProcessor(new ConfigRequestProcessor());
		registerRequestProcessor(new GetUserPreferenceRequestProcessor());
		registerRequestProcessor(new SetUserPreferenceRequestProcessor());
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
