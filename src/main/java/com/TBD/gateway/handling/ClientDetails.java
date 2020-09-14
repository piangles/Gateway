package com.TBD.gateway.handling;

import java.net.InetSocketAddress;

import com.TBD.core.services.remoting.SessionDetails;
import com.TBD.gateway.ClientEndpoint;

public final class ClientDetails
{
	private InetSocketAddress remoteAddress;
	private ClientEndpoint clientEndpoint = null;
	private SessionDetails sessionDetails = null;
	
	ClientDetails(InetSocketAddress remoteAddress, ClientEndpoint clientEndpoint, SessionDetails sessionDetails)
	{
		this.remoteAddress = remoteAddress;
		this.clientEndpoint = clientEndpoint;
		this.sessionDetails = sessionDetails;
	}

	public ClientEndpoint getClientEndpoint()
	{
		return clientEndpoint;
	}

	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
	
	InetSocketAddress getRemoteAddress()
	{
		return remoteAddress;
	}
	
	public String getIPAddress()
	{
		return remoteAddress.getAddress().getHostAddress();
	}

	public String getHostName()
	{
		return remoteAddress.getHostName();
	}
	
	public int getPort()
	{
		return remoteAddress.getPort();
	}

	@Override
	public String toString()
	{
		return "ClientDetails [remoteAddress=" + remoteAddress + ", sessionDetails=" + sessionDetails + "]";
	}
}
