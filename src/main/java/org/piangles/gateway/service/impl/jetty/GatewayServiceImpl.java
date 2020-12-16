package org.piangles.gateway.service.impl.jetty;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
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
		/**
		 * Jetty HTTP Servlet Server.
		 * This class is the main class for the Jetty HTTP Servlet server.
		 * It aggregates Connectors (HTTP request receivers) and request Handlers.
		 * The server is itself a handler and a ThreadPool.  Connectors use the ThreadPool methods
		 * to run jobs that will eventually call the handle method.
		 */
		server = new Server(new GatewayThreadPool());
	}

	public void init(String host, int port) throws Exception
	{
		ServerConnector connector = new ServerConnector(server);
		connector.setHost(host);
		connector.setPort(port);
		
		//A server can listen on multiple ports ex: http and https
		server.addConnector(connector);

		/**
		 * Setup the basic application "context" for this application at "/"
		 * This is also known as the handler tree (in jetty speak)

         * Use a DefaultServlet to serve static files. Alternate Holder technique, prepare then add.
         * DefaultServlet should be named 'default'
         */
        ServletContextHandler defaultContext = new ServletContextHandler();
        defaultContext.setContextPath("/");
        defaultContext.setWelcomeFiles(new String[] { "index.html" });
        /**
         * web is the folder under resources where index.html exists
         * the index.html needs to reside under a subdirectory of resources.
         */
        String path = getClass().getClassLoader().getResource("web").toExternalForm();
        defaultContext.setResourceBase(path);
        defaultContext.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed","false");
        defaultContext.addServlet(DefaultServlet.class, "/");
        
        /**
         * This is to allow for CORS, when the UI is served from one host but the
         * Webservices (Websocket API) is on another server.
         */
        FilterHolder cors = defaultContext.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

		/**
		 * Create a Handler for Websocket protocol, on connection from a client
		 * the server determines the protocol from url
		 * http://host:port/
		 * or
		 * ws://host:port 
		 * and uses the appropriate handler. So it it sends an Upgrade message.
		 * 
		 * TODO
		 * Configure websocket behavior
		 * wsfilter.getFactory().getPolicy().setIdleTimeout(5000);
		 */
        // Add the websocket filter
        WebSocketUpgradeFilter wsfilter = WebSocketUpgradeFilter.configure(defaultContext);
        // Add websocket mapping
        wsfilter.addMapping(new ServletPathSpec("/api/"),new WebSocketCreatorImpl());
        
		server.setHandler(defaultContext);
	}

	public void startProcessingRequests() throws Exception
	{
		System.out.println("GatewayService is being started...");
		server.start();
		server.dump(System.err);
		System.out.println("GatewayService has started and is ready to process requests.");
		server.join();
	}

	/**
	 * A thread pool for the Server to create ClientHandlerThread.
	 * Everytime a client establishes connection a ClientHandlerThread
	 * from the pool services the connection.
	 * 
	 * This is protocol agnostic.
 	 */
	class GatewayThreadPool extends QueuedThreadPool
	{
		@Override
		public Thread newThread(Runnable runnable)
		{
			return new ClientHandlerThread(runnable);
		}
	}

    class WebSocketCreatorImpl implements WebSocketCreator
    {
        @Override
        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp)
        {
            return new WebSocketLifecycleEventHandler();
        }
    }

	/**
	 * This needs to be SessionAwareable to obtain access to basic services
	 * like logging.
	 */
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
