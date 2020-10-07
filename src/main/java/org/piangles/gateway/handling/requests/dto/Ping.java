package org.piangles.gateway.handling.requests.dto;

public class Ping
{
	private long sequenceNo;
	private long timeStamp;
	
	public Ping(long sequenceNo)
	{
		super();
		this.sequenceNo = sequenceNo;
		this.timeStamp = System.currentTimeMillis();
	}
	
	public long getSequenceNo()
	{
		return sequenceNo;
	}
	
	public long getTimeStamp()
	{
		return timeStamp;
	}
}