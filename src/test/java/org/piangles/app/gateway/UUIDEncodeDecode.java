package org.piangles.app.gateway;

import java.util.UUID;

import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.handling.requests.dto.Request;

public class UUIDEncodeDecode
{
	public static void main(String[] args)
	{
		try
		{
			Request req = new Request(UUID.randomUUID().toString(), null, "Dummy", null);
			System.out.println("Trace Id : " + req.getTraceId());
			String traceId = req.getTraceId().toString();
			String reqAsString = new String(JSON.getEncoder().encode(req));
			System.out.println("ReqAsString : " + reqAsString);
			Request reqDecoded = JSON.getDecoder().decode(reqAsString.getBytes(), Request.class);
			System.out.println("Decodecd Trace Id : " + reqDecoded.getTraceId());
			System.out.println("Are TraceId equals : " + req.getTraceId().equals(reqDecoded.getTraceId()));
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
