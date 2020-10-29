package org.piangles.gateway.handling.events;

import java.util.Map;

import org.piangles.backbone.services.msg.Event;
import org.piangles.backbone.services.msg.Topic;

public interface EventDispatcher
{
	public void dispatchAllEvents(Map<Event, Topic> topicEventMap) throws Exception;
}
