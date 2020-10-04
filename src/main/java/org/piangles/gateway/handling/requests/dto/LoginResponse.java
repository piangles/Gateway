package org.piangles.gateway.handling.requests.dto;

public final class LoginResponse
{
	private boolean authenticated = false;
	private String userId;
	private String sessionId;

	public LoginResponse()
	{
		authenticated = false;
	}
	
	public LoginResponse(String userId, String sessionId)
	{
		this.authenticated = true;
		this.userId = userId;
		this.sessionId = sessionId;
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

	@Override
	public String toString()
	{
		return "LoginResponse [authenticated=" + authenticated + ", userId=" + userId + ", sessionId=" + sessionId + "]";
	}
}
