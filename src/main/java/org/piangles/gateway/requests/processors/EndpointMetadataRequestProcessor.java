package org.piangles.gateway.requests.processors;

import org.piangles.core.annotation.Description;
import org.piangles.gateway.CommunicationPattern;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.Endpoints;
import org.piangles.gateway.requests.RequestProcessor;
import org.piangles.gateway.requests.RequestRouter;
import org.piangles.gateway.requests.dto.EndpointMetadata;
import org.piangles.gateway.requests.dto.Request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

public final class EndpointMetadataRequestProcessor extends AbstractRequestProcessor<String, EndpointMetadata>
{
	public EndpointMetadataRequestProcessor()
	{
		super(Endpoints.EndpointMetadata, CommunicationPattern.RequestResponse, String.class, EndpointMetadata.class);
	}
	
	@Override
	protected EndpointMetadata processRequest(ClientDetails clientDetails, Request request, String endpoint) throws Exception
	{
		EndpointMetadata metadata = null;
		RequestProcessor rp = RequestRouter.getInstance().getRequestProcessor(endpoint);
		
		if (rp != null)
		{
			Enum<?> enm = rp.getEndpoint();
			Description desc = enm.getClass().getField(enm.name()).getAnnotation(Description.class);
			String description = null;
			if (desc != null)
			{
				description = desc.content();
			}
			
			metadata = new EndpointMetadata(
					endpoint, 
					description,
					rp.getCommunicationPattern().name() + " : " + rp.getCommunicationPattern().description(),
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
