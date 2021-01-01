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
