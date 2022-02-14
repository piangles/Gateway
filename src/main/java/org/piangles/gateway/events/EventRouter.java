/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.gateway.events;

import java.util.HashMap;
import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Control;
import org.piangles.backbone.services.msg.Event;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.events.processors.PassThruControlEventProcessor;
import org.piangles.gateway.events.processors.PassThruApplicationEventsProcessor;

public class EventRouter
{
	private static final String EVENT_TYPE_CONTROL = "Control";
	
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private static EventRouter self = null;
	
	private boolean automaticPassThru = false;
	private EventProcessor passThruControlProcessor = null;
	private EventProcessor passThruNotificationProcessor = null;
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

	public EventProcessor getProcessor(Event event) throws Exception
	{	
		/**
		 * This is purely for analytics purpose. To make sure 99% of the
		 * events are Notification and 1% is Control.
		 */
		String processorId = null;
		if (EVENT_TYPE_CONTROL.equals(event.getEventType()))
		{
			Control controlDetails = JSON.getDecoder().decode(((String)event.getPayload()).getBytes(), Control.class);
			processorId = createControlEventProcessorId(controlDetails.getType());
		}
		else
		{
			processorId = event.getPayloadType();
		}
		
		EventProcessor ep = eventProcessorMap.get(processorId);
		if (ep == null && automaticPassThru)
		{
			/**
			 * Move this to Configuration enabled, it prints quite a bit.
			 * 
			 * logger.debug("EventProcessor for ProcessorId: " + processorId + " not found however automaticPassThru enabled.");
			 */
			if (EVENT_TYPE_CONTROL.equals(event.getEventType()))
			{
				ep = passThruControlProcessor;
			}
			else
			{
				ep = passThruNotificationProcessor;
			}
		}
		else
		{
			logger.error("Unable to find EventProcessor for:" + processorId);
		}


		return ep;
	}

	public void registerControlProcessor(String contolPayloadType, EventProcessor eventProcessor)
	{
		register(createControlEventProcessorId(contolPayloadType), eventProcessor);
	}

	public void registerNotificationProcessor(EventProcessor eventProcessor)
	{
		register(eventProcessor.getType(), eventProcessor);
	}

	public void registerPassThruProcessor(String eventType, String payloadType)
	{
		String processorId = null;
		EventProcessor ep = null;
		
		if (EVENT_TYPE_CONTROL.equals(eventType))
		{
			ep = new PassThruControlEventProcessor();
			processorId = createControlEventProcessorId(payloadType); 
			
		}
		else
		{
			processorId = payloadType;
			ep = new PassThruApplicationEventsProcessor(payloadType);
		}
		register(processorId, ep);
	}

	private void register(String processorId, EventProcessor eventProcessor)
	{
		if (eventProcessorMap.containsKey(processorId))
		{
			throw new RuntimeException("Event Router already has a registered endpoint : " + eventProcessor.getType());
		}
		logger.info("Registering EventProcessor for Id:" + processorId);
		eventProcessorMap.put(processorId, eventProcessor);
	}
	
	/**
	 * If we do not enable AutomaticPassThru for Events then for
	 * each type of Control & Notification we have to create and
	 * register Processor, like below
	 * 
	 * EventRouter.getInstance().registerPassThruProcessor(EventType.Control, "TestMessage");
	 * EventRouter.getInstance().registerPassThruProcessor(EventType.Notification, "com.pianglesdemo.services.news.NewsEvent");
	 * 
	 * OR
	 * 
	 * process it specifically, like below
	 * 
	 * EventRouter.getInstance().registerControlProcessor("TestMessage", new PassThruControlEventProcessor());
	 * EventRouter.getInstance().registerNotificationProcessor(new PassThruNotificationEventProcessor("com.pianglesdemo.services.news.NewsEvent"));
	 *   
	 */
	public void enableAutomaticPassThru()
	{
		automaticPassThru = true;
		passThruControlProcessor = new PassThruControlEventProcessor();
		passThruNotificationProcessor = new PassThruApplicationEventsProcessor("AutoPassThru");
	}
	
	public boolean isAutomaticPassThru()
	{
		if (automaticPassThru)
		{
			logger.info("AutomaticPassThru is Enabled:  PassThruEventProcessor will be used when event processor is not found.");
		}
		else
		{
			logger.info("AutomaticPassThru is Disabled. Error will be thrown, when event processor is not found.");
		}
		return automaticPassThru;
	}
	
	private String createControlEventProcessorId(String controlPayloadType)
	{
		return Control.class.getCanonicalName() + ":" + controlPayloadType;
	}
}
