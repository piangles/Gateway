package org.piangles.gateway.requests;

import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.gateway.client.ClientDetails;

public interface Communicator
{
	public void sendGeneratePasswordResetTokenCommunication(String emailId, AuthenticationResponse authResponse);

	public void sendPasswordChangeAttemptCommunication(ClientDetails clientDetails, AuthenticationResponse authResponse);
	
	public void sendMFASetupCommunication(ClientDetails clientDetails, BasicUserProfile basicUserProfile);
}
