package org.piangles.gateway.requests.dto;

public class GenerateTokenRequest
{
	private String emailId;

	public GenerateTokenRequest(String emailId)
	{
		this.emailId = emailId;
	}

	public String getEmailId()
	{
		return emailId;
	}

	@Override
	public String toString()
	{
		return "GenerateTokenRequest [emailId=" + emailId + "]";
	}
}
