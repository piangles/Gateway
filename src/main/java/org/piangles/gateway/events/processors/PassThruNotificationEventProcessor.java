package org.piangles.gateway.events.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.gateway.Message;
import org.piangles.gateway.MessageType;
import org.piangles.gateway.events.EventProcessor;
import org.piangles.gateway.requests.ClientDetails;

public class PassThruNotificationEventProcessor implements EventProcessor
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private String type = null;

	public PassThruNotificationEventProcessor(String type)
	{
		this.type = type;
	}
	
	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public void process(ClientDetails clientDetails, Event event)
	{
		try
		{
			clientDetails.getClientEndpoint().sendMessage(new Message(MessageType.Event, event));
		}
		catch (Exception e)
		{
			logger.info("Unable to process event : " + event, e);
		}		
	}
}
