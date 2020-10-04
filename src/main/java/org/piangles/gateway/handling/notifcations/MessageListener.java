package org.piangles.gateway.handling.notifcations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Message;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.handling.ClientDetails;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class MessageListener implements Runnable
{
	private static final int DEFAULT_WAIT_TIME = 100;
	private static final int MAX_ERROR_LIMIT = 10;
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private ClientDetails clientDetails = null;
	private KafkaConsumer<String, String> consumer = null;
	private int errorCount = 0; 
	private final AtomicBoolean stop = new AtomicBoolean(false);

	public MessageListener(ClientDetails clientDetails, KafkaConsumer<String, String> consumer)
	{
		this.clientDetails = clientDetails;
		this.consumer = consumer;
	}
	
	@Override
	public void run()
	{
		//Start the while loop
		while (stop.get())
		{
			try
			{
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(DEFAULT_WAIT_TIME));
				for (ConsumerRecord<String, String> record : records)
				{
					List<Message> messages = new ArrayList<Message>();
					
					//Convert the String in Value to Message
					Message message = composeMessage(record.value());

					messages.add(message);
				}
			}
			catch (Exception e)
			{
				logger.error("Exception while polling / composingMessage:", e);
				errorCount = errorCount + 1;
				if (errorCount > MAX_ERROR_LIMIT)
				{
					logger.fatal("Message listener crossed the maximum limit of error : " + clientDetails);
					break;
				}
			}
		}
	}

	public void markForStopping()
	{
		stop.set(true);
	}
	
	private Message composeMessage(String messageAsStr) throws Exception
	{
		Message message = JSON.getDecoder().decode(messageAsStr.getBytes(), Message.class);
		JsonObject jsonObject = new Gson().toJsonTree(message.getPayload()).getAsJsonObject();
		Class<?> payloadClass = Class.forName(message.getPayloadType());
		Object payload = JSON.getDecoder().decode(jsonObject.toString().getBytes(), payloadClass);
		message.setPayload(payload);

		return message;
	}
}
