package org.piangles.gateway.handling.events;

import org.piangles.backbone.services.msg.Event;
import org.piangles.gateway.handling.ClientDetails;

public interface EventProcessor
{
	public void init(ClientDetails clientDetails);
	public String getType();
	public void process(Event event);
}
