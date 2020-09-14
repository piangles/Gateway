package com.TBD.gateway.dto;

public final class LoginRequest
{
	private String loginId;
	private String password; //@MaskedValue 
	private String sessionId;
	
	public LoginRequest(String loginId, String password, String sessionId)
	{
		this.loginId = loginId;
		this.password = password;
		this.sessionId = sessionId;
	}

	public String getLoginId()
	{
		return loginId;
	}

	public String getPassword()
	{
		return password;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	@Override
	public String toString()
	{
		return "LoginRequest [loginId=" + loginId + ", sessionId=" + sessionId + "]";
	}
}
