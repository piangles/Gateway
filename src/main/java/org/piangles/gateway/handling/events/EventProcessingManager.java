package org.piangles.gateway.handling.events;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.backbone.services.msg.MessagingException;
import org.piangles.backbone.services.msg.MessagingService;
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
 * TODO : Need to synchronize this class properly
 */
public class EventProcessingManager implements EventDispatcher
{
	private static final String COMPONENT_ID = "1a465968-c647-4fac-9d25-fbd70fa86fee";
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private MessagingService msgService = Locator.getInstance().getMessagingService();

	private ClientDetails clientDetails = null;
	private List<Topic> topics = null;
	private boolean restartEventListener = true;
	private KafkaMessagingSystem kms = null;
	private KafkaConsumer<String, String> consumer = null;
	private EventListener eventListener = null;

	public EventProcessingManager(ClientDetails clientDetails) throws ResourceException
	{
		this.clientDetails = clientDetails;
		topics = new ArrayList<Topic>();

		// TODO ::: Need to fix this.
		EventRouter.getInstance().init(clientDetails);
		EventRouter.getInstance().registerEventProcessors();
		kms = ResourceManager.getInstance().getKafkaMessagingSystem(new DefaultConfigProvider(Constants.SERVICE_NAME, COMPONENT_ID));
	}

	public void subscribeToTopic(Topic topic)
	{
		logger.info("Subscribing to " + topic);
		this.topics.add(topic);
		restartEventListener = true;
	}

	public void subscribeToTopics(List<Topic> topics)
	{
		logger.info("Subscribing to " + topics);
		this.topics.addAll(topics);
		restartEventListener = true;
	}

	public void subscribeToAlias(List<String> aliases) throws MessagingException
	{
		try
		{
			List<Topic> aliasTopics = msgService.getTopicsForAliases(aliases);
			for (Topic topic : aliasTopics)
			{
				logger.info("Subscribing to " + topic);
				this.topics.add(topic);
			}
			restartEventListener = true;
		}
		catch (MessagingException e)
		{
			logger.error("Unable to convert from alias to topic:", e);
			throw e;
		}
	}

	public void unsubscribeTopic(Topic topic)
	{
		logger.info("Unsubscribing to " + topic);
		this.topics.remove(topic);
		restartEventListener = true;
	}

	public void unsubscribeTopics(List<Topic> topics)
	{
		logger.info("Unsubscribing to " + topics);
		this.topics.removeAll(topics);
		restartEventListener = true;
	}

	public void dispatchAllEvents(List<Event> events) throws Exception
	{
		for (Event event : events)
		{
			/**
			 * This is purely for analytics purpose. To make sure 99% of the
			 * events are Notification and 1% is Control.
			 */
			switch (event.getType())
			{
			case Control:

			case Notification:
			}

			try
			{
				EventProcessor mp = EventRouter.getInstance().getProcessor(event.getPayloadType());
				if (mp != null)
				{
					mp.process(event);
				}
				else
				{
					logger.error("Unable to find EventProcessor for:" + event.getPayloadType());
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

		// create ConsumerProperties from list of Topics
		ConsumerProperties consumerProps = new ConsumerProperties(UUID.randomUUID().toString()); // clientDetails.getSessionDetails().getUserId());
		List<ConsumerProperties.Topic> modifiedTopics = topics.stream().map(topic -> {
			int partiton = 0;
			if (!topic.isPartioned())
			{
				partiton = 0;
			}
			return consumerProps.new Topic(topic.getTopicName(), partiton);
		}).collect(Collectors.toList());
		consumerProps.setTopics(modifiedTopics);
		consumer = kms.createConsumer(consumerProps);
		KafkaConsumerManager.getInstance().addNewConsumer(consumer);

		if (topics.size() != 0)
		{
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
