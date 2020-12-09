package org.piangles.gateway.requests;

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
