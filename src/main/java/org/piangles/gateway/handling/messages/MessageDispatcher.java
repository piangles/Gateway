package org.piangles.gateway.handling.messages;

import java.util.List;

import org.piangles.backbone.services.msg.Message;

public interface MessageDispatcher
{
	public void dispatchAllMessages(List<Message> messages) throws Exception;
}
