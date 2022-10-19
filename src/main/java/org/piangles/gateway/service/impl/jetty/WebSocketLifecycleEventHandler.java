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
 
 
 
package org.piangles.gateway.service.impl.jetty;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.piangles.core.util.Logger;
import org.piangles.gateway.GatewayConfiguration;
import org.piangles.gateway.requests.RequestProcessingManager;

@WebSocket
public final class WebSocketLifecycleEventHandler
{
	private static final String IP_DELIMITER = ",";
	private static final String PORT_DELIMITER = ",";
	private static final String DEFAULT_CLIENT_PORT = "5555";
	
	private static final String FORWARDED_HEADER = "Forwarded";
	private static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
	
	private GatewayConfiguration gatewayConfiguration = null;
	private InetSocketAddress remoteAddr = null;
	
	private RequestProcessingManager rpm = null;
	
	public WebSocketLifecycleEventHandler(GatewayConfiguration gatewayConfiguration, Map<String, List<String>> headers)
	{
		this.gatewayConfiguration = gatewayConfiguration;
		this.remoteAddr = determineRemoteAddress(headers); 
	}
	
	@OnWebSocketClose
	public void onClose(int statusCode, String reason)
	{
		rpm.onClose(statusCode, reason);
	}

	@OnWebSocketError
	public void onError(Throwable t)
	{
		rpm.onError(t);
	}

	@OnWebSocketConnect
	public void onConnect(Session session)
	{
		try
		{
			session.setIdleTimeout(gatewayConfiguration.getIdleTimeout());
			
			/**
			 * https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent/code
			 * 
			 * 1000	Normal Closure		The connection successfully completed the purpose for which it was created.
			 * 1006	Abnormal Closure	Indicates that a connection was closed abnormally (that is, with no close frame being sent) when a status code is expected.
			 * 1009	Message too big		The endpoint is terminating the connection because a data frame was received that is too large.
			 * 
			 */
			session.getPolicy().setMaxTextMessageSize(gatewayConfiguration.getMaxTextMessageSize());
			
			if (remoteAddr == null)
			{
				Logger.getInstance().warn("Remote Address was unable to be determined from Headers, using Session.");
				remoteAddr = session.getRemoteAddress();
			}
			
			rpm = new RequestProcessingManager(remoteAddr, new ClientEndpointImpl(session), gatewayConfiguration);
		}
		catch(Throwable t)
		{
			Logger.getInstance().error("Exception while creating RequestProcessingManager: " + t.getMessage(), t);
			t.printStackTrace();
			t.printStackTrace(System.out);
		}
	}

	@OnWebSocketMessage
	public void onMessage(String message)
	{
		rpm.onMessage(message);
	}
	
	private InetSocketAddress determineRemoteAddress(Map<String, List<String>> headers)
	{
		InetSocketAddress remoteAddress = null;
		
		try
		{
			/**
			 * As per documentation 
			 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-For
			 * 
			 * We are not currently handling TODO Implement extractRemoteAddressFromForwarded
			 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded
			 * 
			 * AWS Documentation
			 * https://docs.aws.amazon.com/elasticloadbalancing/latest/application/x-forwarded-headers.html
			 * 
			 */
			remoteAddress = extractRemoteAddressFromXForwardedFor(headers.get(X_FORWARDED_FOR_HEADER));
			if (remoteAddress == null)
			{
				remoteAddress = extractRemoteAddressFromForwarded(headers.get(FORWARDED_HEADER));
			}
		}
		catch(Exception e)
		{
			/**
			 * Precautionary Exception Catch.
			 * ------------------------------
			 * Not able to determine the address here is not an failure, we can
			 * always recover in onConnect where we determine the RemoteAddress
			 * from Session.
			 */
			Logger.getInstance().warn("Unable to determineRemoteAddress. Reason: " + e.getMessage(), e);
		}
		return remoteAddress;
	}
	
	private InetSocketAddress extractRemoteAddressFromXForwardedFor(List<String> xForwardedForHeaderValues)
	{
		InetSocketAddress remoteAddress = null;
		
		if (xForwardedForHeaderValues != null && !xForwardedForHeaderValues.isEmpty())
		{
			//We pick the first element only
			String valueString = xForwardedForHeaderValues.get(0);
			
			if (StringUtils.isNotBlank(valueString))
			{
				if (valueString.indexOf(IP_DELIMITER) != -1)
				{
					valueString = valueString.substring(0, valueString.indexOf(IP_DELIMITER));	
				}
				
				valueString = valueString.trim();
				
				String host = null;
				String port = null;
				
				if (valueString.indexOf(PORT_DELIMITER) != -1)
				{
					host = valueString.substring(0, valueString.indexOf(PORT_DELIMITER));
					port = valueString.substring(valueString.indexOf(PORT_DELIMITER)+1);
				}
				else
				{
					host = valueString;
					port = DEFAULT_CLIENT_PORT;
				}

				if (host != null)
				{
					remoteAddress = new InetSocketAddress(host, Integer.parseInt(port));
				}
			}
		}

		return remoteAddress;
	}
	
	private InetSocketAddress extractRemoteAddressFromForwarded(List<String> forwardedHeaderValues)
	{
		InetSocketAddress remoteAddress = null;
		
		if (forwardedHeaderValues != null && !forwardedHeaderValues.isEmpty())
		{
			String valueString = forwardedHeaderValues.get(0);
			if (StringUtils.isNotBlank(valueString))
			{
				
			}
		}
		
		return remoteAddress;
	}
}
