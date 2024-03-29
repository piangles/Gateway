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

public enum StatusCode
{
	 //Number are corresponding to HTTP Status Codes
	Success(200, "OK"),
	
	BadRequest(400, "Bad Request"),
	Unauthenticated(401, "Not Authenticated"),
	Unauthorized(403, "Not Authorized" ),
	NotFound(404, "Not Found"),
	RequestedFormatNotSupported(406, "Not Acceptable"),
	VersionMismatch(409, "Version Mismatch"),
	PayloadTooLarge(413, "Payload Too Large"),
	UnsupportedMedia(415, "Unsupported Media"),
	ValidationFailure(422, "Unprocessable Entity"),
	
	InternalError(500, "Internal Server Error");
	
	private final int code;
	private final String message;
	
	private StatusCode(int code, String message)
	{
		this.code = code;
		this.message = message;
	}
	
	public int getCode()
	{
		return code;
	}
	
	public String getMessage()
	{
		return  message;
	}
	
	@Override
	public String toString()
	{
		return code + " : " + message;
	}
}
