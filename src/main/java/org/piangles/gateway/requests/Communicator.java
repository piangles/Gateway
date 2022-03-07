package org.piangles.gateway.requests;

import org.piangles.backbone.services.auth.AuthenticationResponse;
import org.piangles.gateway.client.ClientDetails;

public interface Communicator
{
	public void sendGenerateResetTokenCommunication(String emailId, AuthenticationResponse authResponse);

	public void sendPasswordChangeAttemptCommunication(ClientDetails clientDetails, AuthenticationResponse authResponse);
	
	public void sendMFATokenCommunication(ClientDetails clientDetails, AuthenticationResponse authResponse);
}
