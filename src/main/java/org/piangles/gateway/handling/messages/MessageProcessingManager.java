package org.piangles.gateway.handling.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Message;
import org.piangles.backbone.services.msg.MessagingException;
import org.piangles.backbone.services.msg.MessagingService;
import org.piangles.backbone.services.msg.Topic;
import org.piangles.core.resources.ConsumerProperties;
import org.piangles.core.resources.KafkaMessagingSystem;
import org.piangles.core.resources.ResourceException;
import org.piangles.core.resources.ResourceManager;
import org.piangles.gateway.handling.ClientDetails;

/**
 * 
 *
 *	TODO : Need to synchronize this class properly
 */
public class MessageProcessingManager implements MessageDispatcher
{
	private static final String COMPONENT_ID = "5d435fe2-7e54-43c3-84d2-8f4addf2dac9";
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private MessagingService msgService = Locator.getInstance().getMessagingService();

	private ClientDetails clientDetails = null;
	private List<Topic> topics = null;
	private boolean restartMessageListener = true;
	private KafkaMessagingSystem kms = null;
	private KafkaConsumer<String, String> consumer = null;	
	private MessageListener messageListener = null;

	public MessageProcessingManager(ClientDetails clientDetails) throws ResourceException
	{
		this.clientDetails = clientDetails;
		topics = new ArrayList<Topic>();	
		//kms = ResourceManager.getInstance().getKafkaMessagingSystem(new DefaultConfigProvider(Constants.SERVICE_NAME, COMPONENT_ID));
		kms = ResourceManager.getInstance().getKafkaMessagingSystem(new DefaultConfigProvider("MessagingService", "fd5f51bc-5a14-4675-9df4-982808bb106b"));
	}

	public void subscribeToTopic(Topic topic)
	{
		logger.info("Subscribing to " + topic);
		this.topics.add(topic);
		//TODO Start a timer thread
		restartMessageListener = true;
	}

	public void subscribeToTopics(List<Topic> topics)
	{
		logger.info("Subscribing to " + topics);
		this.topics.addAll(topics);
		//TODO Start a timer thread
		restartMessageListener = true;
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
			//TODO Start a timer thread
			restartMessageListener = true;
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
		//TODO Start a timer thread
		restartMessageListener = true;
	}

	public void unsubscribeTopics(List<Topic> topics)
	{
		logger.info("Unsubscribing to " + topics);
		this.topics.removeAll(topics);
		//TODO Start a timer thread
		restartMessageListener = true;
	}

	public void dispatchAllMessages(List<Message> messages) throws Exception
	{
		for (Message message : messages)
		{
			/**
			 * This is purely for analytics purpose. 
			 * To make sure 99% of the messages are Notification and 1% is Control. 
			 */
			switch (message.getType())
			{
			case Control:
					
			case Notification:	
			}
			
			try
			{
				MessageProcessor mp = MessageRouter.getInstance().getProcessor(message.getPayloadType());
				mp.process(message);
			}
			catch (Exception e)
			{
				logger.error("Unexpected Eror while processing Message : " + message, e);
			}
		}
		
		if (restartMessageListener)
		{
			//recreate consumer
			stop();
			start();
		}
	}
	
	public void start()
	{
		restartMessageListener = false;

		//create ConsumerProperties from list of Topics
		ConsumerProperties consumerProps = new ConsumerProperties(UUID.randomUUID().toString()); //clientDetails.getSessionDetails().getUserId());
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
		
		if (topics.size() != 0)
		{
			messageListener = new MessageListener(clientDetails, consumer, this);
			Thread thread = new Thread(messageListener);
			thread.start();
		}
		else
		{
			logger.info("No topics to listen for: " + clientDetails);
		}
	}
	
	public void stop()
	{
		if (consumer != null)
		{
			consumer.close();
		}
		messageListener.markForStopping();
	}
}
