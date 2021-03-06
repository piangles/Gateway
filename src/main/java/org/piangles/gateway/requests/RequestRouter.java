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
 
 
 
package org.piangles.gateway.requests;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.gateway.requests.processors.AutoSuggestRequestProcessor;
import org.piangles.gateway.requests.processors.ChangePasswordRequestProcessor;
import org.piangles.gateway.requests.processors.CreateUserProfileRequestProcessor;
import org.piangles.gateway.requests.processors.EndpointMetadataRequestProcessor;
import org.piangles.gateway.requests.processors.GenerateTokenRequestProcessor;
import org.piangles.gateway.requests.processors.GetConfigRequestProcessor;
import org.piangles.gateway.requests.processors.GetUserPreferenceRequestProcessor;
import org.piangles.gateway.requests.processors.GetUserProfileRequestProcessor;
import org.piangles.gateway.requests.processors.KeepSessionAliveRequestProcessor;
import org.piangles.gateway.requests.processors.ListEndpointsRequestProcessor;
import org.piangles.gateway.requests.processors.LoginRequestProcessor;
import org.piangles.gateway.requests.processors.LogoutRequestProcessor;
import org.piangles.gateway.requests.processors.PingMessageProcessor;
import org.piangles.gateway.requests.processors.UpdateUserPreferenceRequestProcessor;
import org.piangles.gateway.requests.processors.SignUpRequestProcessor;
import org.piangles.gateway.requests.processors.SubscribeRequestProcessor;
import org.piangles.gateway.requests.processors.UpdateUserProfileRequestProcessor;

public class RequestRouter
{
	private LoggingService logger = null;
	private static RequestRouter self = null;
	private Map<String, Endpoints> preAuthenticationEndpoints = null;
	private Map<String, RequestProcessor> endpointRequestProcessorMap;

	private RequestRouter()
	{
		logger = Locator.getInstance().getLoggingService();

		/**
		 * Register all preAuthenticationEndpoints
		 */
		preAuthenticationEndpoints = new HashMap<String, Endpoints>();
		preAuthenticationEndpoints.put(Endpoints.ListEndpoints.name(), Endpoints.ListEndpoints);
		preAuthenticationEndpoints.put(Endpoints.EndpointMetadata.name(), Endpoints.EndpointMetadata);
		
		preAuthenticationEndpoints.put(Endpoints.SignUp.name(), Endpoints.SignUp);
		preAuthenticationEndpoints.put(Endpoints.Login.name(), Endpoints.Login);
		preAuthenticationEndpoints.put(Endpoints.GenerateResetToken.name(), Endpoints.GenerateResetToken);

		/**
		 * Register all standard endpoints and request processors
		 */
		endpointRequestProcessorMap = new HashMap<String, RequestProcessor>();

		registerRequestProcessor(createRequestProcessor(ListEndpointsRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(EndpointMetadataRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(SignUpRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(LoginRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(GenerateTokenRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(ChangePasswordRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(LogoutRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(PingMessageProcessor.class));
		registerRequestProcessor(createRequestProcessor(KeepSessionAliveRequestProcessor.class));

		registerRequestProcessor(createRequestProcessor(CreateUserProfileRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(UpdateUserProfileRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(GetUserProfileRequestProcessor.class));
		
		
		registerRequestProcessor(createRequestProcessor(GetConfigRequestProcessor.class));

		registerRequestProcessor(createRequestProcessor(GetUserPreferenceRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(UpdateUserPreferenceRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(SubscribeRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(AutoSuggestRequestProcessor.class));
	}

	public static RequestRouter getInstance()
	{
		if (self == null)
		{
			synchronized (RequestRouter.class)
			{
				if (self == null)
				{
					self = new RequestRouter();
				}
			}
		}

		return self;
	}
	
	public boolean isPreAuthenticationEndpoint(String endpoint)
	{
		return preAuthenticationEndpoints.containsKey(endpoint);
	}
	
	public Set<String> getRegisteredEndpoints()
	{
		return endpointRequestProcessorMap.keySet();
	}

	public RequestProcessor getRequestProcessor(String endpoint)
	{
		return endpointRequestProcessorMap.get(endpoint);
	}
	
	public void clear()
	{
		endpointRequestProcessorMap.clear();
	}

	public void registerRequestProcessor(RequestProcessor rp)
	{
		if (rp != null)
		{
			RequestProcessor existingRP = endpointRequestProcessorMap.get(rp.getEndpoint().name()); 
			if (existingRP != null)
			{
				logger.warn("Request Router already has a registered endpoint : " + rp.getEndpoint() + " : " + existingRP.getClass().getCanonicalName());
				logger.warn("Overriding " + rp.getEndpoint() + " with : " + rp.getClass().getCanonicalName());
			}
			endpointRequestProcessorMap.put(rp.getEndpoint().name(), rp);
		}
	}
	
	private RequestProcessor createRequestProcessor(Class<?> rpClass)
	{
		RequestProcessor rp = null;
		
		try
		{
			rp = (RequestProcessor)rpClass.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			logger.warn("Unable to create " + rpClass.getCanonicalName() + " because of : " + e.getMessage());
		}
		
		return rp;
	}
}
