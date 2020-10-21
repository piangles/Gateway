package org.piangles.gateway.handling.events;

import java.util.HashMap;
import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.EventType;
import org.piangles.gateway.handling.events.processors.PassThruControlEventProcessor;
import org.piangles.gateway.handling.events.processors.PassThruNotificationEventProcessor;

public class EventRouter
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private static EventRouter self = null;
	
	private boolean automaticPassThru = false;
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

	public void registerPassThruProcessor(EventType type, String payloadType)
	{
		String processorId = null;
		EventProcessor ep = null;
		
		if (type == EventType.Control)
		{
			ep = new PassThruControlEventProcessor();
			processorId = ep.getType() + ":" + payloadType; 
			
		}
		else if (type == EventType.Notification)
		{
			processorId = payloadType;
			ep = new PassThruNotificationEventProcessor(payloadType);
		}
		register(processorId, ep);
	}

	public EventProcessor getProcessor(String type)
	{
		return eventProcessorMap.get(type);
	}

	public void registerProcessor(EventProcessor eventProcessor)
	{
		register(eventProcessor.getType(), eventProcessor);
	}
	
	private void register(String processorId, EventProcessor eventProcessor)
	{
		if (eventProcessorMap.containsKey(processorId))
		{
			throw new RuntimeException("Event Router already has a registered endpoint : " + eventProcessor.getType());
		}
		System.out.println("Registering EventProcessor for Id:" + processorId);
		logger.info("Registering EventProcessor for Id:" + processorId);
		eventProcessorMap.put(processorId, eventProcessor);
	}
	
	public void enableAutomaticPassThru()
	{
		automaticPassThru = true;
	}
	
	public boolean isAutomaticPassThru()
	{
		return automaticPassThru;
	}
}
