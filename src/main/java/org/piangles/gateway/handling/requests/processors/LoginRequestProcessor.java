package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.backbone.services.auth.AuthenticationType;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.auth.FailureReason;
import org.piangles.backbone.services.session.SessionDetails;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.LoginRequest;
import org.piangles.gateway.handling.requests.dto.LoginResponse;
import org.piangles.gateway.handling.requests.dto.Request;

public final class LoginRequestProcessor extends AbstractRequestProcessor<LoginRequest, LoginResponse>
{
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public LoginRequestProcessor()
	{
		super(Endpoints.Login.name(), false, LoginRequest.class);
	}
	
	/**
	 * The user can login in 2 ways.
	 * 
	 * 1. Traditional Approach : LoginId and Password. Also create a new Session and the SessionManagementService should invalidate any old sessions.
	 * 	However the Authentication Service does a few more features to the mix.
	 * 
	 * 2. Enhanced Approach : LoginId and SessionId. In this approach the SessionId is validated with SessionManagementService. However we do need to 
	 * return AuthResponse and that functionality should not be duplicated here, call needs to be made to Authentication Service. Or we could as well just
	 * pass the Request to AuthenticationService and let it deal with the steps there.
	 * 
	 * AuthenticationResponse
	 * PasswordMismatch,
	 * AccountDisabled,
	 * TooManyTries;
	 * 
	 * Also we need to take care of ChangePassword on first login after user is setup. Not sure where that code is to be.
	 * 
	 */
	@Override
	protected LoginResponse processRequest(ClientDetails clientDetails, Request request, LoginRequest loginRequest) throws Exception
	{
		LoginResponse loginResponse = null;
		if (loginRequest.getId() == null || (loginRequest.getPassword() == null && loginRequest.getSessionId() == null))
		{
			throw new Exception("Invalid LoginRequest request, mandatory fields are absent.");
		}

		if (loginRequest.getPassword() != null) //Authenticate using login and password
		{
			AuthenticationType type = AuthenticationType.valueOf(loginRequest.getAuthenticationType());
			AuthenticationResponse authResponse = authService.authenticate(type, new Credential(loginRequest.getId(), loginRequest.getPassword()));
			
			if (authResponse.isAuthenticated())
			{
				SessionDetails sessionDetails = sessionMgmtService.register(authResponse.getUserId());
				loginResponse = new LoginResponse(authResponse.getUserId(), sessionDetails.getSessionId(), authResponse.IsValidatedByToken());
			}
			else
			{
				loginResponse = new LoginResponse(authResponse.getNoOfAttemptsRemaining(), authResponse.getFailureReason().name());
			}
		}
		else //Authenticate using login and sessionId
		{
			boolean isSessionValid = sessionMgmtService.isValid(loginRequest.getId(), loginRequest.getSessionId());
			if (isSessionValid)
			{
				loginResponse = new LoginResponse(loginRequest.getId(), loginRequest.getSessionId(), false); 
			}
			else
			{
				loginResponse = new LoginResponse(0, FailureReason.InvalidSession.name());
			}
		}

		return loginResponse;
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
