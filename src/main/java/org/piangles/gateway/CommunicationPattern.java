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
 
 
 
package org.piangles.gateway;

public enum CommunicationPattern
{
	FireAndForget("Fire and Forget."),
	RequestResponse("Request is responded to synchronously in a blocking manner."),
	RequestAsynchronousResponse("Request is responded to asynchronously."),
	RequestForStream("Request is responded with success or failure message and subsequently one or more responses are streamed."),
	RequestForSubscription("Request for a subscritpion of events.");
	
	private String description;
	
	CommunicationPattern(String description)
	{
        this.description = description;
    }
	
    public String description()
    {
        return description;
    }
}
