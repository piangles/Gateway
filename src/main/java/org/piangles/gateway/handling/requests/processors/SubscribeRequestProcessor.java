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
		super(Endpoints.Subscribe.name(), SubscribeRequest.class);
		msgService = Locator.getInstance().getMessagingService();
	}

	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, SubscribeRequest subscribeRequest) throws Exception
	{
		boolean result = true;
		String message = "Subscription was successful.";

		if (subscribeRequest.isUserTopics())
		{
			List<Topic> userTopics = msgService.getTopicsForUser(clientDetails.getSessionDetails().getUserId());
			if (userTopics != null)
			{
				getEventProcessingManager().subscribeToTopics(userTopics);
			}
			else
			{
				result = false;
				message = "User does not have any associated topics.";
			}
		}
		else if (subscribeRequest.getAliases() != null)
		{
			List<Topic> aliasTopics = msgService.getTopicsForAliases(subscribeRequest.getAliases());

			getEventProcessingManager().subscribeToTopics(aliasTopics);
		}
		else if (subscribeRequest.getTopic() != null)
		{
			getEventProcessingManager().subscribeToTopic(new Topic(subscribeRequest.getTopic()));
		}
		else
		{
			result = false;
			message = "None of the mandatory fields are specified.";
		}

		/**
		 * Restart the notification processing manager to stop any previous
		 * event listeners and start a new one.
		 * TODO : May be we just need a refresh method.
		 */
		getEventProcessingManager().restart();

		return new SimpleResponse(result, message);
	}
}
