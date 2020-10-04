package org.piangles.gateway.handling.messages.processors;

import org.piangles.gateway.handling.messages.dto.SampleMessage;

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
