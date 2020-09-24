package org.piangles.gateway;

public interface GatewayService
{
	public void init(int port) throws Exception;
	public void startProcessingRequests() throws Exception;
}
