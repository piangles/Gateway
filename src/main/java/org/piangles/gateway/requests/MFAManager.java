package org.piangles.gateway.requests;

public interface MFAManager
{
	public void sendMFAToken();
	
	public void validateMFAToken();
}
