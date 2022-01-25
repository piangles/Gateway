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
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.backbone.services.msg.Topic;
import org.piangles.core.resources.ConsumerProperties;
import org.piangles.core.resources.KafkaMessagingSystem;
import org.piangles.core.resources.ResourceException;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.Constants;
import org.piangles.gateway.client.ClientDetails;

public final class EventListener extends Thread
{
	private static final String COMPONENT_ID = "1a465968-c647-4fac-9d25-fbd70fa86fee";

	private static final int DEFAULT_SLEEP_TIME = 1500;
	private static final int DEFAULT_WAIT_TIME = 100;
	private static final int MAX_ERROR_LIMIT = 10;
	
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private ClientDetails clientDetails = null;
	
	private KafkaMessagingSystem kms = null;
	private ConsumerProperties consumerProps = null;
	private KafkaConsumer<String, String> consumer = null;
	
	private EventDispatcher eventDispatcher = null;
	
	private int errorCount = 0; 
	private final AtomicBoolean topicsHaveChanged = new AtomicBoolean(false);
	private final AtomicBoolean stopRequested = new AtomicBoolean(false);

	public EventListener(ClientDetails clientDetails, ConsumerProperties consumerProps, EventDispatcher eventDispatcher)  throws ResourceException
	{
		this.clientDetails = clientDetails;
		this.consumerProps = consumerProps;
		this.eventDispatcher = eventDispatcher;
		
		kms = ResourceManager.getInstance().getKafkaMessagingSystem(new DefaultConfigProvider(Constants.SERVICE_NAME, COMPONENT_ID));
	}
	
	@Override
	public void run()
	{
		logger.info("Started listening for events for: " + clientDetails);
		while (!stopRequested.get())
		{
			try
			{
				if (consumer != null)
				{
					ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(DEFAULT_WAIT_TIME));
					if (records.count() != 0)
					{
						Map<Event, Topic> eventTopicMap = new HashMap<>();
						for (ConsumerRecord<String, String> record : records)
						{
							//Convert the String in Value to Event
							Event event = JSON.getDecoder().decode(record.value().getBytes(), Event.class);
							eventTopicMap.put(event, new Topic(record.topic(), record.partition()));
						}
						eventDispatcher.dispatchAllEvents(eventTopicMap);
					}
				}
				else
				{
					logger.info("No consumer yet going to sleep for " + DEFAULT_SLEEP_TIME + " Milliseconds.");
					Thread.sleep(DEFAULT_SLEEP_TIME);
				}
				
				synchronized (consumerProps)
				{
					if (topicsHaveChanged.get())
					{
						logger.info("Topics have changed. Creating consumer for Topics: " + consumerProps.getTopics());
						closeConsumer();
						consumer = kms.createConsumer(consumerProps);

						topicsHaveChanged.set(false);
					}
				}
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
		closeConsumer();
		logger.info("Stopped listening for events for: " + clientDetails);
	}
	
	void topicsHaveChanged()
	{
		synchronized (consumerProps)
		{
			topicsHaveChanged.set(true);
		}
	}

	public void markForStopping()
	{
		logger.info("Stop listening for events requested for: " + clientDetails);
		stopRequested.set(true);
	}
	
	private void closeConsumer()
	{
		try
		{
			if (consumer != null)
			{
				logger.debug("Closing existing EventListener->Consumer for: " + clientDetails);
				consumer.close();
			}
		}
		catch(Exception e)
		{
			logger.warn("Unable to close KafkaConsumer because of: " + e.getMessage(), e);
		}
	}
}
