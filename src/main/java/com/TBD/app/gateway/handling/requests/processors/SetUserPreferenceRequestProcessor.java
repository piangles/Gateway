package com.TBD.app.gateway.handling.requests.processors;

import java.util.Properties;

import com.TBD.app.gateway.dto.SimpleResponse;
import com.TBD.app.gateway.handling.ClientDetails;
import com.TBD.appcore.locator.BackboneServiceLocator;
import com.TBD.backbone.services.prefs.UserPreference;
import com.TBD.backbone.services.prefs.UserPreferenceService;

public class SetUserPreferenceRequestProcessor extends AbstractRequestProcessor<Properties, SimpleResponse>
{
	private UserPreferenceService upService = BackboneServiceLocator.getInstance().getUserPreferenceService();
	
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
