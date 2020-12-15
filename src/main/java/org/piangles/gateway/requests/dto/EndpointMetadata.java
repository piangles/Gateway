package org.piangles.gateway.requests.dto;

public final class EndpointMetadata
{
	private String endpoint;
	private boolean synchronousProcessor;
	private boolean sessionValidation;
	private String requestSchema;
	private String responseSchema;
	
	public EndpointMetadata(String endpoint, boolean synchronousProcessor, boolean sessionValidation, String requestSchema, String responseSchema)
	{
		this.endpoint = endpoint;
		this.synchronousProcessor = synchronousProcessor;
		this.sessionValidation = sessionValidation;
		this.requestSchema = requestSchema;
		this.responseSchema = responseSchema;
	}

	public String getEndpoint()
	{
		return endpoint;
	}

	public boolean isSynchronousProcessor()
	{
		return synchronousProcessor;
	}

	public boolean isSessionValidation()
	{
		return sessionValidation;
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
