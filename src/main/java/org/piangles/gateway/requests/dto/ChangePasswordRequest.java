package org.piangles.gateway.requests.dto;

public class ChangePasswordRequest
{
	private String oldPassword = null;
	private String newPassword = null;
	
	public ChangePasswordRequest(String newPassword)
	{
		this(null, newPassword);
	}
	
	public ChangePasswordRequest(String oldPassword, String newPassword)
	{
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}

	public String getOldPassword()
	{
		return oldPassword;
	}

	public String getNewPassword()
	{
		return newPassword;
	}
}
