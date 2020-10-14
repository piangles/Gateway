package org.piangles.gateway.handling.requests;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.gateway.Message;
import org.piangles.gateway.MessageType;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.requests.dto.Response;

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
			clientDetails.getClientEndpoint().sendMessage(new Message(MessageType.Response, response));
			logger.info("Response sent to client successfully.");
		}
		catch (Exception e)
		{
			logger.error("Unable to send client response because of : ", e);
		}
	}
}
