/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
 
 
package org.piangles.gateway.requests.processors;

import java.util.Optional;

import org.piangles.backbone.services.Locator;
import org.piangles.backbone.services.logging.LoggingService;
import org.piangles.core.stream.PassThruStreamProcessor;
import org.piangles.core.stream.Stream;
import org.piangles.core.stream.StreamProcessor;
import org.piangles.core.util.coding.JSON;
import org.piangles.gateway.client.ClientDetails;
import org.piangles.gateway.requests.ResponseSender;
import org.piangles.gateway.requests.dto.Request;
import org.piangles.gateway.requests.dto.Response;
import org.piangles.gateway.requests.dto.SimpleResponse;
import org.piangles.gateway.requests.dto.StatusCode;

public final class DefaultStreamRequestProcessor<AppReq, SI, SO> extends AbstractRequestProcessor<AppReq, SimpleResponse>
{
	private LoggingService logger = Locator.getInstance().getLoggingService();
	private StreamAcquirer<AppReq, SI> srp = null;
	private StreamProcessor<SI, SO> processor = null;

	public DefaultStreamRequestProcessor(StreamAcquirer<AppReq, SI> srp)
	{
		this(srp, new PassThruStreamProcessor<SI, SO>());
	}

	public DefaultStreamRequestProcessor(StreamAcquirer<AppReq, SI> srp, StreamProcessor<SI, SO> processor)
	{
		super(srp.getEndpoint(), srp.getRequestClass(), SimpleResponse.class);
		this.srp = srp;
		this.processor = processor;
	}
	
	@Override
	protected SimpleResponse processRequest(ClientDetails clientDetails, Request request, AppReq appRequest) throws Exception
	{
		Stream<SI> stream = srp.acquireStream(appRequest);
		
		stream.processAsync((payload) -> {
			try
			{
				Optional<SO> output = processor.process(payload);

				String appResponseAsStr = null;
				if (output.isPresent())
				{
					appResponseAsStr = new String(JSON.getEncoder().encode(output.get()));
				}
				else
				{
					logger.info("Reached EndOfStream for Request : " + request.getTraceId().toString());
				}
				
				Response response = new Response(request.getTraceId(), request.getEndpoint(), request.getReceiptTime(),
													request.getTransitTime(), StatusCode.Success, appResponseAsStr);
				ResponseSender.sendResponse(clientDetails, response);
			}
			catch (Exception e)
			{
				logger.error("Error while processing stream data because of : " + e.getMessage(), e);
			}
			return null;
		});
		
		return new SimpleResponse("Accquired Stream successfully.");
	}
}
