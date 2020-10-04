package org.piangles.gateway.handling.requests.processors;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.EmptyRequest;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;

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
