package com.TBD.gateway.handling.notifcations;

import com.TBD.gateway.handling.ClientDetails;

public interface ControlChannelMessageHandler
{
	public void init(ClientDetails clientDetails);
	public String getType();
	public void process(ControlChannelMessage ccMessage);
}
