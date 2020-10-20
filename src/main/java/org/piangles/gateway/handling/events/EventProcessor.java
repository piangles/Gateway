package org.piangles.gateway.handling.events;

import org.piangles.backbone.services.msg.Event;
import org.piangles.gateway.handling.ClientDetails;

public interface EventProcessor
{
	public String getType();
	public void process(ClientDetails clientDetails, Event event);
}
