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
 
 
 
package org.piangles.gateway.requests.dao;

import org.piangles.backbone.services.config.DefaultConfigProvider;
import org.piangles.core.dao.DAOException;
import org.piangles.core.resources.MongoDataStore;
import org.piangles.core.resources.ResourceManager;
import org.piangles.core.util.abstractions.ConfigProvider;
import org.piangles.gateway.GatewayService;

public class GatewayDAOImpl implements GatewayDAO
{
	private static final String COMPONENT_ID = "cb8e39d5-b0cc-447a-b3d8-ddfbe4af1dd0";
	
	private MongoDataStore mongoDataStore = null;
	
	private UserDeviceInfoDAOImpl userDeviceInfoDAO = null;
	private RequestResponseDAOImpl reqRespDetailsDAO = null;
	
	public GatewayDAOImpl() throws Exception
	{
		ConfigProvider cp = new DefaultConfigProvider(GatewayService.NAME, COMPONENT_ID);
		mongoDataStore = ResourceManager.getInstance().getMongoDataStore(cp);
		
		userDeviceInfoDAO = new UserDeviceInfoDAOImpl(mongoDataStore);
		reqRespDetailsDAO = new RequestResponseDAOImpl(mongoDataStore);
	}
	
	@Override
	public void insertUserDeviceInfo(UserDeviceInfo userDeviceInfo) throws DAOException
	{
		userDeviceInfoDAO.insertUserDeviceInfo(userDeviceInfo);
	}

	@Override
	public void insertRequestResponseDetails(RequestResponseDetails reqRespDetails) throws DAOException
	{
		reqRespDetailsDAO.insertRequestResponseDetails(reqRespDetails);
	}
}
