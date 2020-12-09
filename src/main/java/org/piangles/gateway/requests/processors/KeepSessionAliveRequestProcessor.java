package org.piangles.gateway.requests.processors;

import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public final class KeepSessionAliveRequestProcessor extends AbstractRequestProcessor<EmptyRequest, SimpleResponse>
{
	public KeepSessionAliveRequestProcessor()
	{
		super(Endpoints.KeepSessionAlive.name(), EmptyRequest.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		return new SimpleResponse(true);
	}
}
