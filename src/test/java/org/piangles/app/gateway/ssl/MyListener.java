/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.app.gateway.ssl;

import java.io.IOException;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;

public class MyListener implements WebSocketListener
{
	private static final Logger LOG = Log.getLogger(MyListener.class);

	private String mName;
	private Session mSession;

	private void disconnect()
	{
		if (mSession != null && mSession.isOpen())
		{
			mSession.close();
		}

		mSession = null;
	}

	@Override
	public void onWebSocketConnect(Session session)
	{
		LOG.info("onWebSocketConnect: {}", session.getRemoteAddress());

		try
		{
			session.getRemote().sendString("Hello " + session.getRemoteAddress());
		}
		catch (IOException ex)
		{
			LOG.warn(ex);
			return;
		}

		mSession = session;
	}

	@Override
	public void onWebSocketClose(int statusCode, String reason)
	{
		LOG.info("onWebSocketClose: {} - {}", statusCode, reason);
		disconnect();
	}

	@Override
	public void onWebSocketError(Throwable cause)
	{
		LOG.warn("onWebSocketError", cause);
		disconnect();
	}

	@Override
	public void onWebSocketText(String str)
	{
		LOG.info("onWebSocketText: {}", str);
		disconnect();
	}

	@Override
	public void onWebSocketBinary(byte[] arg0, int arg1, int arg2)
	{
		LOG.info("onWebSocketBinary");
		disconnect();
	}
}
