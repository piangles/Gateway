package com.TBD.app.gateway.handling.requests.processors;

import com.TBD.app.gateway.dto.EmptyRequest;
import com.TBD.app.gateway.dto.SimpleResponse;
import com.TBD.app.gateway.handling.ClientDetails;
import com.TBD.appcore.locator.BackboneServiceLocator;
import com.TBD.backbone.services.session.SessionManagementService;

public final class LogoutRequestProcessor extends AbstractRequestProcessor<EmptyRequest, SimpleResponse>
{
	private SessionManagementService sessionMgmtService = BackboneServiceLocator.getInstance().getSessionManagementService();
	
	public LogoutRequestProcessor()
	{
		super("Logout", EmptyRequest.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, EmptyRequest emptyRequest) throws Exception
	{
		sessionMgmtService.unregister(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());

		return new SimpleResponse(true, "Logged out successfully.");
	}
}
