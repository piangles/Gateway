package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.EmptyRequest;
import org.piangles.gateway.handling.requests.dto.Request;

public class GetUserProfileRequestProcessor extends AbstractRequestProcessor<EmptyRequest, BasicUserProfile>
{
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public GetUserProfileRequestProcessor()
	{
		super(Endpoints.GetUserProfile.name(), EmptyRequest.class);
	}
	
	@Override
	protected BasicUserProfile processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		BasicUserProfile profile = null;
		profile = profileService.getProfile(clientDetails.getSessionDetails().getUserId());

		return profile; 
	}
}
