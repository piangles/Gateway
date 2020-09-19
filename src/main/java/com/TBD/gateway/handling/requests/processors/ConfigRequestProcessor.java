package com.TBD.gateway.handling.requests.processors;

import com.TBD.backbone.services.Locator;
import com.TBD.backbone.services.config.ConfigService;
import com.TBD.backbone.services.config.Configuration;
import com.TBD.gateway.handling.ClientDetails;
import com.TBD.gateway.handling.Endpoints;

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
