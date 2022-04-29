package org.piangles.gateway.requests;

import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.hooks.AlertDetails;

public interface AlertManager
{
	public void process(AlertDetails alertDetails, ClientDetails clientDetails);
}
