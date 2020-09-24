package org.piangles.gateway.handling.requests;

import org.piangles.gateway.dto.Response;
import org.piangles.gateway.handling.ClientDetails;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.logging.LoggingService;
import com.TBD.core.util.coding.JSON;

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
