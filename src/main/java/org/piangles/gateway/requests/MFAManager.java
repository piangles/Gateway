package org.piangles.gateway.requests;

import org.piangles.gateway.client.ClientDetails;

public interface MFAManager
{
	public void sendMFAToken(ClientDetails clientDetails);
	
	public void validateMFAToken(ClientDetails clientDetails, String token);
}
