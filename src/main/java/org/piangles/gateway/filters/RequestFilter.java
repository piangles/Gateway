package org.piangles.gateway.filters;

import org.piangles.gateway.requests.dto.Request;

public interface RequestFilter
{
	public boolean proceed(Request request);
}
