package com.TBD.gateway.handling.requests.processors;

import java.util.Properties;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.prefs.UserPreference;
import com.TBD.backbone.services.prefs.UserPreferenceService;
import com.TBD.gateway.dto.SimpleResponse;
import com.TBD.gateway.handling.ClientDetails;

public class SetUserPreferenceRequestProcessor extends AbstractRequestProcessor<Properties, SimpleResponse>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public SetUserPreferenceRequestProcessor()
	{
		super("SetUserPreferences", Properties.class);
	}
	
	@Override
	public SimpleResponse processRequest(ClientDetails clientDetails, Properties props) throws Exception
	{
		UserPreference prefs = new UserPreference();
		prefs.setProperties(props);
		
		upService.persistUserPreference(clientDetails.getSessionDetails().getUserId(), prefs);
		
		return new SimpleResponse(true, "UserPreferences persisted successfully.");
	}
}
