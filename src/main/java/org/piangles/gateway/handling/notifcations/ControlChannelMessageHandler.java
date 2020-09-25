package org.piangles.gateway.handling.notifcations;

import org.piangles.gateway.handling.ClientDetails;

public interface ControlChannelMessageHandler
{
	public void init(ClientDetails clientDetails);
	public String getType();
	public void process(ControlChannelMessage ccMessage);
}
