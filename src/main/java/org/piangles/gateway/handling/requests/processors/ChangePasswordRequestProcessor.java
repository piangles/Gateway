package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.ChangePasswordRequest;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;

public class ChangePasswordRequestProcessor extends AbstractRequestProcessor<ChangePasswordRequest, SimpleResponse>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public ChangePasswordRequestProcessor()
	{
		super(Endpoints.ChangePassword.name(), ChangePasswordRequest.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, ChangePasswordRequest request) throws Exception
	{
		SimpleResponse response = null;
		AuthenticationResponse authResponse = authService.changePassword(clientDetails.getSessionDetails().getUserId(), 
																	request.getOldPassword(), request.getNewPassword());
		if (authResponse.isRequestSuccessful())
		{
			response = new SimpleResponse(true);
		}
		else
		{
			StringBuffer sb = new StringBuffer();
			authResponse.getFailureMessages().stream().map(msg -> sb.append(msg).append("\n"));
			response = new SimpleResponse(false, sb.toString());
		}
		return response;
	}
}
