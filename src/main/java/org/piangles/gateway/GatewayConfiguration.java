package org.piangles.gateway;

/**
 * Set the configuration here to overcome Errors like below.
 * 
 * https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent/code
 * 
 * 1000	Normal Closure		The connection successfully completed the purpose for which it was created.
 * 1006	Abnormal Closure	Indicates that a connection was closed abnormally (that is, with no close frame being sent) when a status code is expected.
 * 1009	Message too big		The endpoint is terminating the connection because a data frame was received that is too large.
 * 
 */

public final class GatewayConfiguration
{
	public static String DEFAULT_HOST = "0.0.0.0";
	public static int DEFAULT_PORT = 80;
	public static long DEFAULT_IDLE_TIMEOUT = 1 * 60 * 1000; //1 Minute in MilliSeconds
	public static int DEFAULT_MAX_TEXT_MESSAGE_SIZE = 320 * 1024;//320 Kilo Bytes
	
	private String host = null;
	private int port = -1;
	private long idleTimeout = -1;
	private int maxTextMessageSize = -1;
	
	public GatewayConfiguration()
	{
		this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_IDLE_TIMEOUT, DEFAULT_MAX_TEXT_MESSAGE_SIZE);
	}
	
	public GatewayConfiguration(String host, int port, long idleTimeout, int maxTextMessageSize)
	{
		this.host = host;
		this.port = port;
		this.idleTimeout = idleTimeout;
		this.maxTextMessageSize = maxTextMessageSize;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	public long getIdleTimeout()
	{
		return idleTimeout;
	}

	public int getMaxTextMessageSize()
	{
		return maxTextMessageSize;
	}
}
