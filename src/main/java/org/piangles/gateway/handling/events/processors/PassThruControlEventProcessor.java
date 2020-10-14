package org.piangles.gateway.handling.events.processors;

import org.piangles.backbone.services.msg.ControlDetails;
import org.piangles.gateway.Message;
import org.piangles.gateway.MessageType;

public class PassThruControlEventProcessor extends AbstractEventProcessor<ControlDetails>
{
	public PassThruControlEventProcessor()
	{
		super(ControlDetails.class.getCanonicalName());
	}

	@Override
	public void processPayload(ControlDetails controlDetails) throws Exception
	{
		getClientDetails().getClientEndpoint().sendMessage(new Message(MessageType.Event, controlDetails));
	}
}
