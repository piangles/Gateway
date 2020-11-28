package org.piangles.gateway.handling.requests;

import java.util.HashMap;
import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.processors.ChangePasswordRequestProcessor;
import org.piangles.gateway.handling.requests.processors.CreateUserProfileRequestProcessor;
import org.piangles.gateway.handling.requests.processors.GenerateTokenRequestProcessor;
import org.piangles.gateway.handling.requests.processors.GetConfigRequestProcessor;
import org.piangles.gateway.handling.requests.processors.GetUserPreferenceRequestProcessor;
import org.piangles.gateway.handling.requests.processors.GetUserProfileRequestProcessor;
import org.piangles.gateway.handling.requests.processors.KeepSessionAliveRequestProcessor;
import org.piangles.gateway.handling.requests.processors.LoginRequestProcessor;
import org.piangles.gateway.handling.requests.processors.LogoutRequestProcessor;
import org.piangles.gateway.handling.requests.processors.PingMessageProcessor;
import org.piangles.gateway.handling.requests.processors.SetUserPreferenceRequestProcessor;
import org.piangles.gateway.handling.requests.processors.SignUpRequestProcessor;
import org.piangles.gateway.handling.requests.processors.SubscribeRequestProcessor;
import org.piangles.gateway.handling.requests.processors.UpdateUserProfileRequestProcessor;

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
		preAuthenticationEndpoints.put(Endpoints.SignUp.name(), Endpoints.SignUp);
		preAuthenticationEndpoints.put(Endpoints.Login.name(), Endpoints.Login);
		preAuthenticationEndpoints.put(Endpoints.GenerateResetToken.name(), Endpoints.GenerateResetToken);

		/**
		 * Register all standard endpoints and request processors
		 */
		endpointRequestProcessorMap = new HashMap<String, RequestProcessor>();

		registerRequestProcessor(new SignUpRequestProcessor());
		registerRequestProcessor(new LoginRequestProcessor());
		registerRequestProcessor(new GenerateTokenRequestProcessor());
		
		registerRequestProcessor(new ChangePasswordRequestProcessor());
		registerRequestProcessor(new LogoutRequestProcessor());
		
		registerRequestProcessor(new PingMessageProcessor());
		registerRequestProcessor(new KeepSessionAliveRequestProcessor());

		registerRequestProcessor(new CreateUserProfileRequestProcessor());
		registerRequestProcessor(new UpdateUserProfileRequestProcessor());
		registerRequestProcessor(new GetUserProfileRequestProcessor());
		
		
		registerRequestProcessor(new GetConfigRequestProcessor());

		registerRequestProcessor(new GetUserPreferenceRequestProcessor());
		registerRequestProcessor(new SetUserPreferenceRequestProcessor());
		
		registerRequestProcessor(new SubscribeRequestProcessor());
	}

	public static RequestRouter getInstance()
	{
		if (self == null)
		{
			synchronized (RequestProcessor.class)
			{
				if (self == null)
				{
					self = new RequestRouter();
				}
			}
		}

		return self;
	}
	
	public Map<String, Endpoints> getPreAuthenticationEndpoints()
	{
		return preAuthenticationEndpoints;
	}

	public RequestProcessor getRequestProcessor(String endpoint)
	{
		return endpointRequestProcessorMap.get(endpoint);
	}

	public void registerRequestProcessor(RequestProcessor rp)
	{
		RequestProcessor existingRP = endpointRequestProcessorMap.get(rp.getEndpoint()); 
		if (existingRP != null)
		{
			logger.warn("Request Router already has a registered endpoint : " + rp.getEndpoint() + " : " + existingRP.getClass().getCanonicalName());
			logger.warn("Overriding " + rp.getEndpoint() + " with : " + rp.getClass().getCanonicalName());
		}
		endpointRequestProcessorMap.put(rp.getEndpoint(), rp);
	}
}
