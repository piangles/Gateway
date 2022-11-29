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

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreferences;
import org.piangles.backbone.services.prefs.UserPreferencesService;
import org.piangles.core.expt.ValidationException;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public class UpdateUserPreferencesRequestProcessor extends AbstractRequestProcessor<UserPreferences, SimpleResponse>
{
	private UserPreferencesService upService = Locator.getInstance().getUserPreferencesService();
	
	public UpdateUserPreferencesRequestProcessor()
	{
		super(Endpoints.UpdateUserPreferences, UserPreferences.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, UserPreferences userPrefs) throws Exception
	{
		if (!clientDetails.getSessionDetails().getUserId().equalsIgnoreCase(userPrefs.getUserId()))
		{
			throw new ValidationException("UserPreferences does not belong to current user.");
		}
		
		upService.persistUserPreferences(clientDetails.getSessionDetails().getUserId(), userPrefs);
		
		return new SimpleResponse("UserPreferences persisted successfully.");
	}
}
