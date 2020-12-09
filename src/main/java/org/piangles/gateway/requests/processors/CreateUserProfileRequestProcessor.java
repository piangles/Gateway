package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public class CreateUserProfileRequestProcessor extends AbstractRequestProcessor<BasicUserProfile, SimpleResponse>
{
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public CreateUserProfileRequestProcessor()
	{
		super(Endpoints.CreateUserProfile.name(), BasicUserProfile.class);
	}

	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, BasicUserProfile userProfile) throws Exception
	{
		String userId = profileService.createProfile(userProfile);
		return new SimpleResponse(true, userId);
	}
}
