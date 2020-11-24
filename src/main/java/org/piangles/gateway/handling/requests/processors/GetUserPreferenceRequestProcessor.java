package org.piangles.gateway.handling.requests.processors;

import java.util.Map;
import java.util.Properties;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.EmptyRequest;
import org.piangles.gateway.handling.requests.dto.Request;

public class GetUserPreferenceRequestProcessor extends AbstractRequestProcessor<EmptyRequest, Map<String, Object>>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public GetUserPreferenceRequestProcessor()
	{
		super(Endpoints.GetUserPreferences.name(), EmptyRequest.class);
	}
	
	@Override
	protected Map<String, Object> processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		Map<String, Object> nvPair = null;
		UserPreference prefs = upService.retrieveUserPreference(clientDetails.getSessionDetails().getUserId());
		if (prefs != null)
		{
			nvPair = prefs.getNVPair();
		}
		return nvPair; 
	}
}
