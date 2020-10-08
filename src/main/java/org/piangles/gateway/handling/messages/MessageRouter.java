package org.piangles.gateway.handling.messages;

import java.util.HashMap;
import java.util.Map;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.messages.processors.PassThruControlMessageProcessor;
import org.piangles.gateway.handling.requests.RequestProcessor;

public class MessageRouter
{
	private static MessageRouter self = null;
	private ClientDetails clientDetails = null;
	private Map<String, MessageProcessor> messageProcessorMap;

	void init(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
		messageProcessorMap = new HashMap<String, MessageProcessor>();
	}

	public static MessageRouter getInstance()
	{
		if (self == null)
		{
			synchronized (RequestProcessor.class)
			{
				if (self == null)
				{
					self = new MessageRouter();
				}
			}
		}

		return self;
	}

	public void registerMessageProcessors()
	{
		registerProcessor(new PassThruControlMessageProcessor());
	}
	
	public MessageProcessor getProcessor(String type)
	{
		return messageProcessorMap.get(type);
	}

	public void registerProcessor(MessageProcessor messageProcessor)
	{
		if (messageProcessorMap.containsKey(messageProcessor.getType()))
		{
			throw new RuntimeException("Request Router already has a registered endpoint : " + messageProcessor.getType());
		}
		messageProcessor.init(clientDetails);
		messageProcessorMap.put(messageProcessor.getType(), messageProcessor);
	}
}
