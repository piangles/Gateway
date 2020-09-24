package org.piangles.gateway.handling.requests;

import org.piangles.gateway.dto.Request;
import org.piangles.gateway.dto.Response;
import org.piangles.gateway.handling.ClientDetails;

public interface RequestProcessor
{
	public String getEndpoint();
	public boolean isAsyncProcessor();
	public boolean shouldValidateSession();
	public Response processRequest(ClientDetails clientDetails, Request request) throws Exception;
}
