package org.piangles.gateway.handling.requests;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.requests.dto.Request;
import org.piangles.gateway.handling.requests.dto.Response;

public interface RequestProcessor
{
	public String getEndpoint();
	public boolean isAsyncProcessor();
	public boolean shouldValidateSession();
	public Response processRequest(ClientDetails clientDetails, Request request) throws Exception;
}
