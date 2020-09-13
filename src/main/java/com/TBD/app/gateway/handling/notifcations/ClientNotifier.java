package com.TBD.app.gateway.handling.notifcations;

import com.TBD.app.gateway.handling.ClientDetails;

/**
 * Actively listens for messages from the messaging bus 
 * >>> meant for this particular client / session
 * and picks them up and sends them via Websocket
 * 
 * this could also be used for Ping/Pong messages
 *
 */

//Create a Kafka listener for this.
public final class ClientNotifier
{
	private ClientDetails clientDetails = null;
	
	public ClientNotifier(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
	}
	
	public void start(){}
	public void stop(){}
	public void subscribe(){System.out.println("SUBSCRIBING>>>>>>>>>>>>>>>>>.");}
	public void unsubscribe(){}
}
