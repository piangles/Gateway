package com.TBD.app.gateway.handling.requests;

import java.util.UUID;

import javax.xml.bind.ValidationException;

import com.TBD.app.gateway.dto.Request;
import com.TBD.app.gateway.dto.Response;
import com.TBD.app.gateway.handling.ClientDetails;
import com.TBD.app.gateway.handling.notifcations.ClientNotifier;
import com.TBD.appcore.locator.BackboneServiceLocator;
import com.TBD.backbone.services.logging.LoggingService;
import com.TBD.backbone.services.session.SessionManagementException;
import com.TBD.backbone.services.session.SessionManagementService;
import com.TBD.core.services.remoting.SessionAwareable;
import com.TBD.core.services.remoting.SessionDetails;
import com.TBD.core.services.remoting.Traceable;

public final class RequestProcessingThread extends Thread implements Traceable, SessionAwareable
{
	private ClientDetails clientDetails = null;
	private Request request = null;
	private Response response = null;
	private ClientNotifier clientNotifier = null;

	private RequestProcessor requestProcessor = null;
	private SessionManagementService sessionMgmtService = BackboneServiceLocator.getInstance().getSessionManagementService();
	protected LoggingService logger = BackboneServiceLocator.getInstance().getLoggingService();
	
	public RequestProcessingThread(ClientDetails clientDetails, Request request, RequestProcessor requestProcessor, ClientNotifier clientNotifier)
	{
		this.clientDetails = clientDetails;
		this.request = request;
		this.requestProcessor = requestProcessor;
		this.clientNotifier = clientNotifier;
	}
	
	@Override
	public void run()
	{
		try
		{
			logger.info("Processing request for Endpoint : " + request.getEndpoint());

			try
			{
				//TODO Validate the Request 
				RequestValidator.validate(clientDetails, request);
			}
			catch (ValidationException e)
			{
				logger.warn("Message receieved from userId : " + clientDetails.getSessionDetails().getUserId() + " is not valid.", e);
				response = new Response(request.getTraceId(), request.getEndpoint(), false, "Request failed validation because of : " + e.getMessage());
			}

			boolean validSession = false;
			if (requestProcessor.shouldValidateSession())
			{
				try
				{
					validSession = sessionMgmtService.isValid(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());
				}
				catch (SessionManagementException e)
				{
					logger.error("Unable to validate Session because of : " + e.getMessage(), e);
					response = new Response(request.getTraceId(), request.getEndpoint(), false, "Unable to validate Session because of : " + e.getMessage());
				}
			}
			else
			{
				validSession = true;
			}
			
			if (validSession)
			{
				//Finally the actual call to the RequestProcessor
				response = requestProcessor.processRequest(clientDetails, request);
			}
			else
			{
				response = new Response(request.getTraceId(), request.getEndpoint(), false, "Invalid sessionId.");
			}
		}
		catch(Exception e)
		{
			logger.warn("Exception while processing request because of : " + e.getMessage(), e);
			response = new Response(getTraceId(), request.getEndpoint(), false, "Request could not be processed successfully because of : " + e.getMessage());
		}
		ResponseProcessor.processResponse(clientDetails, response);
	}

	@Override
	public UUID getTraceId()
	{
		return request.getTraceId();
	}
	
	public Response getResponse()
	{
		return response;
	}
	
	public ClientNotifier getClientNotifier()
	{
		return clientNotifier;
	}

	@Override
	public SessionDetails getSessionDetails()
	{
		SessionDetails sessionDetails = null;
		if (!requestProcessor.shouldValidateSession() && clientDetails.getSessionDetails().getSessionId() != null)
		{
			throw new RuntimeException("RequestProcessor for endpoint " + requestProcessor.getEndpoint() + " has a sessionId but is coded for not validating session.");
		}
		else
		{
			sessionDetails = clientDetails.getSessionDetails();
		}
		return sessionDetails;
	}
}
