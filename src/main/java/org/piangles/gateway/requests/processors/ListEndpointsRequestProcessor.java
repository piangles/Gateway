package org.piangles.gateway.requests.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

public final class ListEndpointsRequestProcessor extends AbstractRequestProcessor<EmptyRequest, List<String>>
{
	private Map<String, Endpoints> metadataEndpoints = null;
	public ListEndpointsRequestProcessor()
	{
		super(Endpoints.ListEndpoints.name(), false, EmptyRequest.class, null);
		
		metadataEndpoints = new HashMap<>();
		populate(Endpoints.ListEndpoints);
		populate(Endpoints.EndpointSchema);
	}
	
	@Override
	protected List<String> processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		return RequestRouter.getInstance().getRegisteredEndpoints().
				stream().
				filter(ep -> !metadataEndpoints.containsKey(ep)).
				collect(Collectors.toList());
	}

	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
	
	private void populate(Endpoints endpoint)
	{
		metadataEndpoints.put(endpoint.name(), endpoint);		
	}
}
