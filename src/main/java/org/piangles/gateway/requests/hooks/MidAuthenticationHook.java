package org.piangles.gateway.requests.hooks;

import org.piangles.gateway.client.ClientDetails;

public interface MidAuthenticationHook
{
	public void process(String endpoint, ClientDetails clientDetails);
}
