package org.piangles.gateway.requests.dto;

public final class EndpointMetadata
{
	private String endpoint;
	private String description;
	private String communicationPattern;
	private boolean validSessionNeeded;
	private String requestSchema;
	private String responseSchema; //TODO Would need Stream /Event details 
	
	public EndpointMetadata(String endpoint, String description, String communicationPattern, boolean validSessionNeeded, String requestSchema, String responseSchema)
	{
		this.endpoint = endpoint;
		this.description = description;
		this.communicationPattern = communicationPattern;
		this.validSessionNeeded = validSessionNeeded;
		this.requestSchema = requestSchema;
		this.responseSchema = responseSchema;
	}

	public String getEndpoint()
	{
		return endpoint;
	}

	public String getDescription()
	{
		return description;
	}

	public String getCommunicationPattern()
	{
		return communicationPattern;
	}

	public boolean isValidSessionNeeded()
	{
		return validSessionNeeded;
	}

	public String getRequestSchema()
	{
		return requestSchema;
	}

	public String getResponseSchema()
	{
		return responseSchema;
	}
}
