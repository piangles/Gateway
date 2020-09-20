package com.TBD.gateway.dto;

import java.util.List;

public class SubscribeRequest
{
	private String topic;
	private List<String> aliases;

	
	public SubscribeRequest(String topic)
	{
		this.topic = topic;
	}
	
	
	public SubscribeRequest(List<String> aliases)
	{
		this.aliases = aliases;
	}


	public List<String> getAliases()
	{
		return aliases;
	}
	
	public String getTopic()
	{
		return topic;
	}
}
