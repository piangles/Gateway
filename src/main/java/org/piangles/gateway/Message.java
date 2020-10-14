package org.piangles.gateway;

import org.piangles.core.util.coding.JSON;

public final class Message
{
	private MessageType messageType;
	private String payload;
	
	public Message(MessageType messageType, Object payloadAsObj) throws Exception
	{
		this.messageType = messageType;
		this.payload = new String(JSON.getEncoder().encode(payloadAsObj));
	}

	public MessageType getMessageType()
	{
		return messageType;
	}

	public String getPayload()
	{
		return payload;
	}

	@Override
	public String toString()
	{
		return "Message [messageType=" + messageType + ", payload=" + payload + "]";
	}
}
