package org.piangles.gateway.handling.messages;

import org.piangles.backbone.services.msg.Message;
import org.piangles.gateway.handling.ClientDetails;

public interface MessageProcessor
{
	public void init(ClientDetails clientDetails);
	public String getType();
	public void process(Message message);
}
