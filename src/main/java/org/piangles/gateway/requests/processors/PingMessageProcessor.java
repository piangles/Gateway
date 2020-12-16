package org.piangles.gateway.requests.processors;

import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.Ping;
import org.piangles.gateway.requests.dto.Pong;
import org.piangles.gateway.requests.dto.Request;

public final class PingMessageProcessor extends AbstractRequestProcessor<Ping, Pong>
{
	public PingMessageProcessor()
	{
		super(Endpoints.Ping, Ping.class, Pong.class);
	}

	@Override
	protected Pong processRequest(ClientDetails clientDetails, Request request, Ping ping) throws Exception
	{
		return new Pong(ping.getSequenceNo(), ping.getTimeStamp());
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
