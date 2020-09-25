package org.piangles.gateway.handling.notifcations;

import java.util.List;

import org.piangles.gateway.handling.ClientDetails;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.ctrl.ControlChannelException;

/**
 * Actively listens for messages from the messaging bus >>> meant for this
 * particular client / session and picks them up and sends them via Websocket
 * 
 * this could also be used for Ping/Pong messages
 *
 */


// Create a Kafka listener for this.
public final class ClientNotifier
{
	/**
	 * Topics org.piangles.gateway.control.user.<UserId>
	 */
	private ClientDetails clientDetails = null;

	public ClientNotifier(ClientDetails clientDetails)
	{
		this.clientDetails = clientDetails;
	}

	public void start()
	{
		try
		{
			List<String> userTopics = Locator.getInstance().getChannelControlService().getTopicsFor(clientDetails.getSessionDetails().getUserId());
			System.out.println("USER TOPICS ::::::::::::::::::::::" + userTopics);
		}
		catch (ControlChannelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stop()
	{
	}

	public void subscribeToTopic(String topic)
	{
		System.out.println("SYSTEM TOPIC ::::::::::::::::::::::" + topic);
	}

	public void subscribeToAlias(List<String> aliases)
	{
		try
		{
			List<String> aliasTopics = Locator.getInstance().getChannelControlService().getTopicsForAliases(aliases);
			System.out.println("ALIAS TOPICS ::::::::::::::::::::::" + aliasTopics);
		}
		catch (ControlChannelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void unsubscribe()
	{
	}
}
