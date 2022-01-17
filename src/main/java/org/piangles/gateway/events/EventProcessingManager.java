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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.piangles.gateway.Constants;
import org.piangles.gateway.client.ClientDetails;

/**
 * 
 * �People only see what you allow them to see.� 
 * �Dr. Jennifer Melfi in Sopranos
 */
public class EventProcessingManager implements EventDispatcher
{
	private static final String COMPONENT_ID = "1a465968-c647-4fac-9d25-fbd70fa86fee";
	private LoggingService logger = Locator.getInstance().getLoggingService();

	
	/** TODO on reconnect need to either reuse or recreate EventProcessingManager with CLientDetails and topicTradeId**/
	private ClientDetails clientDetails = null;
	private Map<Topic, UUID> topicTraceIdMap = null;
	
	private boolean restartEventListener = true;
	private KafkaMessagingSystem kms = null;
	private KafkaConsumer<String, String> consumer = null;
	private EventListener eventListener = null;

	public EventProcessingManager(ClientDetails clientDetails) throws ResourceException
	{
		this.clientDetails = clientDetails;
		topicTraceIdMap = new HashMap<>();

		kms = ResourceManager.getInstance().getKafkaMessagingSystem(new DefaultConfigProvider(Constants.SERVICE_NAME, COMPONENT_ID));
	}

	public synchronized void restart()
	{
		stop();
		start();
	}

	public synchronized void stop()
	{
		KafkaConsumerManager.getInstance().closeOrMarkForClose(consumer);
		if (eventListener != null)
		{
			eventListener.markForStopping();
		}
	}

	public synchronized void subscribeToTopics(Map<Topic, UUID> topicTraceIdMap)
	{
		logger.info("Subscribing to " + topicTraceIdMap.keySet());
		this.topicTraceIdMap.putAll(topicTraceIdMap);
		restartEventListener = true;
	}

	public synchronized void unsubscribeTopics(List<Topic> topics)
	{
		logger.info("Unsubscribing to " + topics);
		topics.stream().forEach(topic -> topicTraceIdMap.remove(topic));
		restartEventListener = true;
	}

	public synchronized void dispatchAllEvents(Map<Event, Topic> toBeDispactedTopicEventMap) throws Exception
	{
		for (Map.Entry<Event, Topic> entry : toBeDispactedTopicEventMap.entrySet())
		{
			Event event = entry.getKey();
			try
			{
				EventProcessor mp = EventRouter.getInstance().getProcessor(event);
				if (mp != null)
				{
					event.setTraceId(topicTraceIdMap.get(entry.getValue()));
					mp.process(clientDetails, event);
				}
			}
			catch (Exception e)
			{
				logger.error("Unexpected Eror while processing Event : " + event, e);
			}
		}

		if (restartEventListener)
		{
			// restart consumer
			restart();
		}
	}

	private void start()
	{
		restartEventListener = false;

		if (topicTraceIdMap.size() != 0)
		{
			/**
			 * ConsumerProperties uses the userId as consumerGroupId, the Kafka servers uses the
			 * consumergroupId to maintain offset of the Consumer. This is how Kafka figures out
			 * how many messages a consumer has consumed from a Topic and when the consumer connects
			 * next time it uses this to start from the correct offset.
			 */
			ConsumerProperties consumerProps = new ConsumerProperties(clientDetails.getSessionDetails().getUserId());
			// create ConsumerProperties from list of Topics
			List<ConsumerProperties.Topic> modifiedTopics = topicTraceIdMap.keySet().stream().map(topic -> {
				return consumerProps.new Topic(topic.getTopicName(), topic.getPartition(), topic.shouldReadEarliest());
			}).collect(Collectors.toList());
			
			consumerProps.setTopics(modifiedTopics);
			consumer = kms.createConsumer(consumerProps);
			KafkaConsumerManager.getInstance().addNewConsumer(consumer);

			eventListener = new EventListener(clientDetails, consumer, this);
			Thread thread = new Thread(eventListener);
			thread.start();
		}
		else
		{
			logger.info("No topics to listen for: " + clientDetails);
		}
	}
}
