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
 
 
 
package org.piangles.gateway.requests.dto;

import java.io.Serializable;

public final class SystemInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String osName = null;
	private String browserName = null;
	private String browserVersion = null; 
	private int screenWidthInPixels;
	private int screenHeightInPixels;
	
	public SystemInfo(String osName, String browserName, String browserVersion)
	{
		this.osName = osName;
		this.browserName = browserName;
		this.browserVersion = browserVersion;
	}

	public SystemInfo(String osName, String browserName, String browserVersion, int screenWidthInPixels, int screenHeightInPixels)
	{
		this.osName = osName;
		this.browserName = browserName;
		this.browserVersion = browserVersion;
	}

	public String getOsName()
	{
		return osName;
	}

	public String getBrowserName()
	{
		return browserName;
	}

	public String getBrowserVersion()
	{
		return browserVersion;
	}
	
	public int getScreenWidthInPixels()
	{
		return screenWidthInPixels;
	}

	public int getScreenHeightInPixels()
	{
		return screenHeightInPixels;
	}

	@Override
	public String toString()
	{
		return "SystemInfo [osName=" + osName + ", browserName=" + browserName + ", browserVersion=" + browserVersion + ", screenWidthInPixels=" + screenWidthInPixels + ", screenHeightInPixels="
				+ screenHeightInPixels + "]";
	}
}
