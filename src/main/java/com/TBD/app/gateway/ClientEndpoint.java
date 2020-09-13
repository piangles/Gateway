package com.TBD.app.gateway;

import java.io.IOException;

public interface ClientEndpoint
{
	void sendString(String text) throws IOException;
}
