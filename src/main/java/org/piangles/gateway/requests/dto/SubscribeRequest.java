package org.piangles.gateway.requests.dto;

import java.util.List;

public class SubscribeRequest
{
	private boolean userTopics = false;
	private String topic;//TODO Will need to change to Topic. Currently will work only for Default partition.
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
