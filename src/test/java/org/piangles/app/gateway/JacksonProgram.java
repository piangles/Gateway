package org.piangles.app.gateway;

import java.io.IOException;

import org.piangles.gateway.requests.dto.Ping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public class JacksonProgram {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(Ping.class, visitor);
        JsonSchema schema = visitor.finalSchema();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
    }
}