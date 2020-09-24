package org.piangles.gateway.handling.requests.processors;

import org.piangles.gateway.dto.Ping;
import org.piangles.gateway.dto.Pong;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;

public final class PingMessageProcessor extends AbstractRequestProcessor<Ping, Pong>
{
	public PingMessageProcessor()
	{
		super(Endpoints.Ping.name(), Ping.class);
	}

	@Override
	public Pong processRequest(ClientDetails clientDetails, Ping ping) throws Exception
	{
		return new Pong(ping.getSequenceNo(), ping.getTimeStamp());
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
