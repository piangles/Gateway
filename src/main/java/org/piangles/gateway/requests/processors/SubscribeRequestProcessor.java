/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.gateway.requests.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.msg.MessagingService;
import org.piangles.backbone.services.msg.Topic;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;
import org.piangles.gateway.requests.dto.SubscribeRequest;

public class SubscribeRequestProcessor extends AbstractRequestProcessor<SubscribeRequest, SimpleResponse>
{
	private MessagingService msgService = null;

	public SubscribeRequestProcessor()
	{
		super(Endpoints.Subscribe, SubscribeRequest.class, SimpleResponse.class);
		msgService = Locator.getInstance().getMessagingService();
	}

	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, Request request, SubscribeRequest subscribeRequest) throws Exception
	{
		boolean result = true;
		String message = "Subscription was successful.";
		List<Topic> topics = null;

		if (subscribeRequest.isUserTopics())
		{
			topics = msgService.getTopicsForUser(clientDetails.getSessionDetails().getUserId());
			if (topics == null)
			{
				result = false;
				message = "User does not have any associated topics.";
			}
		}
		else if (subscribeRequest.getAliases() != null)
		{
			topics = msgService.getTopicsForAliases(subscribeRequest.getAliases());
			if (topics == null)
			{
				result = false;
				message = "Alias does not have any associated topics.";
			}
		}
		else if (subscribeRequest.getTopic() != null)
		{
			/**
			 * The reason we make a call to Messaging service is to check
			 * 1. If the topic has parition (TODO: until we change the Subscribe Request to have Topic instead of just topicName)
			 * 2. Equally important if it log compacted.
			 */
			topics = new ArrayList<>();
			Topic topic = msgService.getTopic(subscribeRequest.getTopic());
			topics.add(topic);
		}
		else
		{
			result = false;
			message = "None of the mandatory fields are specified.";
		}

		/**
		 * Restart the notification processing manager to stop any previous
		 * event listeners and start a new one.
		 */
		if (topics != null)
		{
			Map<Topic, UUID> topicTraceIdMap = new HashMap<>();
			
			topics.stream().forEach(topic -> topicTraceIdMap.put(topic, request.getTraceId()));
			getEventProcessingManager().subscribeToTopics(topicTraceIdMap);
			getEventProcessingManager().restart();
		}

		return new SimpleResponse(result, message);
	}
}
