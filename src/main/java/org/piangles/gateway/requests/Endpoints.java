/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
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
	@Description(content="Allows the user to login and on succesful login creates a session.")
	Login,
	@Description(content="Enables the user to generate a temporary password in the the event the user does not remember the password.")
	GenerateResetToken,
	
	@Description(content="Allows the user to change passoword post login.")
	ChangePassword,
	@Description(content="Allows the user to logout and terminates the session.")
	Logout,
	
	@Description(content="A systematic approach to keep the socket alive.")
	Ping,
	@Description(content="A systematic approach to keep the session alive.")
	KeepSessionAlive,

	@Description(content="During registration process this endpoint helps in creation of a basic profile.")
	CreateUserProfile,
	@Description(content="Allows the user to update the details of the profile.")
	UpdateUserProfile,
	@Description(content="Returns the latest user profile.")
	GetUserProfile,
	
	@Description(content="Returns user preferences that are captured systematically.")
	GetUserPreferences,
	@Description(content="Updates the user preferences through the course of application usage.")
	UpdateUserPreferences,

	@Description(content="Returns any configuration required by the client application.")
	GetConfiguration,

	@Description(content="Registers the current connection to receive application and system notifications.")
	Subscribe,
}
