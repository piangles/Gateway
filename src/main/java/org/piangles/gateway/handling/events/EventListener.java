package org.piangles.gateway.handling.events;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.backbone.services.msg.Event;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.handling.ClientDetails;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class EventListener implements Runnable
{
	private static final int DEFAULT_WAIT_TIME = 100;
	private static final int MAX_ERROR_LIMIT = 10;
	private LoggingService logger = Locator.getInstance().getLoggingService();

	private ClientDetails clientDetails = null;
	private KafkaConsumer<String, String> consumer = null;
	private EventDispatcher eventDispatcher = null;
	private int errorCount = 0; 
	private final AtomicBoolean stopRequested = new AtomicBoolean(false);

	public EventListener(ClientDetails clientDetails, KafkaConsumer<String, String> consumer, EventDispatcher eventDispatcher)
	{
		this.clientDetails = clientDetails;
		this.consumer = consumer;
		this.eventDispatcher = eventDispatcher;
	}
	
	@Override
	public void run()
	{
		logger.info("Started listening for events for: " + clientDetails);
		while (!stopRequested.get())
		{
			try
			{
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(DEFAULT_WAIT_TIME));
				List<Event> events = new ArrayList<Event>();
				for (ConsumerRecord<String, String> record : records)
				{
					//Convert the String in Value to Event
					Event event = composeEvent(record.value());
					events.add(event);
				}
				eventDispatcher.dispatchAllEvents(events);
			}
			catch (Exception e)
			{
				logger.error("Exception while polling / composingEvent:", e);
				errorCount = errorCount + 1;
				if (errorCount > MAX_ERROR_LIMIT)
				{
					logger.fatal("Event listener crossed the maximum limit of error : " + clientDetails);
					break;
				}
			}
		}
		logger.info("Stopped listening for events for: " + clientDetails);
	}

	public void markForStopping()
	{
		logger.info("Stop listening for events requested for: " + clientDetails);
		stopRequested.set(true);
	}
	
	private Event composeEvent(String eventAsStr) throws Exception
	{
		Event event = JSON.getDecoder().decode(eventAsStr.getBytes(), Event.class);
		JsonObject jsonObject = new Gson().toJsonTree(event.getPayload()).getAsJsonObject();
		Class<?> payloadClass = Class.forName(event.getPayloadType());
		Object payload = JSON.getDecoder().decode(jsonObject.toString().getBytes(), payloadClass);
		event.setPayload(payload);

		return event;
	}
}
