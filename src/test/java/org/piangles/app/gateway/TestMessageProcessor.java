package org.piangles.app.gateway;

import org.piangles.backbone.services.msg.Message;
import org.piangles.core.util.coding.JSON;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TestMessageProcessor
{
	public static void main(String[] args) throws Exception
	{
		String messageAsStr = "{\"type\":\"Control\",\"primaryKey\":\"1(This is specific to app)\",\"payloadType\":\"org.piangles.backbone.services.msg.ControlDetails\",\"payload\":{\"type\":\"Hello World\",\"action\":\"Add\",\"content\":\"This is the content\"}}";

		Message message = JSON.getDecoder().decode(messageAsStr.getBytes(), Message.class);
		JsonObject jsonObject = new Gson().toJsonTree(message.getPayload()).getAsJsonObject();
		Class<?> payloadClass = Class.forName(message.getPayloadType());
		Object payload = JSON.getDecoder().decode(jsonObject.toString().getBytes(), payloadClass);
		message.setPayload(payload);
		
		System.out.println(message);
	}
}
