package org.piangles.gateway.handling.events;

import java.util.HashMap;
import java.util.Map;

import org.piangles.gateway.handling.events.processors.PassThruControlEventProcessor;
import org.piangles.gateway.handling.events.processors.PassThruNotificationEventProcessor;

public class EventRouter
{
	private static EventRouter self = null;
	private Map<String, EventProcessor> eventProcessorMap;

	private EventRouter()
	{
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

	public void registerPassThruControlEventProcessor()
	{
		registerProcessor(new PassThruControlEventProcessor());
	}

	public void registerPassThruNotificationEventProcessor(String type)
	{
		registerProcessor(new PassThruNotificationEventProcessor(type));
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
		eventProcessorMap.put(eventProcessor.getType(), eventProcessor);
	}
}
