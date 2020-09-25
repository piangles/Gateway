package org.piangles.gateway;

import java.io.IOException;

public interface ClientEndpoint
{
	void sendString(String text) throws IOException;
}
