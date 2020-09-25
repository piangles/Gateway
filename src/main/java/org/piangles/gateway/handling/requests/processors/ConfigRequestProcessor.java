package org.piangles.gateway.handling.requests.processors;

import org.piangles.gateway.handling.ClientDetails;
import org.piangles.gateway.handling.Endpoints;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.config.ConfigService;
import org.piangles.backbone.services.config.Configuration;

public class ConfigRequestProcessor extends AbstractRequestProcessor<String, Configuration>
{
	private ConfigService configService = Locator.getInstance().getConfigService();
	
	public ConfigRequestProcessor()
	{
		super(Endpoints.GetConfiguration.name(), String.class);
	}
	
	@Override
	public Configuration processRequest(ClientDetails clientDetails, String configId) throws Exception
	{
		return configService.getConfiguration(configId);
	}
}
