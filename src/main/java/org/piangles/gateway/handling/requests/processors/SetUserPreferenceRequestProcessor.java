package org.piangles.gateway.handling.requests.processors;

import java.util.Properties;

import org.piangles.gateway.dto.SimpleResponse;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.prefs.UserPreference;
import com.TBD.backbone.services.prefs.UserPreferenceService;

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
		UserPreference prefs = new UserPreference();
		prefs.setProperties(props);
		
		upService.persistUserPreference(clientDetails.getSessionDetails().getUserId(), prefs);
		
		return new SimpleResponse(true, "UserPreferences persisted successfully.");
	}
}
