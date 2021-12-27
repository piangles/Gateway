package org.piangles.gateway;

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
