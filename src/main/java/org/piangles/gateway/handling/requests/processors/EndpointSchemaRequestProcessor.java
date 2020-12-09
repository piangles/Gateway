package org.piangles.gateway.handling.requests.processors;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.RequestProcessor;
import org.piangles.gateway.handling.requests.RequestRouter;
import org.piangles.gateway.handling.requests.dto.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.databind.jsonSchema.types.JsonSchema;

public final class EndpointSchemaRequestProcessor extends AbstractRequestProcessor<String, String>
{
	public EndpointSchemaRequestProcessor()
	{
		super(Endpoints.EndpointSchema.name(), false, String.class);
	}
	
	@Override
	protected String processRequest(ClientDetails clientDetails, Request request, String endpoint) throws Exception
	{
		String schema = null;
		RequestProcessor rp = RequestRouter.getInstance().getRequestProcessor(endpoint);
		
		if (rp != null)
		{
	        ObjectMapper mapper = new ObjectMapper();
	        SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
	        mapper.acceptJsonFormatVisitor(rp.getAppReqClass(), visitor);
	        JsonSchema jsonSchema = visitor.finalSchema();
	        schema = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonSchema);
		}
		
		return schema;
	}

	@Override
	public boolean shouldValidateSession()
	{
		return false;
	}
}
