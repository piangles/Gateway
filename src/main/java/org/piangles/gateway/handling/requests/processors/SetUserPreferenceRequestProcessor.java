package org.piangles.gateway.handling.requests.processors;

import java.util.Properties;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;

public class SetUserPreferenceRequestProcessor extends AbstractRequestProcessor<Properties, SimpleResponse>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public SetUserPreferenceRequestProcessor()
	{
		super(Endpoints.SetUserPreferences.name(), Properties.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, Properties props) throws Exception
	{
		UserPreference prefs = new UserPreference(props);
		
		upService.persistUserPreference(clientDetails.getSessionDetails().getUserId(), prefs);
		
		return new SimpleResponse(true, "UserPreferences persisted successfully.");
	}
}
