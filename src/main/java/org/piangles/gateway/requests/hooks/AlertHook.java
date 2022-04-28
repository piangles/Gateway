package org.piangles.gateway.requests.hooks;

import org.piangles.gateway.client.ClientDetails;

public interface AlertHook
{
	public void process(AlertDetails alertDetails, ClientDetails clientDetails);
}
