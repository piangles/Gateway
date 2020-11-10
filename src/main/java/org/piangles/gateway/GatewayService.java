package org.piangles.gateway;

public interface GatewayService
{
	public void init(String host, int port) throws Exception;
	public void startProcessingRequests() throws Exception;
}
