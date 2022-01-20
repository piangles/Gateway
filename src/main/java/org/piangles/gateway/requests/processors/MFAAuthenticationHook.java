package org.piangles.gateway.requests.processors;

import org.piangles.gateway.client.ClientDetails;

public interface MFAAuthenticationHook
{
	public void process(String endpoint, ClientDetails clientDetails);
}
