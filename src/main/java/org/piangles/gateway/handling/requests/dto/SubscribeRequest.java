package org.piangles.gateway.handling.requests.dto;

import java.util.List;

public class SubscribeRequest
{
	private boolean userTopics;
	private String topic;
	private List<String> aliases;

	public SubscribeRequest()
	{
		//This will create topics for UserId
		this.userTopics = true;
	}
	
	public SubscribeRequest(String topic)
	{
		this.topic = topic;
	}
	
	public SubscribeRequest(List<String> aliases)
	{
		this.aliases = aliases;
	}

	public boolean isUserTopics()
	{
		return userTopics;
	}
	
	public String getTopic()
	{
		return topic;
	}

	public List<String> getAliases()
	{
		return aliases;
	}
}
