package org.piangles.gateway.events;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;

public class KafkaConsumerManager
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	
	private static KafkaConsumerManager self = null;
	
	private Map<KafkaConsumer<String, String>, Long> consumerThreadMap = null;
	private Map<Long, KafkaConsumer<String, String>> closedConsumerMap = null;

	private KafkaConsumerManager()
	{
		consumerThreadMap = new HashMap<>();
		closedConsumerMap = new HashMap<>();
	}
	
	public static synchronized final KafkaConsumerManager getInstance()
	{
		if (self == null)
		{
			self = new KafkaConsumerManager();
		}
		
		return self;
	}
	
	public synchronized void addNewConsumer(KafkaConsumer<String, String> consumer)
	{
		consumerThreadMap.put(consumer, Thread.currentThread().getId());
	}
	
	public synchronized void closeOrMarkForClose(KafkaConsumer<String, String> consumer)
	{
		if (consumer != null)
		{
			long currentThreadId = Thread.currentThread().getId();
			Long threadId = consumerThreadMap.get(consumer);
			
			if (threadId != null && currentThreadId == threadId.longValue())
			{
				closeConsumer(consumer);
			}
			else
			{
				consumerThreadMap.remove(consumer);
				closedConsumerMap.put(threadId, consumer);
			}
		}
	}
	
	public synchronized void closeAnyMarked()
	{
		closeConsumer(closedConsumerMap.remove(Thread.currentThread().getId()));
	}

	private void closeConsumer(KafkaConsumer<String, String> consumer)
	{
		try
		{
			if (consumer != null)
			{
				consumer.close();
			}
		}
		catch(Exception e)
		{
			logger.warn("Unable to close KafkaConsumer because of: " + e.getMessage(), e);
		}
	}
}
