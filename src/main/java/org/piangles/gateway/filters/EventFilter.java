package org.piangles.gateway.filters;

import org.piangles.backbone.services.msg.Event;

public interface EventFilter
{
	public boolean proceed(Event event);
}
