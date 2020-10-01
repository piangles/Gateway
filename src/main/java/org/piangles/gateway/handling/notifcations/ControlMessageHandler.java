package org.piangles.gateway.handling.notifcations;

import org.piangles.backbone.services.msg.ControlDetails;
import org.piangles.gateway.handling.ClientDetails;

public interface ControlMessageHandler
{
	public void init(ClientDetails clientDetails);
	public String getType();
	public void process(ControlDetails ccDetails);
}
