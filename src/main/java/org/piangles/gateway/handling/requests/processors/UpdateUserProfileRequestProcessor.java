package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.backbone.services.profile.UserProfileService;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.Request;
import org.piangles.gateway.handling.requests.dto.SimpleResponse;

public class UpdateUserProfileRequestProcessor extends AbstractRequestProcessor<BasicUserProfile, SimpleResponse>
{
	private UserProfileService profileService = Locator.getInstance().getUserProfileService();
	
	public UpdateUserProfileRequestProcessor()
	{
		super(Endpoints.UpdateUserProfile.name(), BasicUserProfile.class);
	}

	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, BasicUserProfile userProfile) throws Exception
	{
		profileService.updateProfile(clientDetails.getSessionDetails().getUserId(), userProfile);
		return new SimpleResponse(true);
	}
}