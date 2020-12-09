package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.session.SessionManagementService;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public final class LogoutRequestProcessor extends AbstractRequestProcessor<EmptyRequest, SimpleResponse>
{
	private SessionManagementService sessionMgmtService = Locator.getInstance().getSessionManagementService();
	
	public LogoutRequestProcessor()
	{
		super(Endpoints.Logout.name(), EmptyRequest.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		sessionMgmtService.unregister(clientDetails.getSessionDetails().getUserId(), clientDetails.getSessionDetails().getSessionId());

		return new SimpleResponse(true, "Logged out successfully.");
	}
}
