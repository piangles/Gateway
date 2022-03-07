package org.piangles.gateway.requests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MFASetupRequest
{
	@JsonProperty(required = true)
	private String phoneNumber;

	@JsonProperty(required = true)
	private String token;

	@JsonProperty(required = true)
	private boolean enabled;

	public MFASetupRequest(String phoneNumber, String token, boolean enabled)
	{
		this.phoneNumber = phoneNumber;
		this.token = token;
		this.enabled = enabled;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}
	
	public String getToken()
	{
		return token;
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}
