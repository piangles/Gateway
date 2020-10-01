package org.piangles.gateway.handling.notifcations;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.MessagingException;
import org.piangles.backbone.services.msg.MessagingService;
import org.piangles.backbone.services.msg.Topic;
import org.piangles.gateway.handling.ClientDetails;

/**
 * Actively listens for messages from the messaging bus >>> meant for this
 * particular client / session and picks them up and sends them via Websocket
 * 
 */


public final class ClientNotifier
{
	/**
	 * Topics org.piangles.gateway.control.user.<UserId>
	 */
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private MessagingService msgService = Locator.getInstance().getMessagingService();

	private final AtomicBoolean stop = new AtomicBoolean(false);
	private ClientDetails clientDetails = null;
	private List<Topic> userTopics = null;
	private MessageListener messageListener = null;

	public ClientNotifier(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
	}

	public void start()
	{
		messageListener = new MessageListener();
		//What about general messages

		Thread thread = new Thread(messageListener);
		thread.start();
	}

	public void stop()
	{
		stop.set(true);
	}

	public void subscribeToTopic(Topic topic)
	{
		System.out.println("SYSTEM TOPIC ::::::::::::::::::::::" + topic);
	}

	public void subscribeToAlias(List<String> aliases)
	{
		try
		{
			List<Topic> aliasTopics = msgService.getTopicsForAliases(aliases);
			System.out.println("ALIAS TOPICS ::::::::::::::::::::::" + aliasTopics);
		}
		catch (MessagingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void unsubscribe(Topic topic)
	{
	}
	
	private class MessageListener implements Runnable
	{
		@Override
		public void run()
		{
			try
			{	
				logger.info("USER TOPICS ::::::::::::::::::::::" + userTopics);

				userTopics = msgService.getTopicsFor(clientDetails.getSessionDetails().getUserId());

				
				//Start the while loop
				while (stop.get())
				{
					//On receipt of messages
					
					//controlMessageRouter.getHandler(type);
				}
			}
			catch (MessagingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
