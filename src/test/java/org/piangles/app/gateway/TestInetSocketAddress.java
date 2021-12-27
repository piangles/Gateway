package org.piangles.app.gateway;

import java.net.InetSocketAddress;

public class TestInetSocketAddress
{

	public static void main(String[] args)
	{
		InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.0.105", 5555);
		System.out.println(inetSocketAddress);
		
		String valueString = "203.0.113.195, 70.41.3.18, 150.172.238.178";
		
		//valueString = "";
		
		if (valueString.indexOf(",") != -1)
		{
			valueString = valueString.substring(0, valueString.indexOf(","));	
		}
		
		valueString = valueString.trim();
		
		String host = null;
		String port = null;
		
		if (valueString.indexOf(":") != -1)
		{
			host = valueString.substring(0, valueString.indexOf(":"));
			port = valueString.substring(valueString.indexOf(":")+1);
		}
		else
		{
			host = valueString;
			port = "5555";
		}
		
		System.out.println(host);
		System.out.println(Integer.parseInt(port));
	}
}
