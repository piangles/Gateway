package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.GenerateTokenRequest;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;

public final class GenerateTokenRequestProcessor extends AbstractRequestProcessor<GenerateTokenRequest, SimpleResponse>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public GenerateTokenRequestProcessor()
	{
		super(Endpoints.GenerateResetToken.name(), false, GenerateTokenRequest.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, GenerateTokenRequest tokenRequest) throws Exception
	{
		boolean result = true;
		String message = "Please check your registered email for the token.";
		
		result = authService.generateResetToken(tokenRequest.getEmailId());
		if (!result)
		{
			message = "Could not generate a reset token.";
		}
		
		return new SimpleResponse(result, message);
	}
	
	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
