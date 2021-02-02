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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.backbone.services.msg.Topic;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.client.ClientDetails;

public final class EventListener implements Runnable
{
	private static final int DEFAULT_WAIT_TIME = 100;
	private static final int MAX_ERROR_LIMIT = 10;
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private ClientDetails clientDetails = null;
	private KafkaConsumer<String, String> consumer = null;
	private EventDispatcher eventDispatcher = null;
	private int errorCount = 0; 
	private final AtomicBoolean stopRequested = new AtomicBoolean(false);

	public EventListener(ClientDetails clientDetails, KafkaConsumer<String, String> consumer, EventDispatcher eventDispatcher)
	{
		this.clientDetails = clientDetails;
		this.consumer = consumer;
		this.eventDispatcher = eventDispatcher;
	}
	
	@Override
	public void run()
	{
		logger.info("Started listening for events for: " + clientDetails);
		while (!stopRequested.get())
		{
			try
			{
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(DEFAULT_WAIT_TIME));
				Map<Event, Topic> eventTopicMap = new HashMap<>();
				for (ConsumerRecord<String, String> record : records)
				{
					//Convert the String in Value to Event
					Event event = JSON.getDecoder().decode(record.value().getBytes(), Event.class);
					eventTopicMap.put(event, new Topic(record.topic(), record.partition()));
				}
				eventDispatcher.dispatchAllEvents(eventTopicMap);
			}
			catch (Exception e)
			{
				logger.error("Exception while polling / composingEvent:", e);
				errorCount = errorCount + 1;
				if (errorCount > MAX_ERROR_LIMIT)
				{
					logger.fatal("Event listener crossed the maximum limit of error : " + clientDetails);
					break;
				}
			}
		}
		logger.info("Stopped listening for events for: " + clientDetails);
	}

	public void markForStopping()
	{
		logger.info("Stop listening for events requested for: " + clientDetails);
		stopRequested.set(true);
	}
}
