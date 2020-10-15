package org.piangles.app.gateway;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.msg.Action;
import org.piangles.backbone.services.msg.ControlDetails;
import org.piangles.backbone.services.msg.DistributionListType;
import org.piangles.backbone.services.msg.Event;
import org.piangles.backbone.services.msg.EventType;
import org.piangles.backbone.services.msg.FanoutRequest;
import org.piangles.backbone.services.msg.MessagingService;
import org.piangles.core.services.remoting.SessionAwareable;
import org.piangles.core.services.remoting.SessionDetails;

public class ControlPublisher extends Thread implements SessionAwareable
{
	public static void main(String[] args) throws Exception
	{
		ControlPublisher cp = new ControlPublisher();
		cp.start();
	}
	
	public void run()
	{
		try
		{
			execute();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void execute() throws Exception
	{
		String topicName = "com.TBD.playground";

		MessagingService ms = Locator.getInstance().getMessagingService();

		for (int i = 11; i < 20; i++)
		{
			System.out.println("Trying to send message No:" + i);
			try
			{
				Event message = new Event(EventType.Control, "" + i, new ControlDetails("Number:" + i, Action.Add, "This is message:" + i));
				FanoutRequest fanoutRequest = new FanoutRequest(DistributionListType.Entity, "UserId", message);
				fanoutRequest.getDistributionList().add("7014b086");
				ms.fanOut(fanoutRequest);
				System.out.println("Message sent successfully");
			}
			catch (Throwable e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("ALL Message sent successfully");
		System.exit(1);
	}

	@Override
	public SessionDetails getSessionDetails()
	{
		return new SessionDetails("LoggingService", "TODOSessionId");
	}
}
