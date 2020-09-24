package org.piangles.app.gateway.it;

public class LoginLogoutTest extends HeadlessClientHelper
{
	public LoginLogoutTest() throws Exception
	{
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			LoginLogoutTest client = new LoginLogoutTest();
			
			client.login();
			Thread.sleep(1000);
			
			client.createRequestAndSend("Logout");
			Thread.sleep(1000);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.err.println("Exception because of : " + ex.getMessage());
		}
	}
}