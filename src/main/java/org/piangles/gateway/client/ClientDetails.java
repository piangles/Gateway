/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.gateway.client;

import java.net.InetSocketAddress;

import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.gateway.ClientEndpoint;

public final class ClientDetails
{
	private InetSocketAddress remoteAddress;
	private ClientEndpoint clientEndpoint = null;
	private SessionDetails sessionDetails = null;
	private Location location = null;
	
	public ClientDetails(InetSocketAddress remoteAddress, ClientEndpoint clientEndpoint, SessionDetails sessionDetails, Location location)
	{
		this.remoteAddress = remoteAddress;
		this.clientEndpoint = clientEndpoint;
		this.sessionDetails = sessionDetails;
		this.location = location;
	}

	public ClientEndpoint getClientEndpoint()
	{
		return clientEndpoint;
	}

	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
	
	public InetSocketAddress getRemoteAddress()
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
	
	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}
	
	@Override
	public String toString()
	{
		return "ClientDetails [remoteAddress=" + remoteAddress + ", sessionDetails=" + sessionDetails + "]";
	}
}
