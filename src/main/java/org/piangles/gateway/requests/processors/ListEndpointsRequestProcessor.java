package org.piangles.gateway.requests.processors;

import java.util.Set;

import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

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
