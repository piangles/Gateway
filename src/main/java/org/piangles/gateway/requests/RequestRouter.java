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
import org.piangles.core.util.validate.ValidationManager;
import org.piangles.core.util.validate.Validator;
import org.piangles.gateway.requests.dao.GatewayDAO;
import org.piangles.gateway.requests.dao.GatewayDAOImpl;
import org.piangles.gateway.requests.hooks.MidAuthenticationHook;
import org.piangles.gateway.requests.hooks.PostAuthenticationHook;
import org.piangles.gateway.requests.hooks.PostRequestProcessingHook;
import org.piangles.gateway.requests.processors.AutoSuggestRequestProcessor;
import org.piangles.gateway.requests.processors.ChangePasswordRequestProcessor;
import org.piangles.gateway.requests.processors.CreateUserProfileRequestProcessor;
import org.piangles.gateway.requests.processors.EndpointMetadataRequestProcessor;
import org.piangles.gateway.requests.processors.GeneratePasswordResetTokenRequestProcessor;
import org.piangles.gateway.requests.processors.GetConfigRequestProcessor;
import org.piangles.gateway.requests.processors.GetUserPreferencesRequestProcessor;
import org.piangles.gateway.requests.processors.GetUserProfileRequestProcessor;
import org.piangles.gateway.requests.processors.KeepSessionAliveRequestProcessor;
import org.piangles.gateway.requests.processors.ListEndpointsRequestProcessor;
import org.piangles.gateway.requests.processors.LoginRequestProcessor;
import org.piangles.gateway.requests.processors.LogoutRequestProcessor;
import org.piangles.gateway.requests.processors.SignUpRequestProcessor;
import org.piangles.gateway.requests.processors.SubscriptionRequestProcessor;
import org.piangles.gateway.requests.processors.UpdateUserPreferencesRequestProcessor;
import org.piangles.gateway.requests.processors.UpdateUserProfileRequestProcessor;
import org.piangles.gateway.requests.processors.UserProfileExistsRequestProcessor;
import org.piangles.gateway.requests.validators.ChangePasswordRequestValidator;
import org.piangles.gateway.requests.validators.GenericContactRequestValidator;
import org.piangles.gateway.requests.validators.LoginRequestValidator;
import org.piangles.gateway.requests.validators.SignUpRequestValidator;
import org.piangles.gateway.requests.validators.SubscriptionRequestValidator;

public class RequestRouter
{
	private static RequestRouter self = null;

	private LoggingService logger = null;

	private GatewayDAO gatewayDAO = null;
	
	private Map<String, Enum<?>> preAuthenticationEndpoints = null;

	private Map<String, Enum<?>> authenticationEndpoints = null;
	private Map<String, Enum<?>> midAuthenticationEndpoints = null;
	
	private Map<String, RequestProcessor> endpointRequestProcessorMap;
	
	private PostRequestProcessingHook postRequestProcessingHook = null; 
	private MidAuthenticationHook midAuthenticationHook = null;
	private PostAuthenticationHook postAuthenticationHook = null;
	
	private Communicator communicator = null;
	private MFAManager mfaManager = null;

	private RequestRouter()
	{
		logger = Locator.getInstance().getLoggingService();
		
		try
		{
			gatewayDAO = new GatewayDAOImpl();
		}
		catch (Exception e)
		{
			String message = "Unable to create RequestRouter becaue of DAO Failure. Reason: " + e.getMessage(); 
			logger.fatal(message, e);
			throw new RuntimeException(message, e);
		} 
		
		preAuthenticationEndpoints = new HashMap<>();
		
		authenticationEndpoints = new HashMap<>();
		midAuthenticationEndpoints = new HashMap<>();
		
		endpointRequestProcessorMap = new HashMap<>();
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
	
	public GatewayDAO getGatewayDAO()
	{
		return gatewayDAO;
	}

	/**
	 * Register all preAuthenticationEndpoints
	 */
	public void registerDefaultPreAuthenticationEndpoints()
	{
		registerPreAuthenticationEndpoint(Endpoints.ListEndpoints.name(), Endpoints.ListEndpoints);
		registerPreAuthenticationEndpoint(Endpoints.EndpointMetadata.name(), Endpoints.EndpointMetadata);
		
		registerPreAuthenticationEndpoint(Endpoints.UserProfileExists.name(), Endpoints.UserProfileExists);
		registerPreAuthenticationEndpoint(Endpoints.SignUp.name(), Endpoints.SignUp);
		registerPreAuthenticationEndpoint(Endpoints.Login.name(), Endpoints.Login);
		registerPreAuthenticationEndpoint(Endpoints.GeneratePasswordResetToken.name(), Endpoints.GeneratePasswordResetToken);
	}

	/**
	 * Register all authenticationEndpoints
	 */
	public void registerDefaultAuthenticationEndpoints()
	{
		registerAuthenticationEndpoint(Endpoints.SignUp.name(), Endpoints.SignUp);
		registerAuthenticationEndpoint(Endpoints.Login.name(), Endpoints.Login);
	}

	/**
	 * Register all MidAuthenticationEndpoints
	 */
	public void registerDefaultMidAuthenticationEndpoints()
	{
		registerMidAuthenticationEndpoint(Endpoints.ChangePassword.name(), Endpoints.ChangePassword);
		registerMidAuthenticationEndpoint(Endpoints.ValidateMFAToken.name(), Endpoints.ValidateMFAToken);
	}

	/**
	 * Register all standard endpoints and request processors
	 */
	public void registerDefaultRequestProcessors()
	{
		registerRequestProcessor(createRequestProcessor(ListEndpointsRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(EndpointMetadataRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(UserProfileExistsRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(SignUpRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(LoginRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(GeneratePasswordResetTokenRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(ChangePasswordRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(LogoutRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(KeepSessionAliveRequestProcessor.class));

		registerRequestProcessor(createRequestProcessor(CreateUserProfileRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(UpdateUserProfileRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(GetUserProfileRequestProcessor.class));
		
		
		registerRequestProcessor(createRequestProcessor(GetConfigRequestProcessor.class));

		registerRequestProcessor(createRequestProcessor(GetUserPreferencesRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(UpdateUserPreferencesRequestProcessor.class));
		
		registerRequestProcessor(createRequestProcessor(SubscriptionRequestProcessor.class));
		registerRequestProcessor(createRequestProcessor(AutoSuggestRequestProcessor.class));
	}
	
	public void registerDefaultRequestValidators()
	{
		registerRequestValidator(new GenericContactRequestValidator(Endpoints.UserProfileExists));
		registerRequestValidator(new SignUpRequestValidator());
		registerRequestValidator(new LoginRequestValidator());
		registerRequestValidator(new ChangePasswordRequestValidator());
		registerRequestValidator(new SubscriptionRequestValidator());
	}
	
	public void registerPreAuthenticationEndpoint(String endpointName, Enum<?> endpoint)
	{
		preAuthenticationEndpoints.put(endpointName, endpoint);
	}

	public void registerAuthenticationEndpoint(String endpointName, Enum<?> endpoint)
	{
		authenticationEndpoints.put(endpointName, endpoint);
	}

	public void registerMidAuthenticationEndpoint(String endpointName, Enum<?> endpoint)
	{
		midAuthenticationEndpoints.put(endpointName, endpoint);
	}

	public void registerRequestProcessor(RequestProcessor rp)
	{
		if (rp != null)
		{
			rp.setGatewayDAO(gatewayDAO);
			String registringOrOverriding = null; 
			RequestProcessor existingRP = endpointRequestProcessorMap.get(rp.getEndpoint().name()); 
			if (existingRP != null)
			{
				logger.warn("Request Router already has a registered endpoint : " + rp.getEndpoint() + " : " + existingRP.getClass().getCanonicalName());
				registringOrOverriding = "Overriding"; 
			}
			else
			{
				registringOrOverriding = "Registering";
			}
			logger.info(registringOrOverriding + " " + rp.getEndpoint() + " with : " + rp.getClass().getCanonicalName());
			endpointRequestProcessorMap.put(rp.getEndpoint().name(), rp);
		}
		else
		{
			logger.error("registerRequestProcessor: RequestProcessor is null.");
		}
	}

	public void registerRequestValidator(Validator validator)
	{
		String registringOrOverriding = null; 
		String endpoint = validator.getName();
		Validator existingValidator = ValidationManager.getInstance().getValidator(endpoint); 
		if (existingValidator != null)
		{
			logger.warn("Validator already exists for : " + endpoint + " : " + existingValidator.getClass().getCanonicalName());
			registringOrOverriding = "Overriding"; 
		}
		else
		{
			registringOrOverriding = "Registering";
		}
		logger.info(registringOrOverriding + " " + endpoint + " Validator with : " + validator.getClass().getCanonicalName());
		ValidationManager.getInstance().addValidator(validator);
	}

	public void registerPostRequestProcessingHook(PostRequestProcessingHook postRequestProcessingHook)
	{
		this.postRequestProcessingHook = postRequestProcessingHook;
	}

	public void registerMidAuthenticationHook(MidAuthenticationHook midAuthenticationHook)
	{
		this.midAuthenticationHook = midAuthenticationHook;
	}

	public void registerPostAuthenticationHook(PostAuthenticationHook postAuthenticationHook)
	{
		this.postAuthenticationHook = postAuthenticationHook;
	}
	
	public void registerCommunicator(Communicator communicator)
	{
		this.communicator = communicator;
	}
	
	public void registerMFAManager(MFAManager mfaManager)
	{
		this.mfaManager = mfaManager;
	}
	
	/**
	 * Accessor methods and Helper methods for the above setters / registers
	 */
	
	public boolean isPreAuthenticationEndpoint(String endpoint)
	{
		return preAuthenticationEndpoints.containsKey(endpoint);
	}

	public boolean isAuthenticationEndpoint(String endpoint)
	{
		return authenticationEndpoints.containsKey(endpoint);
	}

	public boolean isMidAuthenticationEndpoint(String endpoint)
	{
		return midAuthenticationEndpoints.containsKey(endpoint);
	}

	public Set<String> getRegisteredEndpoints()
	{
		return endpointRequestProcessorMap.keySet();
	}

	public RequestProcessor getRequestProcessor(String endpoint)
	{
		return endpointRequestProcessorMap.get(endpoint);
	}

	public PostRequestProcessingHook getPostRequestProcessingHook()
	{
		return postRequestProcessingHook;
	}

	public MidAuthenticationHook getMidAuthenticationHook()
	{
		return midAuthenticationHook;
	}

	public PostAuthenticationHook getPostAuthenticationHook()
	{
		return postAuthenticationHook;
	}
	
	public Communicator getCommunicator()
	{
		return communicator;
	}
	
	public MFAManager getMFAManager()
	{
		return mfaManager;
	}
	
	/**
	 * Clearning methods for the internal maps
	 */
	
	public void clearRequestProcessors()
	{
		endpointRequestProcessorMap.clear();
	}

	public void clearRequestValidators()
	{
		ValidationManager.getInstance().clear();
	}

	/**
	 * Private methods for setting up RequestProcessors
	 */
	
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
