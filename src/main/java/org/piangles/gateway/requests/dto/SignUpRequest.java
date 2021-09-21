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

public class SignUpRequest
{
	@JsonProperty(required = true)
	private String firstName = null;
	
	@JsonProperty(required = true)
	private String lastName = null;
	
	@JsonProperty(required = true)
	private String emailId = null;
	
	@JsonProperty(required = false)
	private String phoneNo = null;
	
	@JsonProperty(required = true)
	private String password = null;
	
	public SignUpRequest(String firstName, String lastName, String emailId, String phoneNo, String password)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailId = emailId;
		this.phoneNo = phoneNo;
		this.password = password;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public String getEmailId()
	{
		return emailId;
	}
	
	public String getPhoneNo()
	{
		return phoneNo;
	}
	
	public String getPassword()
	{
		return password;
	}
}
