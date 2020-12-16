package org.piangles.gateway.requests.processors;

import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.core.util.reflect.TypeToken;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

public class GetUserPreferenceRequestProcessor extends AbstractRequestProcessor<EmptyRequest, Map<String,Object>>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public GetUserPreferenceRequestProcessor()
	{
		super(Endpoints.GetUserPreferences, EmptyRequest.class, new TypeToken<Map<String, Object>>() {}.getActualClass());
	}
	
	@Override
	protected Map<String, Object> processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		return upService.retrieveUserPreference(clientDetails.getSessionDetails().getUserId()).getNVPair(); 
	}
}
