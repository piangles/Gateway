package org.piangles.gateway.handling.requests.processors;

import org.piangles.gateway.dto.SimpleResponse;
import org.piangles.gateway.dto.SubscribeRequest;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;

public class SubscribeRequestRequestProcessor extends AbstractRequestProcessor<SubscribeRequest, SimpleResponse>
{
	public SubscribeRequestRequestProcessor()
	{
		super(Endpoints.Subscribe.name(), SubscribeRequest.class);
	}

	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, SubscribeRequest subscribeRequest) throws Exception
	{
		boolean result = false;

		if (subscribeRequest.getTopic() != null)
		{
			getClientNotifier().subscribeToTopic(subscribeRequest.getTopic());
			result = true;
		}
		else if (subscribeRequest.getAliases() != null)
		{
			getClientNotifier().subscribeToAlias(subscribeRequest.getAliases());
			result = true;
		}

		return new SimpleResponse(result);
	}
}
