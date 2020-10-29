package org.piangles.gateway.handling.requests.processors;

import java.util.Properties;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.prefs.UserPreference;
import org.piangles.backbone.services.prefs.UserPreferenceService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.EmptyRequest;
import org.piangles.gateway.handling.requests.dto.Request;

public class GetUserPreferenceRequestProcessor extends AbstractRequestProcessor<EmptyRequest, Properties>
{
	private UserPreferenceService upService = Locator.getInstance().getUserPreferenceService();
	
	public GetUserPreferenceRequestProcessor()
	{
		super(Endpoints.GetUserPreferences.name(), EmptyRequest.class);
	}
	
	@Override
	protected Properties processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
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
