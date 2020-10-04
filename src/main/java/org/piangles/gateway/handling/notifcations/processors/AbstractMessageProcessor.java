package org.piangles.gateway.handling.notifcations.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Message;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.notifcations.MessageProcessor;

public abstract class AbstractMessageProcessor<T> implements MessageProcessor
{
	protected LoggingService logger = Locator.getInstance().getLoggingService();
	private String type;
	private ClientDetails clientDetails;
	
	public AbstractMessageProcessor(String type)
	{
		this.type = type;
	}
	
	public final void init(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
	}
	
	public final String getType()
	{
		return type;
	}
	
	public final void process(Message message)
	{
		try
		{
			processPayload((T)message.getPayload());			
		}
		catch(Exception expt)
		{
			logger.info("Unable to process message : " + message, expt);
		}
	}

	protected final ClientDetails getClientDetails()
	{
		return clientDetails;
	}
	
	public abstract void processPayload(T payload) throws Exception;
}
