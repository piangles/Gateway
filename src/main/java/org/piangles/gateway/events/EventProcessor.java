package org.piangles.gateway.events;

import org.piangles.backbone.services.msg.Event;
import org.piangles.gateway.requests.ClientDetails;

public interface EventProcessor
{
	public String getType();
	public void process(ClientDetails clientDetails, Event event);
}
