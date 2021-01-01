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
 
 
 
package org.piangles.app.gateway;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.schema.JsonSchema;
import org.piangles.core.util.reflect.TypeToken;
import org.piangles.gateway.requests.dto.Ping;

public final class JsonSchemaGenerator
{

	private JsonSchemaGenerator()
	{
	};

	public static void main(String[] args) throws IOException
	{
		System.out.println(JsonSchemaGenerator.getJsonSchema(new TypeToken<List<String>>(){}.getActualClass()));
		
		System.out.println(JsonSchemaGenerator.getJsonSchema(Ping.class));
	}

	public static String getJsonSchema(Class clazz) throws IOException
	{
		org.codehaus.jackson.map.ObjectMapper mapper = new ObjectMapper();
		//ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
		JsonSchema schema = mapper.generateJsonSchema(clazz);
		return mapper.defaultPrettyPrintingWriter().writeValueAsString(schema);
	}
}
