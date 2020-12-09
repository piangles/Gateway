package org.piangles.gateway.handling.requests.processors;

import java.util.Set;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.RequestRouter;
import org.piangles.gateway.handling.requests.dto.EmptyRequest;
import org.piangles.gateway.handling.requests.dto.Request;

public final class ListEndpointsRequestProcessor extends AbstractRequestProcessor<EmptyRequest, Set<String>>
{
	public ListEndpointsRequestProcessor()
	{
		super(Endpoints.ListEndpoints.name(), false, EmptyRequest.class);
	}
	
	@Override
	protected Set<String> processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		return RequestRouter.getInstance().getRegisteredEndpoints();
	}

	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
