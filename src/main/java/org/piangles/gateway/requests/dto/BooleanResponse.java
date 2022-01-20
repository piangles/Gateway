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

public final class BooleanResponse
{
	private static final String TRUE_MESSAGE = "Criteria based query was satisfied.";
	
	@JsonProperty(required = true)
	private boolean criteriaSatisfied;
	
	@JsonProperty(required = true)
	private String message;
	
	public BooleanResponse()
	{
		this(true, TRUE_MESSAGE);
	}

	public BooleanResponse(boolean criteriaSatisfied, String message)
	{
		this.criteriaSatisfied = criteriaSatisfied;
		this.message = message;
	}

	public boolean isCriteriaSatisfied()
	{
		return criteriaSatisfied;
	}

	public String getMessage()
	{
		return message;
	}

	@Override
	public String toString()
	{
		return "BooleanResponse [CriteriaSatisfied=" + criteriaSatisfied + ", message=" + message + "]";
	}
}