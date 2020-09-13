package com.TBD.app.gateway;

import com.TBD.app.gateway.service.impl.jetty.GatewayServiceImpl;

public class GatewayContainer
{
    public static void main(String[] args)
    {
        try
        {
        	GatewayService service = new GatewayServiceImpl();
        	service.init(8080);
        	service.startProcessingRequests();
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }

}
