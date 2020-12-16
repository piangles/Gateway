package org.piangles.gateway.requests.processors;

import org.piangles.core.stream.Stream;

public interface StreamAcquirer<AppReq, SI>
{
	public Enum<?> getEndpoint();
	public Class<AppReq> getRequestClass();
	public Stream<SI> acquireStream(AppReq appRequest) throws Exception; 
}
