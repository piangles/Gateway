package com.TBD.gateway.handling.requests.processors;

import com.TBD.gateway.Constants;
import com.TBD.gateway.dto.Ping;
import com.TBD.gateway.dto.Pong;
import com.TBD.gateway.handling.ClientDetails;

public final class PingMessageProcessor extends AbstractRequestProcessor<Ping, Pong>
{
	public PingMessageProcessor()
	{
		super(Constants.ENDPOINT_PING, Ping.class);
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
