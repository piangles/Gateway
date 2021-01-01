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
 
 
 
package org.piangles.gateway;

import org.piangles.core.util.coding.JSON;

public final class Message
{
	private MessageType messageType;
	private String payload;
	
	public Message(MessageType messageType, Object payloadAsObj) throws Exception
	{
		this.messageType = messageType;
		this.payload = new String(JSON.getEncoder().encode(payloadAsObj));
	}

	public MessageType getMessageType()
	{
		return messageType;
	}

	public String getPayload()
	{
		return payload;
	}

	@Override
	public String toString()
	{
		return "Message [messageType=" + messageType + ", payload=" + payload + "]";
	}
}
