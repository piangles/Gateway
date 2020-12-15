package org.piangles.gateway.requests.processors;

import org.piangles.core.stream.Stream;

public interface StreamCreator<AppReq, SI>
{
	public String getEndpoint();
	public Class<AppReq> getRequestClass();
	public Stream<SI> createStream(AppReq appRequest) throws Exception; 
}
