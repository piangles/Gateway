package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

public class GetUserPreferenceRequestProcessor extends AbstractRequestProcessor<EmptyRequest, UserPreference>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public GetUserPreferenceRequestProcessor()
	{
		super(Endpoints.GetUserPreferences.name(), EmptyRequest.class, UserPreference.class);
	}
	
	@Override
	protected UserPreference processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		return upService.retrieveUserPreference(clientDetails.getSessionDetails().getUserId()); 
	}
}
