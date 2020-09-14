package com.TBD.gateway.handling.requests.processors;

import java.util.Properties;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.prefs.UserPreference;
import com.TBD.backbone.services.prefs.UserPreferenceService;
import com.TBD.gateway.dto.EmptyRequest;
import com.TBD.gateway.handling.ClientDetails;

public class GetUserPreferenceRequestProcessor extends AbstractRequestProcessor<EmptyRequest, Properties>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
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
