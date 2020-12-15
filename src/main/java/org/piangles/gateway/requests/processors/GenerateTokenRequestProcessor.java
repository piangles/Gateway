package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.GenerateTokenRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public final class GenerateTokenRequestProcessor extends AbstractRequestProcessor<GenerateTokenRequest, SimpleResponse>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public GenerateTokenRequestProcessor()
	{
		super(Endpoints.GenerateResetToken.name(), false, GenerateTokenRequest.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, GenerateTokenRequest tokenRequest) throws Exception
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
