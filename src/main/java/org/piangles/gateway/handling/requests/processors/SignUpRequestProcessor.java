package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.backbone.services.auth.Credential;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.SignUpRequest;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;

public final class SignUpRequestProcessor extends AbstractRequestProcessor<SignUpRequest, SimpleResponse>
{
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public SignUpRequestProcessor()
	{
		super(Endpoints.SignUp.name(), false, SignUpRequest.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, SignUpRequest request) throws Exception
	{
		SimpleResponse response = null;
		AuthenticationResponse authResponse = null;
		
		authResponse = authService.validatePasswordStrength(request.getPassword());
		if (authResponse.isRequestSuccessful())
		{
			String userId = profileService.createProfile(new BasicUserProfile(request.getFirstName(), request.getLastName(), request.getEmailId()));
			
			authResponse = authService.createAuthenticationEntry(userId, new Credential(request.getEmailId(), request.getPassword()));
		
			response = new SimpleResponse(authResponse.isRequestSuccessful());
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			authResponse.getFailureMessages().stream().map(msg -> sb.append(msg).append("\n"));
			response = new SimpleResponse(false, sb.toString());
		}
		return response;
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
