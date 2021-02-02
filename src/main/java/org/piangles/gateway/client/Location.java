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
 
 
package org.piangles.gateway.client;

import org.piangles.backbone.services.geo.GeoLocation;

public final class Location
{
	private double latitude  = 0.0;
	private double longitude  = 0.0;
	private boolean precise = false; 

	public Location(double latitude, double longitude, boolean precise)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.precise = precise;
	}

	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public boolean isPrecise()
	{
		return precise;
	}
	
	public GeoLocation convert()
	{
		return new GeoLocation(latitude, longitude);
	}
	
	public static Location convert(GeoLocation geoLocation, boolean precise)
	{
		return new Location(geoLocation.getLatitude().getDecimalValue(), geoLocation.getLongitude().getDecimalValue(), precise);
	}
}
