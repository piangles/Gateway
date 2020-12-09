package org.piangles.app.gateway;

import java.io.IOException;

import org.piangles.gateway.handling.requests.dto.ChangePasswordRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.databind.jsonSchema.types.JsonSchema;

public class JacksonProgram {

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(ChangePasswordRequest.class, visitor);
        JsonSchema schema = visitor.finalSchema();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema));
    }
}