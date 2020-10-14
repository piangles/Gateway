package org.piangles.gateway.handling.events.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.gateway.Message;
import org.piangles.gateway.MessageType;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.events.EventProcessor;

public class PassThruControlEventProcessor implements EventProcessor
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private ClientDetails clientDetails = null;

	@Override
	public void init(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
	}

	@Override
	public String getType()
	{
		return Event.class.getCanonicalName();
	}

	@Override
	public void process(Event event)
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
