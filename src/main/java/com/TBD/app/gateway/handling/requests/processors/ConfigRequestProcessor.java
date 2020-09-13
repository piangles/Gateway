package com.TBD.app.gateway.handling.requests.processors;

import com.TBD.app.gateway.handling.ClientDetails;
import com.TBD.appcore.locator.BackboneServiceLocator;
import com.TBD.backbone.services.config.ConfigService;
import com.TBD.backbone.services.config.Configuration;

public class ConfigRequestProcessor extends AbstractRequestProcessor<String, Configuration>
{
	private ConfigService configService = BackboneServiceLocator.getInstance().getConfigService();
	
	public ConfigRequestProcessor()
	{
		super("GetConfiguration", String.class);
	}
	
	@Override
	public Configuration processRequest(ClientDetails clientDetails, String configId) throws Exception
	{
		return configService.getConfiguration(configId);
	}
}
