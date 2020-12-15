package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.auth.AuthenticationService;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.ChangePasswordRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public class ChangePasswordRequestProcessor extends AbstractRequestProcessor<ChangePasswordRequest, SimpleResponse>
{
	private AuthenticationService authService = Locator.getInstance().getAuthenticationService();
	
	public ChangePasswordRequestProcessor()
	{
		super(Endpoints.ChangePassword.name(), ChangePasswordRequest.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, ChangePasswordRequest chgPassRequest) throws Exception
	{
		SimpleResponse response = null;
		AuthenticationResponse authResponse = authService.changePassword(clientDetails.getSessionDetails().getUserId(), 
																	chgPassRequest.getOldPassword(), chgPassRequest.getNewPassword());
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
