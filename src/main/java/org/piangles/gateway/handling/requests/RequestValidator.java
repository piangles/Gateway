package org.piangles.gateway.handling.requests;

import javax.xml.bind.ValidationException;

import org.piangles.gateway.dto.Request;
import org.piangles.gateway.handling.ClientDetails;

/**
 * One of the checks here should be SessionId the client sends
 * should be the same as the one assigned by the server.
 * This may seems double check at this point because we are already
 * checking in Service call in org.piangles.core.services.remoting.rabbit.RequestProcessingThread
 * In reality, Services are very important to be avaiable and validation can prevent
 * any calls to Service the better.
 * 
 * Should even the request have SessionId?
 */
public class RequestValidator
{
	/**
	 * Client needs to send
	 * 1. traceId
	 * 2. sessionId for every request except LoginRequest
	 * 
	 * Move ValidationException or validation interface and Exception to Core.
	 * 
	 * DO NOT Check in this class till validation is complete
	 */
	public static void validate(ClientDetails clientDetails, Request request) throws ValidationException
	{
		
	}
}
