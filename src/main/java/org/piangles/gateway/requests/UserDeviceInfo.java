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
 
 
package org.piangles.gateway.requests;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import org.piangles.gateway.requests.dto.SystemInfo;

public final class UserDeviceInfo implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String userId = null;
	private String hostName = null;
	private String ipAddress = null;
	private SystemInfo systemInfo = null;
	private LocalDate loggedInDate = null;
	private Date loggedInTimestamp = null;
	
	public UserDeviceInfo(String userId, String hostName, String ipAddress, SystemInfo systemInfo)
	{
		this.userId = userId;
		this.hostName = hostName;
		this.ipAddress = ipAddress;
		this.systemInfo = systemInfo;
		this.loggedInDate = LocalDate.now();
		this.loggedInTimestamp = new Date();
	}

	public String getUserId()
	{
		return userId;
	}

	public String getHostName()
	{
		return hostName;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public SystemInfo getSystemInfo()
	{
		return systemInfo;
	}

	public LocalDate getLoggedInDate()
	{
		return loggedInDate;
	}

	public Date getLoggedInTimestamp()
	{
		return loggedInTimestamp;
	}
}
