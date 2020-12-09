package org.piangles.app.gateway;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.schema.JsonSchema;
import org.piangles.gateway.handling.requests.dto.Ping;

public final class JsonSchemaGenerator
{

	private JsonSchemaGenerator()
	{
	};

	public static void main(String[] args) throws IOException
	{
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