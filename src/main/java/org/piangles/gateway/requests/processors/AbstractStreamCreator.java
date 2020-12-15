package org.piangles.gateway.requests.processors;

public abstract class AbstractStreamCreator<AppReq, SI> implements StreamCreator<AppReq, SI>
{
	private String endpoint = null;
	private Class<AppReq> requestClass = null;
	
	public AbstractStreamCreator(String endpoint, Class<AppReq> requestClass)
	{
		this.endpoint = endpoint;
		this.requestClass = requestClass;
	}
	
	@Override
	public String getEndpoint()
	{
		return endpoint;
	}
	
	@Override
	public Class<AppReq> getRequestClass()
	{
		return requestClass;
	}
}
