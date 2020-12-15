package org.piangles.gateway.requests.processors;

import org.piangles.core.stream.Stream;

public interface StreamRequestProcessor<AppReq, SI>
{
	public Stream<SI> createStream(AppReq appRequest); 
}
