package org.piangles.gateway.requests.processors;

import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public class AbstractStreamingRequestProcessor<AppReq, AppResp> extends AbstractRequestProcessor<AppReq, SimpleResponse>
{
	public AbstractStreamingRequestProcessor(String endpoint, Class<AppReq> requestClass)
	{
		super(endpoint, requestClass, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, AppReq emptyRequest) throws Exception
	{
		return null;
	}
}
