package com.TBD.gateway.dto;

public final class SimpleResponse
{
	private boolean appRequestSuccessful;
	private String appResponseMessage;
	
	public SimpleResponse(boolean appRequestSuccessful)
	{
		this(appRequestSuccessful, null);
	}

	public SimpleResponse(boolean appRequestSuccessful, String appResponeMessage)
	{
		this.appRequestSuccessful = appRequestSuccessful;
		this.appResponseMessage = appResponeMessage;
	}

	public boolean isAppRequestSuccessful()
	{
		return appRequestSuccessful;
	}

	public String getAppResponseMessage()
	{
		return appResponseMessage;
	}

	@Override
	public String toString()
	{
		return "SimpleResponse [appRequestSuccessful=" + appRequestSuccessful + ", appResponeMessage=" + appResponseMessage + "]";
	}
}
