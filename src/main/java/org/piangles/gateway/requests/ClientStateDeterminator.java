package org.piangles.gateway.requests;

import org.piangles.backbone.services.profile.BasicUserProfile;
import org.piangles.gateway.client.ClientState;

public final class ClientStateDeterminator
{
	public static ClientState determine()
	{
		return ClientState.PreAuthentication;
	}
	
	public static ClientState determine(boolean authenticatedByToken, BasicUserProfile userProfile)
	{
		ClientState state = ClientState.PreAuthentication;
		
		if (authenticatedByToken)
		{
			state = ClientState.MidAuthenticationResetPasswordRequired;
		}
		else if (userProfile.isMFAEnabled())
		{
			state = ClientState.MidAuthenticationMFARequired;
		}
		else
		{
			state = ClientState.PostAuthentication;
		}

		return state;
	}

	public static ClientState determine(boolean authenticatedByToken)
	{
		return null;
	}
	
	public static boolean isMidAuthentication(ClientState state)
	{
		return ClientState.MidAuthenticationResetPasswordRequired.equals(state) || ClientState.MidAuthenticationMFARequired.equals(state);
	}
}
