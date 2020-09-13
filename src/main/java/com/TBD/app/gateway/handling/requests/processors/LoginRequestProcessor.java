package com.TBD.app.gateway.handling.requests.processors;

import com.TBD.app.gateway.dto.LoginRequest;
import com.TBD.app.gateway.dto.LoginResponse;
import com.TBD.app.gateway.handling.ClientDetails;
import com.TBD.appcore.locator.BackboneServiceLocator;
import com.TBD.backbone.services.auth.AuthenticationResponse;
import com.TBD.backbone.services.auth.AuthenticationService;
import com.TBD.backbone.services.session.SessionDetails;
import com.TBD.backbone.services.session.SessionManagementService;

public final class LoginRequestProcessor extends AbstractRequestProcessor<LoginRequest, LoginResponse>
{
	private SessionManagementService sessionMgmtService = BackboneServiceLocator.getInstance().getSessionManagementService();
	private AuthenticationService authService = BackboneServiceLocator.getInstance().getAuthenticationService();
	
	public LoginRequestProcessor()
	{
		super("Login", false, LoginRequest.class);
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

		// Check if the authentication is being asked to be done by
		// sessionId or by UserId / Password
		// If the user lost connection and is reconnecting it will be
		// done by sessionId
		AuthenticationResponse authResponse = authService.authenticate(loginRequest.getLoginId(), loginRequest.getPassword());
		
		if (authResponse.isAuthenticated())
		{
			SessionDetails sessionDetails = sessionMgmtService.register(authResponse.getUserId());
			loginResponse = new LoginResponse(authResponse.getUserId(), sessionDetails.getSessionId());
		}
		else
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
