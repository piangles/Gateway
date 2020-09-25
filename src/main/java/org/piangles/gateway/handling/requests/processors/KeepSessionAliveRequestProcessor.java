package org.piangles.gateway.handling.requests.processors;

import org.piangles.gateway.dto.EmptyRequest;
import org.piangles.gateway.dto.SimpleResponse;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;

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
