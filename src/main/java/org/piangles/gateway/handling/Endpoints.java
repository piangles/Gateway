package org.piangles.gateway.handling;

public enum Endpoints
{
	ListEndpoints,
	EndpointSchema,
	
	SignUp,
	Login,
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
