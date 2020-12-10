package org.piangles.app.gateway;

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.requests.dto.SignUpRequest;

public class DumpJson
{
	public static void main(String[] args) throws Exception
	{
		SignUpRequest req = new SignUpRequest("Test", "Name", "test@mail.com", "", "");
		System.out.println(new String(JSON.getEncoder().encode(req)));
		
		req = JSON.getDecoder().decode("{\"emailId\":\"test@mail.com\"}".getBytes(), SignUpRequest.class);
		System.out.println(req.getEmailId());
	}
}
