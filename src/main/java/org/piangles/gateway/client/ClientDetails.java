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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.gateway.ClientEndpoint;

public final class ClientDetails
{
	private InetSocketAddress remoteAddress;
	private ClientEndpoint clientEndpoint = null;
	
	private SessionDetails sessionDetails = null;
	private long inactivityExpiryTimeInSeconds = 0L;
	private long lastLoggedInTimestamp = 0L;
	
	private AtomicLong lastAccessed = null;
	private Location location = null;
	private Map<String, Object> applicationDataMap;
	
	public ClientDetails(InetSocketAddress remoteAddress, ClientEndpoint clientEndpoint, SessionDetails sessionDetails, long inactivityExpiryTimeInSeconds, long lastLoggedInTimestamp, Location location)
	{
		this.remoteAddress = remoteAddress;
		this.clientEndpoint = clientEndpoint;

		this.sessionDetails = sessionDetails;
		this.inactivityExpiryTimeInSeconds = inactivityExpiryTimeInSeconds;
		this.lastLoggedInTimestamp = lastLoggedInTimestamp;
		
		this.lastAccessed = new AtomicLong(); 
		this.location = location;
		this.applicationDataMap = new HashMap<>();
	}

	public ClientEndpoint getClientEndpoint()
	{
		return clientEndpoint;
	}

	public SessionDetails getSessionDetails()
	{
		return sessionDetails;
	}
	
	public long getInactivityExpiryTimeInSeconds()
	{
		return inactivityExpiryTimeInSeconds;
	}

	public long getLastLoggedInTimestamp()
	{
		return lastLoggedInTimestamp;
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

	public Object getApplicationData(String name)
	{
		return applicationDataMap.get(name);
	}

	public void putApplicationData(String name, Object value)
	{
		applicationDataMap.put(name, value);
	}

	public void markLastAccessed()
	{
		lastAccessed.set(System.currentTimeMillis());
	}
	
	public boolean hasSessionExpired()
	{
		return ((System.currentTimeMillis() - lastAccessed.get()) >= (inactivityExpiryTimeInSeconds*1000));
	}
	
	
	@Override
	public String toString()
	{
		return "ClientDetails [remoteAddress=" + remoteAddress + ", sessionDetails=" + sessionDetails + "]";
	}
}
