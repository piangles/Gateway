package org.piangles.app.gateway.it;

public class GetUserPrefsRequestTest extends HeadlessClientHelper
{
	public GetUserPrefsRequestTest() throws Exception
	{
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			GetUserPrefsRequestTest client = new GetUserPrefsRequestTest();
			
			client.login();
			Thread.sleep(1000);
			
			client.createRequestAndSend("GetUserPreferences");
			Thread.sleep(5000);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.err.println("Exception because of : " + ex.getMessage());
		}
	}
}