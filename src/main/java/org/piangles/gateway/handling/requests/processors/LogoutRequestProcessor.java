package org.piangles.gateway.handling.requests.processors;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.EmptyRequest;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;
import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.session.SessionManagementService;

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
