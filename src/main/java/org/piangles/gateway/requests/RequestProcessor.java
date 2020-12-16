package org.piangles.gateway.requests;

import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;

public interface RequestProcessor
{
	public Enum<?> getEndpoint();
	public CommunicationPattern getCommunicationPattern(); 
	public boolean shouldValidateSession();
	public Class<?> getRequestClass();
	public Class<?> getResponseClass();
	public Response processRequest(ClientDetails clientDetails, Request request) throws Exception;
}
