package org.piangles.gateway.requests.dto;

public final class LoginResponse
{
	private boolean authenticated = false;
	private boolean authenticatedByToken = false; 
	private String userId;
	private String sessionId;
	private int noOfAttemptsRemaining = 0;
	private String failureReason = null;

	public LoginResponse(int noOfAttemptsRemaining, String failureReason)
	{
		this.noOfAttemptsRemaining = noOfAttemptsRemaining;
		this.failureReason = failureReason;
	}
	
	public LoginResponse(String userId, String sessionId, boolean authenticatedByToken)
	{
		this.authenticated = true;
		this.userId = userId;
		this.sessionId = sessionId;
		this.authenticatedByToken = authenticatedByToken;
	}

	public boolean isAuthenticated()
	{
		return authenticated;
	}

	public String getUserId()
	{
		return userId;
	}

	public String getSessionId()
	{
		return sessionId;
	}
	
	public boolean isAuthenticatedByToken()
	{
		return authenticatedByToken;
	}

	public int getNoOfAttemptsRemaining()
	{
		return noOfAttemptsRemaining;
	}

	public String getFailureReason()
	{
		return failureReason;
	}

	@Override
	public String toString()
	{
		return "LoginResponse [authenticated=" + authenticated + ", authenticatedByToken=" + authenticatedByToken + ", userId=" + userId + ", sessionId=" + sessionId + ", noOfAttemptsRemaining="
				+ noOfAttemptsRemaining + ", failureReason=" + failureReason + "]";
	}
}
