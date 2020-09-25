package org.piangles.app.gateway.it;

public class ConfigRequestTest extends HeadlessClientHelper
{
	public ConfigRequestTest() throws Exception
	{
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			ConfigRequestTest client = new ConfigRequestTest();
			
			client.login();
			Thread.sleep(1000);
			
			client.createRequestAndSend("GetConfiguration", "14fe64ea-d15a-4c8b-af2f-f2c7efe1943b");
			Thread.sleep(3000);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.err.println("Exception because of : " + ex.getMessage());
		}
	}
}