package org.piangles.gateway.handling.requests.dto;

public final class LoginRequest
{
	private String authenticationType; //Could be one of [Default,TokenBased,Google]
	private String id; //Could be one of [emailId or phoneNumber, tokenId(Proprietary or from SSO Providers), userId]
	private String password; 
	private String sessionId; //When client app reconnects on disconnect this can be used in combo with userId
	
	public LoginRequest(String authenticationType, String id, String password, String sessionId)
	{
		this.authenticationType = authenticationType;
		this.id = id;
		this.password = password;
		this.sessionId = sessionId;
	}

	public String getAuthenticationType()
	{
		return authenticationType;
	}
	
	public String getId()
	{
		return id;
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
		return "LoginRequest [authenticationType=" + authenticationType + ", id=" + id + ", sessionId=" + sessionId + "]";
	}
}
