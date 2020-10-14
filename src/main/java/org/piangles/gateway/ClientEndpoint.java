package org.piangles.gateway;

import java.io.IOException;

public interface ClientEndpoint
{
	void sendMessage(Message message) throws IOException;
}
