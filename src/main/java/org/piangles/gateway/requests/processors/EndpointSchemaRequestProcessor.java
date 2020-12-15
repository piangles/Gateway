package org.piangles.gateway.requests.processors;

import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestProcessor;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.EndpointMetadata;
import org.piangles.gateway.requests.dto.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public final class EndpointSchemaRequestProcessor extends AbstractRequestProcessor<String, EndpointMetadata>
{
	public EndpointSchemaRequestProcessor()
	{
		super(Endpoints.EndpointSchema.name(), false, String.class, EndpointMetadata.class);
	}
	
	@Override
	protected EndpointMetadata processRequest(ClientDetails clientDetails, Request request, String endpoint) throws Exception
	{
		EndpointMetadata metadata = null;
		RequestProcessor rp = RequestRouter.getInstance().getRequestProcessor(endpoint);
		
		if (rp != null)
		{
			metadata = new EndpointMetadata(
					endpoint, 
					rp.isAsyncProcessor(), 
					rp.shouldValidateSession(), 
					getSchema(rp.getRequestClass()), 
					getSchema(rp.getResponseClass())); 
		}
		else
		{
			throw new Exception(endpoint + " Metadata not found.");
		}
		
		return metadata;
	}

	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
	
	private String getSchema(Class<?> reqRespClass) throws Exception
	{
        ObjectMapper mapper = new ObjectMapper();
        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
        mapper.acceptJsonFormatVisitor(reqRespClass, visitor);
        JsonSchema jsonSchema = visitor.finalSchema();
        
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
	}
}
