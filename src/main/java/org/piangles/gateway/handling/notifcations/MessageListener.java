package org.piangles.gateway.handling.notifcations;

import java.util.concurrent.atomic.AtomicBoolean;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;

public final class MessageListener implements Runnable
{
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private final AtomicBoolean stop = new AtomicBoolean(false);

	
	@Override
	public void run()
	{
		try
		{	
//			logger.info("USER TOPICS ::::::::::::::::::::::" + userTopics);
//
//			userTopics = msgService.getTopicsFor(clientDetails.getSessionDetails().getUserId());

			
			//Start the while loop
			while (stop.get())
			{
				//On receipt of messages
				
				//controlMessageRouter.getHandler(type);
			}
		}
		catch (Exception e) //(MessagingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop()
	{
		stop.set(true);
	}
}
