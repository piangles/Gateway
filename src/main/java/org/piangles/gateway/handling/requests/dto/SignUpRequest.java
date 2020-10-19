package org.piangles.gateway.handling.requests.dto;

public class SignUpRequest
{
	private String firstName = null;
	private String lastName = null;
	private String emailId = null;
	private String password = null;
	
	public SignUpRequest(String firstName, String lastName, String emailId, String password)
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailId = emailId;
		this.password = password;
	}
	
	public String getFirstName()
	{
		return firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public String getEmailId()
	{
		return emailId;
	}
	
	public String getPassword()
	{
		return password;
	}
}
