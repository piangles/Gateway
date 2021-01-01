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
 
 
 
package org.piangles.gateway.events.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.gateway.Message;
import org.piangles.gateway.MessageType;
import org.piangles.gateway.events.EventProcessor;
import org.piangles.gateway.requests.ClientDetails;

public class PassThruNotificationEventProcessor implements EventProcessor
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private String type = null;

	public PassThruNotificationEventProcessor(String type)
	{
		this.type = type;
	}
	
	@Override
	public String getType()
	{
		return type;
	}

	@Override
	public void process(ClientDetails clientDetails, Event event)
	{
		try
		{
			clientDetails.getClientEndpoint().sendMessage(new Message(MessageType.Event, event));
		}
		catch (Exception e)
		{
			logger.info("Unable to process event : " + event, e);
		}		
	}
}
