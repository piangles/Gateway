package com.TBD.gateway.handling.requests.processors;

import com.TBD.gateway.dto.Ping;
import com.TBD.gateway.dto.Pong;
import com.TBD.gateway.handling.ClientDetails;
import com.TBD.gateway.handling.Endpoints;

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
