package org.piangles.gateway.service.impl.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.piangles.gateway.Constants;
import org.piangles.gateway.GatewayService;

import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.core.services.remoting.SessionDetailsCreator;

public class GatewayServiceImpl implements GatewayService
{
	private Server server = null;

	public GatewayServiceImpl()
	{
		server = new Server(new GatewayThreadPool());
	}

	public void init(int port) throws Exception
	{
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(port);
		server.addConnector(connector);

		// Setup the basic application "context" for this application at "/"
		// This is also known as the handler tree (in jetty speak)
		WebSocketHandler wsHandler = new WebSocketHandler()
		{
			@Override
			public void configure(WebSocketServletFactory factory)
			{
				factory.register(WebSocketLifecycleEventHandler.class);
			}
		};

		server.setHandler(wsHandler);
		server.start();
		server.dump(System.err);
	}

	public void startProcessingRequests() throws Exception
	{
		server.join();
	}

	class GatewayThreadPool extends QueuedThreadPool
	{
		@Override
		protected Thread newThread(Runnable runnable)
		{
			return new ClientHandlerThread(runnable);
		}
	}

	class ClientHandlerThread extends Thread implements SessionAwareable
	{
		private Runnable runnable = null;
		private SessionDetails sessionDetails = null;

		public ClientHandlerThread(Runnable runnable)
		{
			this.runnable = runnable;
			try
			{
				sessionDetails = SessionDetailsCreator.createSessionDetails(Constants.SERVICE_NAME);
			}
			catch (Exception e)
			{
				e.printStackTrace(System.err);
				throw new RuntimeException(e);
			}
		}

		public void run()
		{
			runnable.run();
		}

		@Override
		public SessionDetails getSessionDetails()
		{
			return sessionDetails;
		}
	}
}
