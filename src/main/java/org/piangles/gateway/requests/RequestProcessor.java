package org.piangles.gateway.requests;

import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;

public interface RequestProcessor
{
	public String getEndpoint();
	public boolean isAsyncProcessor();
	public boolean shouldValidateSession();
	public Class<?> getRequestClass();
	public Class<?> getResponseClass();
	public Response processRequest(ClientDetails clientDetails, Request request) throws Exception;
}
