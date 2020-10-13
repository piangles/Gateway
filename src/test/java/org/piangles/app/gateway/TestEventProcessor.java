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
