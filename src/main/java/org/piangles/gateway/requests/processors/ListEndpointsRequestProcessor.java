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
 
 
 
package org.piangles.gateway.requests.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.piangles.core.util.reflect.TypeToken;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

public final class ListEndpointsRequestProcessor extends AbstractRequestProcessor<EmptyRequest, List<String>>
{
	private Map<String, Endpoints> metadataEndpoints = null;
	public ListEndpointsRequestProcessor()
	{
		super(Endpoints.ListEndpoints, CommunicationPattern.RequestResponse, EmptyRequest.class, new TypeToken<List<String>>() {}.getActualClass());
		
		metadataEndpoints = new HashMap<>();
		populate(Endpoints.ListEndpoints);
		populate(Endpoints.EndpointMetadata);
	}
	
	@Override
	protected List<String> processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		return RequestRouter.getInstance().getRegisteredEndpoints().
				stream().
				filter(ep -> !metadataEndpoints.containsKey(ep)).
				collect(Collectors.toList());
	}

	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
	
	private void populate(Endpoints endpoint)
	{
		metadataEndpoints.put(endpoint.name(), endpoint);		
	}
}
