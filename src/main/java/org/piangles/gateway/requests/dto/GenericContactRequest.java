package org.piangles.gateway.requests.dto;

public final class GenericContactRequest
{
	private String emailId = null;
	private String phoneNo = null;
	
	public GenericContactRequest(String emailId, String phoneNo)
	{
		this.emailId = emailId;
		this.phoneNo = phoneNo;
	}

	public String getEmailId()
	{
		return emailId;
	}

	public String getPhoneNo()
	{
		return phoneNo;
	}
}
