package org.piangles.gateway.handling.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.ConfigService;
import org.piangles.backbone.services.config.Configuration;
import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;
import org.piangles.gateway.handling.requests.dto.Request;

public class ConfigRequestProcessor extends AbstractRequestProcessor<String, Configuration>
{
	private ConfigService configService = Locator.getInstance().getConfigService();
	
	public ConfigRequestProcessor()
	{
		super(Endpoints.GetConfiguration.name(), String.class);
	}
	
	@Override
	protected Configuration processRequest(ClientDetails clientDetails, Request request, String configId) throws Exception
	{
		return configService.getConfiguration(configId);
	}
}
