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

import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.core.util.reflect.TypeToken;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

public class GetUserPreferenceRequestProcessor extends AbstractRequestProcessor<EmptyRequest, Map<String,Object>>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public GetUserPreferenceRequestProcessor()
	{
		super(Endpoints.GetUserPreferences, EmptyRequest.class, new TypeToken<Map<String, Object>>() {}.getActualClass());
	}
	
	@Override
	protected Map<String, Object> processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		return upService.retrieveUserPreference(clientDetails.getSessionDetails().getUserId()).getNVPair(); 
	}
}
