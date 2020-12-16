package org.piangles.gateway.requests.processors;

public abstract class AbstractStreamAcquirer<AppReq, SI> implements StreamAcquirer<AppReq, SI>
{
	private Enum<?> endpoint = null;
	private Class<AppReq> requestClass = null;
	
	public AbstractStreamAcquirer(Enum<?> endpoint, Class<AppReq> requestClass)
	{
		this.endpoint = endpoint;
		this.requestClass = requestClass;
	}
	
	@Override
	public Enum<?> getEndpoint()
	{
		return endpoint;
	}
	
	@Override
	public Class<AppReq> getRequestClass()
	{
		return requestClass;
	}
}
