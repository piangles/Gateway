package org.piangles.gateway.handling.events;

import java.util.List;

import org.piangles.backbone.services.msg.Event;

public interface EventDispatcher
{
	public void dispatchAllEvents(List<Event> events) throws Exception;
}
