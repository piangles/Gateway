package org.piangles.gateway.requests;

/**
 * Checks if traceId is unique - the chance to a UUID repeating is 1 in 17 billion
 *if it repeats safe to assume this is a potential fraud threat 
 */
public interface TraceIdStore
{
	public void put(String traceId) throws Exception;
	
	public boolean exists(String traceId) throws Exception;
}
