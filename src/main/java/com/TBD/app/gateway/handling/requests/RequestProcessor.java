package com.TBD.app.gateway.handling.requests;

import com.TBD.app.gateway.dto.Request;
import com.TBD.app.gateway.dto.Response;
import com.TBD.app.gateway.handling.ClientDetails;

public interface RequestProcessor
{
	public String getEndpoint();
	public boolean isAsyncProcessor();
	public boolean shouldValidateSession();
	public Response processRequest(ClientDetails clientDetails, Request request) throws Exception;
}
