package com.TBD.gateway.handling.requests;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.logging.LoggingService;
import com.TBD.core.util.coding.JSON;
import com.TBD.gateway.dto.Response;
import com.TBD.gateway.handling.ClientDetails;

public class ResponseProcessor
{	
	private static LoggingService logger = Locator.getInstance().getLoggingService();
	
	public static void processResponse(ClientDetails clientDetails, Response response)
	{
		if (response.isRequestSuccessful())
		{
			logger.info("Request was processed successfully.");
		}
		else
		{
			logger.warn("Request could not be processed successfully because of : " + response.getErrorMessage());
		}
		
		try
		{
			clientDetails.getClientEndpoint().sendString(new String(JSON.getEncoder().encode(response)));
			logger.info("Response sent to client successfully.");
		}
		catch (Exception e)
		{
			logger.error("Unable to send client response because of : ", e);
		}
	}
}
