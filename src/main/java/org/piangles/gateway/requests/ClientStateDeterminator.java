package org.piangles.gateway.requests;

import org.piangles.gateway.client.ClientState;
import org.piangles.gateway.requests.dto.AuthenticationDetails;

public final class ClientStateDeterminator
{
	public static ClientState determine(AuthenticationDetails authDetails)
	{
		ClientState state = ClientState.PreAuthentication;
		
		if (authDetails != null && authDetails.isAuthenticated())
		{
			if (authDetails.isAuthenticatedBySession())
			{
				if (authDetails.isMFAEnabled() && !authDetails.isAuthenticatedByMultiFactor())
				{
					state = ClientState.MidAuthentication;
				}
				else
				{
					state = ClientState.PostAuthentication;
				}
			}
			else if (authDetails.isAuthenticatedByToken())
			{
				state = ClientState.MidAuthentication;
			}
			else if (authDetails.isMFAEnabled())
			{
				state = ClientState.MidAuthentication;
			}
			else
			{
				state = ClientState.PostAuthentication;
			}
		}
		
		return state;
	}
}
