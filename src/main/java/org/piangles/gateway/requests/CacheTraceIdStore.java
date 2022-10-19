package org.piangles.gateway.requests;

import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.core.resources.RedisCache;
import org.piangles.core.resources.ResourceManager;
import org.piangles.gateway.GatewayService;

public class CacheTraceIdStore implements TraceIdStore 
{
	private RedisCache redisCache = null;
	private static final String GATEWAY_COMPONENT_ID = "4f1fc058-75d9-4956-a5df-da4697c4e5b3";

	public CacheTraceIdStore() throws Exception 
	{
		redisCache = ResourceManager.getInstance().getRedisCache(new DefaultConfigProvider(GatewayService.NAME, GATEWAY_COMPONENT_ID));
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
