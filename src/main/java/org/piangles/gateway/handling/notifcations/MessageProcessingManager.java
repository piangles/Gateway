package org.piangles.gateway.handling.notifcations;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Message;
import org.piangles.backbone.services.msg.MessagingException;
import org.piangles.backbone.services.msg.MessagingService;
import org.piangles.backbone.services.msg.Topic;
import org.piangles.core.resources.ConsumerProperties;
import org.piangles.core.resources.KafkaMessagingSystem;
import org.piangles.gateway.handling.ClientDetails;

/**
 * 
 *
 *	TODO : Need to synchronize this class properly
 */
public class MessageProcessingManager
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private MessagingService msgService = Locator.getInstance().getMessagingService();

	private ClientDetails clientDetails = null;
	private List<Topic> topics = null;
	private boolean restartMessageListener = true;
	private KafkaMessagingSystem kms = null;
	private KafkaConsumer<String, String> consumer = null;	
	private MessageListener messageListener = null;

	public MessageProcessingManager(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
		topics = new ArrayList<Topic>();	 
	}

	public void subscribeToTopic(Topic topic)
	{
		logger.info("Subscribing to " + topic);
		topics.add(topic);
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
				topics.add(topic);	
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

	public void unsubscribe(Topic topic)
	{
		logger.info("Unsubscribing to " + topic);
		topics.remove(topic);
		//TODO Start a timer thread
		restartMessageListener = true;
	}

	public void processAllMessages(List<Message> messages) throws Exception
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
		ConsumerProperties consumerProps = new ConsumerProperties(clientDetails.getSessionDetails().getUserId());
		topics.stream().map(topic -> { 
			int partiton = 0;
			if (!topic.isPartioned())
			{
				partiton = 0;
			}
			consumerProps.getTopics().add(consumerProps.new Topic(topic.getTopicName(), partiton));
			return topic;
		});
		consumer = kms.createConsumer(consumerProps);
		
		messageListener = new MessageListener(clientDetails, consumer);
		Thread thread = new Thread(messageListener);
		thread.start();
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
