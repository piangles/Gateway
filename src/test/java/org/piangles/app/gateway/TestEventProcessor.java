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
 
 
 
package org.piangles.app.gateway;

import org.piangles.backbone.services.msg.Event;
import org.piangles.core.util.coding.JSON;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TestEventProcessor
{
	public static void main(String[] args) throws Exception
	{
		String eventAsStr = "{\"type\":\"Control\",\"primaryKey\":\"1(This is specific to app)\",\"payloadType\":\"org.piangles.backbone.services.msg.ControlDetails\",\"payload\":{\"type\":\"Hello World\",\"action\":\"Add\",\"content\":\"This is the content\"}}";

		Event event = JSON.getDecoder().decode(eventAsStr.getBytes(), Event.class);
		JsonObject jsonObject = new Gson().toJsonTree(event.getPayload()).getAsJsonObject();
		Class<?> payloadClass = Class.forName(event.getPayloadType());
		Object payload = JSON.getDecoder().decode(jsonObject.toString().getBytes(), payloadClass);
		event.setPayload(payload);
		
		System.out.println(event);
	}
}
