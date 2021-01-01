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

public final class SimpleResponse
{
	private static final String SUCCESS_MESSAGE = "Request was successfully processed.";
	private static final String FAILURE_MESSAGE = "Request failed to be processed.";
	private boolean appRequestSuccessful;
	private String appResponseMessage;
	
	public SimpleResponse(boolean appRequestSuccessful)
	{
		this(appRequestSuccessful, appRequestSuccessful?SUCCESS_MESSAGE:FAILURE_MESSAGE);
	}

	public SimpleResponse(boolean appRequestSuccessful, String appResponeMessage)
	{
		this.appRequestSuccessful = appRequestSuccessful;
		this.appResponseMessage = appResponeMessage;
	}

	public boolean isAppRequestSuccessful()
	{
		return appRequestSuccessful;
	}

	public String getAppResponseMessage()
	{
		return appResponseMessage;
	}

	@Override
	public String toString()
	{
		return "SimpleResponse [appRequestSuccessful=" + appRequestSuccessful + ", appResponeMessage=" + appResponseMessage + "]";
	}
}
