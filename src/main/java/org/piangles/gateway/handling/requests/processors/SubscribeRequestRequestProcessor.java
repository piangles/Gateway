package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.msg.Topic;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;
import org.piangles.gateway.handling.requests.dto.SubscribeRequest;

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
			getMessageProcessingManager().subscribeToTopic(new Topic(subscribeRequest.getTopic()));
			result = true;
		}
		else if (subscribeRequest.getAliases() != null)
		{
			getMessageProcessingManager().subscribeToAlias(subscribeRequest.getAliases());
			result = true;
		}

		return new SimpleResponse(result);
	}
}
