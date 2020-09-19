package com.TBD.gateway.handling.requests.processors;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.session.SessionManagementService;
import com.TBD.gateway.dto.EmptyRequest;
import com.TBD.gateway.dto.SimpleResponse;
import com.TBD.gateway.handling.ClientDetails;
import com.TBD.gateway.handling.Endpoints;

public final class LogoutRequestProcessor extends AbstractRequestProcessor<EmptyRequest, SimpleResponse>
{
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	
	public LogoutRequestProcessor()
	{
		super(Endpoints.Logout.name(), EmptyRequest.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, EmptyRequest emptyRequest) throws Exception
	{
		sessionMgmtService.unregister(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());

		return new SimpleResponse(true, "Logged out successfully.");
	}
}
