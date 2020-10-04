package org.piangles.gateway.handling.notifcations.processors;

import org.piangles.gateway.handling.notifcations.dto.SampleMessage;

public class SampleMessageProcessor extends AbstractMessageProcessor<SampleMessage>
{
	public SampleMessageProcessor()
	{
		super(SampleMessage.class.getCanonicalName());
	}

	@Override
	public void processPayload(SampleMessage payload) throws Exception
	{
		
	}
}
