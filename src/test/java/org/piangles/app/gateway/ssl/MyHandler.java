package org.piangles.app.gateway.ssl;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


public class MyHandler extends WebSocketHandler {
    
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.register(MyListener.class);
    }
}