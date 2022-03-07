package org.piangles.gateway.requests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidateMFATokenRequest
{
	@JsonProperty(required = true)
	private String mfaToken = null;

	public ValidateMFATokenRequest(String mfaToken)
	{
		this.mfaToken = mfaToken;
	}
	
	public String getMFAToken()
	{
		return mfaToken;
	}
}
