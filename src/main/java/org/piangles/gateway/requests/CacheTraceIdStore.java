package org.piangles.gateway.requests;

import org.piangles.core.resources.RedisCache;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.InMemoryConfigProvider;

public class CacheTraceIdStore implements TraceIdStore 
{
	private RedisCache redisCache = null;

	public CacheTraceIdStore() 
	{
		redisCache = ResourceManager.getInstance().getRedisCache(new InMemoryConfigProvider(, "c6a1426f-34b9-4f6f-9ff6-bb0c7f200c5b"));
	}

	@Override
	public void put(String traceId) throws Exception
	{
		redisCache.execute((jedis) ->
		{
			jedis.set(traceId, "");
			return null;
		});
	}

	@Override
	public boolean exists(String traceId) throws Exception
	{
		String found = redisCache.execute((jedis) -> jedis.get(traceId));

		return found != null;
	}
}
