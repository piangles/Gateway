package org.piangles.gateway.handling.requests.processors;

import java.util.List;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.msg.MessagingService;
import org.piangles.backbone.services.msg.Topic;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;
import org.piangles.gateway.handling.requests.dto.SubscribeRequest;

public class SubscribeRequestProcessor extends AbstractRequestProcessor<SubscribeRequest, SimpleResponse>
{
	private MessagingService msgService = null;

	public SubscribeRequestProcessor()
	{
		super(Endpoints.SubscribeToUser.name(), SubscribeRequest.class);
		msgService = Locator.getInstance().getMessagingService();
	}

	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, SubscribeRequest subscribeRequest) throws Exception
	{
		boolean result = false;

		if (subscribeRequest.isUserTopics())
		{
			List<Topic> userTopics = msgService.getTopicsForUser(clientDetails.getSessionDetails().getUserId());
			getNotificationProcessingManager().subscribeToTopics(userTopics);
			getNotificationProcessingManager().start();
			result = true;
		}
		else if (subscribeRequest.getTopic() != null)
		{
			getNotificationProcessingManager().subscribeToTopic(new Topic(subscribeRequest.getTopic()));
			result = true;
		}
		else if (subscribeRequest.getAliases() != null)
		{
			getNotificationProcessingManager().subscribeToAlias(subscribeRequest.getAliases());
			result = true;
		}

		return new SimpleResponse(result);
	}
}
