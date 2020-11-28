package org.piangles.gateway.handling.requests.dto;

public final class SimpleResponse
{
	private static final String SUCCESS_MESSAGE = "Request was successfully processed.";
	private static final String FAILURE_MESSAGE = "Request failed to be processed.";
	private boolean appRequestSuccessful;
	private String appResponseMessage;
	
	public SimpleResponse(boolean appRequestSuccessful)
	{
		this(appRequestSuccessful, appRequestSuccessful?SUCCESS_MESSAGE:FAILURE_MESSAGE);
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
