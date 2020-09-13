package com.TBD.app.gateway.handling.requests.processors;

import java.util.Properties;

import com.TBD.app.gateway.dto.EmptyRequest;
import com.TBD.app.gateway.handling.ClientDetails;
import com.TBD.appcore.locator.BackboneServiceLocator;
import com.TBD.backbone.services.prefs.UserPreference;
import com.TBD.backbone.services.prefs.UserPreferenceService;

public class GetUserPreferenceRequestProcessor extends AbstractRequestProcessor<EmptyRequest, Properties>
{
	private UserPreferenceService upService = BackboneServiceLocator.getInstance().getUserPreferenceService();
	
	public GetUserPreferenceRequestProcessor()
	{
		super("GetUserPreferences", EmptyRequest.class);
	}
	
	@Override
	public Properties processRequest(ClientDetails clientDetails, EmptyRequest emptyRequest) throws Exception
	{
		Properties props = null;
		UserPreference prefs = upService.retrieveUserPreference(clientDetails.getSessionDetails().getUserId());
		if (prefs != null)
		{
			props = prefs.getProperties();
		}
		return props; 
	}
}
