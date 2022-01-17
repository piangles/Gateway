package org.piangles.gateway.requests;

import java.util.UUID;

import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.core.services.remoting.Traceable;

public final class HookProcessor extends Thread implements Traceable, SessionAwareable
{
	private UUID traceId = null;
	private SessionDetails sessionDetails = null;
	private Runnable runnable = null;
	
	public HookProcessor(UUID traceId, SessionDetails sessionDetails, Runnable runnable)
	{
		this.traceId = traceId;
		this.sessionDetails = sessionDetails;
		this.runnable = runnable;
	}
	
	@Override
	public void run()
	{
		this.runnable.run();
	}
	
	@Override
	public UUID getTraceId()
	{
		return traceId;
	}

	@Override
	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
}
