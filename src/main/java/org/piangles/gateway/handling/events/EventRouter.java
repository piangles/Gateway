package org.piangles.gateway.handling.events;

import java.util.HashMap;
import java.util.Map;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.events.processors.PassThruControlEventProcessor;
import org.piangles.gateway.handling.requests.RequestProcessor;

public class EventRouter
{
	private static EventRouter self = null;
	private ClientDetails clientDetails = null;
	private Map<String, EventProcessor> eventProcessorMap;

	void init(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
		eventProcessorMap = new HashMap<String, EventProcessor>();
	}

	public static EventRouter getInstance()
	{
		if (self == null)
		{
			synchronized (EventRouter.class)
			{
				if (self == null)
				{
					self = new EventRouter();
				}
			}
		}

		return self;
	}

	public void registerEventProcessors()
	{
		registerProcessor(new PassThruControlEventProcessor());
	}
	
	public EventProcessor getProcessor(String type)
	{
		return eventProcessorMap.get(type);
	}

	public void registerProcessor(EventProcessor eventProcessor)
	{
		if (eventProcessorMap.containsKey(eventProcessor.getType()))
		{
			throw new RuntimeException("Event Router already has a registered endpoint : " + eventProcessor.getType());
		}
		eventProcessor.init(clientDetails);
		eventProcessorMap.put(eventProcessor.getType(), eventProcessor);
	}
}
