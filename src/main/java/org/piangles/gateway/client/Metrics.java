package org.piangles.gateway.client;

import java.util.HashMap;
import java.util.Map;

public final class Metrics
{
	private Map<String, Integer> endpointCountMap = null;
	
	public Metrics()
	{
		endpointCountMap = new HashMap<>();
	}
	
	public synchronized void increment(String endpoint)
	{
		int count = getCount(endpoint);
		
		count = count + 1;
		endpointCountMap.put(endpoint, count);
	}
	
	public synchronized int getCount(String endpoint)
	{
		Integer count = endpointCountMap.get(endpoint);
		
		if (count == null)
		{
			count = new Integer(0);
		}
		
		return count;
	}
	
	public Map<String, Integer> getEndpointCountMap()
	{
		return endpointCountMap;
	}

	@Override
	public String toString()
	{
		return "Metrics [endpointCountMap=" + endpointCountMap + "]";
	}
}
