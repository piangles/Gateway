package org.piangles.gateway.dto;

public class Pong
{
	private long sequenceNo;
	private long pingTimeStamp;
	private long receivedTimeStamp;
	
	public Pong(long sequenceNo, long pingTimeStamp)
	{
		this.sequenceNo = sequenceNo;
		this.pingTimeStamp = pingTimeStamp;
		this.receivedTimeStamp = System.currentTimeMillis();
	}

	public long getSequenceNo()
	{
		return sequenceNo;
	}

	public long getPingTimeStamp()
	{
		return pingTimeStamp;
	}

	public long getReceivedTimeStamp()
	{
		return receivedTimeStamp;
	}
}
