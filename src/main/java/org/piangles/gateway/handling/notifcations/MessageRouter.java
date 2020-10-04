package org.piangles.gateway.handling.notifcations;

import java.util.HashMap;
import java.util.Map;

import org.piangles.gateway.handling.requests.RequestProcessor;

public class MessageRouter
{
	private static MessageRouter self = null;
	private Map<String, MessageProcessor> messageProcessorMap;

	private MessageRouter()
	{
		messageProcessorMap = new HashMap<String, MessageProcessor>();

		//registerHandler(new PingMessageProcessor());
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
		messageProcessorMap.put(messageProcessor.getType(), messageProcessor);
	}
}
