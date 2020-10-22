package org.piangles.gateway.service.impl.jetty;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;
import org.piangles.core.services.remoting.SessionDetailsCreator;
import org.piangles.gateway.Constants;
import org.piangles.gateway.GatewayService;

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
		
        ServletContextHandler defaultContext = new ServletContextHandler();
        FilterHolder cors = defaultContext.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        // Use a DefaultServlet to serve static files.
        // Alternate Holder technique, prepare then add.
        // DefaultServlet should be named 'default'
        ServletHolder defaultServletHolder = new ServletHolder("default", DefaultServlet.class);
        defaultServletHolder.setInitParameter("resourceBase","./http/");
        defaultServletHolder.setInitParameter("dirAllowed","false");
        defaultContext.addServlet(defaultServletHolder,"/");
        
        List<Handler> webSocketHandlerList = new ArrayList<>();
        webSocketHandlerList.add(defaultContext);
        
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(webSocketHandlerList.toArray(new Handler[0]));
        server.setHandler(handlerCollection);
        
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
		public Thread newThread(Runnable runnable)
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
