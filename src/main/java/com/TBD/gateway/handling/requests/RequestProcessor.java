package com.TBD.gateway.handling.requests;

import com.TBD.gateway.dto.Request;
import com.TBD.gateway.dto.Response;
import com.TBD.gateway.handling.ClientDetails;

public interface RequestProcessor
{
	public String getEndpoint();
	public boolean isAsyncProcessor();
	public boolean shouldValidateSession();
	public Response processRequest(ClientDetails clientDetails, Request request) throws Exception;
}
