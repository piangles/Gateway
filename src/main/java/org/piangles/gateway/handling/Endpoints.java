package org.piangles.gateway.handling;

public enum Endpoints
{
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
