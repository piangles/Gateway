package org.piangles.gateway.requests.processors;

import org.piangles.gateway.client.ClientDetails;

public interface PostAuthenticationHook
{
	public void process(ClientDetails clientDetails);
}
