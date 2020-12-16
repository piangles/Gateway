package org.piangles.gateway.requests;

import org.piangles.core.annotation.Description;

public enum Endpoints
{
	@Description(content="Lists all the endpoints this instance of gateway supports.")
	ListEndpoints,
	@Description(content="Given an endpoint, the request processor returns the metadat of that endpoint.")
	EndpointMetadata,
	
	@Description(content="Enables the user to register for the application.")
	SignUp,
	@Description(content="Enables the user to login.")
	Login,
	@Description(content="Enables the user to generate a temporary password in the the event the user does not remember the password.")
	GenerateResetToken,
	
	ChangePassword,
	Logout,
	
	Ping,
	KeepSessionAlive,

	CreateUserProfile,
	UpdateUserProfile,
	GetUserProfile,
	
	GetUserPreferences,
	SetUserPreferences,

	GetConfiguration,

	Subscribe,
}
