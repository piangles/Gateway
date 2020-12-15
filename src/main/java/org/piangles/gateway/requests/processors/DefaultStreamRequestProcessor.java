package org.piangles.gateway.requests.processors;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.core.stream.PassThruStreamProcessor;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamProcessor;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.requests.ClientDetails;
import org.piangles.gateway.requests.ResponseSender;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.SimpleResponse;

public final class DefaultStreamRequestProcessor<AppReq, SI, SO> extends AbstractRequestProcessor<AppReq, SimpleResponse>
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private StreamCreator<AppReq, SI> srp = null;
	private StreamProcessor<SI, SO> processor = null;

	public DefaultStreamRequestProcessor(StreamCreator<AppReq, SI> srp)
	{
		this(srp, new PassThruStreamProcessor<SI, SO>());
	}

	public DefaultStreamRequestProcessor(StreamCreator<AppReq, SI> srp, StreamProcessor<SI, SO> processor)
	{
		super(srp.getEndpoint(), srp.getRequestClass(), SimpleResponse.class);
		this.srp = srp;
		this.processor = processor;
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, AppReq appRequest) throws Exception
	{
		Stream<SI> stream = srp.createStream(appRequest);
		
		stream.processAsync((payload) -> {
			try
			{
				SO output = processor.process(payload);

				String appResponseAsStr = new String(JSON.getEncoder().encode(output));
				
				Response response = new Response(request.getTraceId(), request.getEndpoint(), true, appResponseAsStr);
				ResponseSender.sendResponse(clientDetails, response);
			}
			catch (Exception e)
			{
				logger.error("Error while processing stream data because of : " + e.getMessage(), e);
			}
			return null;
		});
		
		return new SimpleResponse(true);
	}
}
