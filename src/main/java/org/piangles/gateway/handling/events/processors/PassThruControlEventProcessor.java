package org.piangles.gateway.handling.events.processors;

import org.piangles.backbone.services.msg.ControlDetails;
import org.piangles.core.util.coding.JSON;

public class PassThruControlEventProcessor extends AbstractEventProcessor<ControlDetails>
{
	public PassThruControlEventProcessor()
	{
		super(ControlDetails.class.getCanonicalName());
	}

	@Override
	public void processPayload(ControlDetails controlDetails) throws Exception
	{
		getClientDetails().getClientEndpoint().sendString(new String(JSON.getEncoder().encode(controlDetails)));
	}
}
