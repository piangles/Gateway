package com.TBD.gateway.handling.requests.processors;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.auth.AuthenticationResponse;
import com.TBD.backbone.services.auth.AuthenticationService;
import com.TBD.backbone.services.session.SessionDetails;
import com.TBD.backbone.services.session.SessionManagementService;
import com.TBD.gateway.dto.LoginRequest;
import com.TBD.gateway.dto.LoginResponse;
import com.TBD.gateway.handling.ClientDetails;
import com.TBD.gateway.handling.Endpoints;

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
	public LoginResponse processRequest(ClientDetails clientDetails, LoginRequest loginRequest) throws Exception
	{
		LoginResponse loginResponse = null;
		if (loginRequest.getLoginId() == null || (loginRequest.getPassword() == null && loginRequest.getSessionId() == null))
		{
			throw new Exception("Invalid LoginRequest request, mandatory fields are absent.");
		}

		if (loginRequest.getPassword() != null) //Authenticate using login and password
		{
			AuthenticationResponse authResponse = authService.authenticate(loginRequest.getLoginId(), loginRequest.getPassword());
			
			if (authResponse.isAuthenticated())
			{
				SessionDetails sessionDetails = sessionMgmtService.register(authResponse.getUserId());
				loginResponse = new LoginResponse(authResponse.getUserId(), sessionDetails.getSessionId());
			}
		}
		else //Authenticate using login and sessionId
		{
			boolean isSessionValid = sessionMgmtService.isValid(loginRequest.getLoginId(), loginRequest.getSessionId());
			if (isSessionValid)
			{
				loginResponse = new LoginResponse(loginRequest.getLoginId(), loginRequest.getSessionId()); 
			}
		}

		if (loginResponse == null)
		{
			loginResponse = new LoginResponse(); 
		}
		
		return loginResponse;
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
