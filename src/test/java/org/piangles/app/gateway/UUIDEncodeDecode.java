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
 
 
 
package org.piangles.app.gateway;

import java.util.UUID;

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.requests.dto.Request;

public class UUIDEncodeDecode
{
	public static void main(String[] args)
	{
		try
		{
			Request req = new Request(UUID.randomUUID().toString(), null, "Dummy");
			System.out.println("Trace Id : " + req.getTraceId());
			String traceId = req.getTraceId().toString();
			String reqAsString = new String(JSON.getEncoder().encode(req));
			System.out.println("ReqAsString : " + reqAsString);
			Request reqDecoded = JSON.getDecoder().decode(reqAsString.getBytes(), Request.class);
			System.out.println("Decodecd Trace Id : " + reqDecoded.getTraceId());
			System.out.println("Are TraceId equals : " + req.getTraceId().equals(reqDecoded.getTraceId()));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
