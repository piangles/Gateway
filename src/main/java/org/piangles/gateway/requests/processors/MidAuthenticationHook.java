package org.piangles.gateway.requests.processors;

import org.piangles.gateway.client.ClientDetails;

public interface MidAuthenticationHook
{
	public void process(ClientDetails clientDetails);
}
