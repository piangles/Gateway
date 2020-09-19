package com.TBD.gateway.handling.requests.processors;

import com.TBD.gateway.dto.EmptyRequest;
import com.TBD.gateway.dto.SimpleResponse;
import com.TBD.gateway.handling.ClientDetails;
import com.TBD.gateway.handling.Endpoints;

public final class KeepSessionAliveRequestProcessor extends AbstractRequestProcessor<EmptyRequest, SimpleResponse>
{
	public KeepSessionAliveRequestProcessor()
	{
		super(Endpoints.KeepSessionAlive.name(), EmptyRequest.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, EmptyRequest emptyRequest) throws Exception
	{
		return new SimpleResponse(true);
	}
}
