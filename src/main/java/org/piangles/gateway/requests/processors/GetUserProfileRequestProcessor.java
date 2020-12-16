package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.EmptyRequest;
import org.piangles.gateway.requests.dto.Request;

public class GetUserProfileRequestProcessor extends AbstractRequestProcessor<EmptyRequest, BasicUserProfile>
{
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public GetUserProfileRequestProcessor()
	{
		super(Endpoints.GetUserProfile, EmptyRequest.class, BasicUserProfile.class);
	}
	
	@Override
	protected BasicUserProfile processRequest(ClientDetails clientDetails, Request request, EmptyRequest emptyRequest) throws Exception
	{
		BasicUserProfile profile = null;
		profile = profileService.getProfile(clientDetails.getSessionDetails().getUserId());

		return profile; 
	}
}
