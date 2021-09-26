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
 
 
 
package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.geo.GeoLocation;
import org.piangles.backbone.services.geo.GeoLocationService;
import org.piangles.core.expt.NotFoundException;
import org.piangles.core.expt.ValidationException;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.client.Location;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.dto.LocationRequest;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.SimpleResponse;

public class SetLocationRequestProcessor extends AbstractRequestProcessor<LocationRequest, SimpleResponse>
{
	private GeoLocationService geolocationService = Locator.getInstance().getGeoLocationService();
	
	public SetLocationRequestProcessor()
	{
		super(Endpoints.SetLocation, LocationRequest.class, SimpleResponse.class);
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, LocationRequest locationRequest) throws Exception
	{
		SimpleResponse simpleResponse = null;
		GeoLocation geoLocation = null; 
		boolean precise = true;
		
		if (	(locationRequest.getLocation() == null || geolocationService.isValid(locationRequest.getLocation().convert())) &&
				(locationRequest.getPhysicalAddress() == null || locationRequest.getPhysicalAddress().isValid()) &&
				(locationRequest.getZipCode() == null || !locationRequest.getZipCode().isValid())
			)
		{
			throw new ValidationException("LocationRequest received is not a valid or does not have valid values.");
		}
		else
		{
			String fromName = null;
			if (locationRequest.getLocation() != null)
			{
				fromName = "Location";
				geoLocation = locationRequest.getLocation().convert();
			}
			else if (locationRequest.getPhysicalAddress() != null)
			{
				fromName = "PhysicalAddress";
				geoLocation = geolocationService.getGeoLocation(locationRequest.getPhysicalAddress());
			}
			else
			{
				fromName = "ZipCode";
				geoLocation = geolocationService.getGeoLocation(locationRequest.getZipCode());
				precise = false;
			}
			
			if (geoLocation != null)
			{
				clientDetails.setLocation(Location.convert(geoLocation, precise));
				simpleResponse = new SimpleResponse("GeoLocation was determined successfully.");
			}
			else
			{
				throw new NotFoundException(String.format("Unable to determine GeoLocation from %s .", fromName));
			}
		}
		
		return simpleResponse; 
	}
}
