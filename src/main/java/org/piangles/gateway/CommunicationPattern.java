package org.piangles.gateway;

public enum CommunicationPattern
{
	Command("Fire and Forget."),
	RequestResponse("Request is responded to synchronously in a blocking manner."),
	RequestAsynchronousResponse("Request is responded to asynchronously."),
	RequestForStream("Request is responded with success or failure message and subsequently one or more responses are streamed."),
	RequestForSubscription("Request for a subscritpion of events.");
	
	private String description;
	
	CommunicationPattern(String description)
	{
        this.description = description;
    }
	
    public String description()
    {
        return description;
    }
}
