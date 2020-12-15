package org.piangles.gateway.requests.processors;

import java.util.Map;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public class SetUserPreferenceRequestProcessor extends AbstractRequestProcessor<Map, SimpleResponse>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public SetUserPreferenceRequestProcessor()
	{
		super(Endpoints.SetUserPreferences.name(), Map.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, Map nvPair) throws Exception
	{
		UserPreference prefs = new UserPreference(clientDetails.getSessionDetails().getUserId(), nvPair);
		
		upService.persistUserPreference(clientDetails.getSessionDetails().getUserId(), prefs);
		
		return new SimpleResponse(true, "UserPreferences persisted successfully.");
	}
}
