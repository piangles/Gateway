package org.piangles.gateway.handling.messages.processors;

import org.piangles.backbone.services.msg.ControlDetails;

public class PassThruControlProcessor extends AbstractMessageProcessor<ControlDetails>
{
	public PassThruControlProcessor()
	{
		super(ControlDetails.class.getCanonicalName());
	}

	@Override
	public void processPayload(ControlDetails controlDetails) throws Exception
	{
		getClientDetails().getClientEndpoint().sendString(controlDetails.toString());
	}
}
