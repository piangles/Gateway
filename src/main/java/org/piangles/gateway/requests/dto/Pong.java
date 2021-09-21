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

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pong
{
	@JsonProperty(required = true)
	private long sequenceNo;
	
	@JsonProperty(required = true)
	private long pingTimestamp;
	
	@JsonProperty(required = true)
	private long receivedTimestamp;
	
	public Pong(long sequenceNo, long pingTimestamp)
	{
		this.sequenceNo = sequenceNo;
		this.pingTimestamp = pingTimestamp;
		this.receivedTimestamp = System.currentTimeMillis();
	}

	public long getSequenceNo()
	{
		return sequenceNo;
	}

	public long getPingTimestamp()
	{
		return pingTimestamp;
	}

	public long getReceivedTimestamp()
	{
		return receivedTimestamp;
	}
}
