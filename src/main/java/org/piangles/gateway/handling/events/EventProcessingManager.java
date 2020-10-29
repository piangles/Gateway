package org.piangles.gateway.handling.events;

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
import org.piangles.gateway.handling.ClientDetails;

/**
 * 
 * 
 * TODO : Need to synchronize this class properly or pause and stop consumer
 */
public class EventProcessingManager implements EventDispatcher
{
	private static final String COMPONENT_ID = "1a465968-c647-4fac-9d25-fbd70fa86fee";
	private LoggingService logger = Locator.getInstance().getLoggingService();

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

	public void subscribeToTopic(Topic topic, UUID traceId)
	{
		logger.info("Subscribing to " + topic);
		topicTraceIdMap.put(topic, traceId);
		restartEventListener = true;
	}

	public void subscribeToTopics(Map<Topic, UUID> topicTraceIdMap)
	{
		logger.info("Subscribing to " + topicTraceIdMap.keySet());
		this.topicTraceIdMap.putAll(topicTraceIdMap);
		restartEventListener = true;
	}

	public void unsubscribeTopic(Topic topic)
	{
		logger.info("Unsubscribing to " + topic);
		topicTraceIdMap.remove(topic);
		restartEventListener = true;
	}

	public void unsubscribeTopics(List<Topic> topics)
	{
		logger.info("Unsubscribing to " + topics);
		topics.stream().forEach(topic -> topicTraceIdMap.remove(topics));
		restartEventListener = true;
	}

	public void dispatchAllEvents(Map<Event, Topic> topicEventMap) throws Exception
	{
		for (Map.Entry<Event, Topic> entry : topicEventMap.entrySet())
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

	public void restart()
	{
		stop();
		start();
	}

	private void start()
	{
		restartEventListener = false;

		if (topicTraceIdMap.size() != 0)
		{
			// create ConsumerProperties from list of Topics
			ConsumerProperties consumerProps = new ConsumerProperties(clientDetails.getSessionDetails().getUserId());
			List<ConsumerProperties.Topic> modifiedTopics = topicTraceIdMap.keySet().stream().map(topic -> {
				int partiton = -1;
				if (topic.isPartioned())
				{
					partiton = topic.getPartition();
				}
				else
				{
					partiton = 0;
				}
				return consumerProps.new Topic(topic.getTopicName(), partiton);
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

	public void stop()
	{
		KafkaConsumerManager.getInstance().closeOrMarkForClose(consumer);
		if (eventListener != null)
		{
			eventListener.markForStopping();
		}
	}
}
