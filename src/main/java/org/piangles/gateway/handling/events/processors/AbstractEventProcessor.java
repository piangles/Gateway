package org.piangles.gateway.handling.events.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.events.EventProcessor;

public abstract class AbstractEventProcessor<T> implements EventProcessor
{
	protected LoggingService logger = Locator.getInstance().getLoggingService();
	private String type;
	
	public AbstractEventProcessor(String type)
	{
		this.type = type;
	}
	
	@Override
	public final String getType()
	{
		return type;
	}
	
	@Override
	public final void process(ClientDetails clientDetails, Event event)
	{
		try
		{
			processPayload(clientDetails, convertPayload(event));			
		}
		catch(Exception expt)
		{
			logger.info("Unable to process event : " + event, expt);
		}
	}

	public abstract void processPayload(ClientDetails clientDetails, T payload) throws Exception;
	
	private T convertPayload(Event event) throws Exception
	{
		Class<?> payloadClass = Class.forName(event.getPayloadType());
		Object payload = JSON.getDecoder().decode(((String)event.getPayload()).getBytes(), payloadClass);
		
		return (T)payload;
	}
}
