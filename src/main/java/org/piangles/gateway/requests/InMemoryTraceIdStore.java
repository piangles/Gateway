package org.piangles.gateway.requests;

import java.util.HashMap;
import java.util.Map;


public class InMemoryTraceIdStore implements TraceIdStore
{
	private Map<String, String> traceIdMap = null;

	public InMemoryTraceIdStore() 
	{
		this.traceIdMap  = new HashMap<>();
	}

	@Override
	public void put(String traceId) 
	{
		//using empty string for value to optimize space complexity
		this.traceIdMap.put(traceId, "");
	}

	@Override
	public boolean exists(String traceId) 
	{
		return traceIdMap.get(traceId) != null;
	}
}
