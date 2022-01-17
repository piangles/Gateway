package org.piangles.gateway.requests;

import java.util.HashMap;
import java.util.Map;

import org.piangles.core.expt.BadRequestException;
import org.piangles.core.expt.NotFoundException;
import org.piangles.core.expt.PayloadTooLargeException;
import org.piangles.core.expt.RequestedFormatNotSupportedException;
import org.piangles.core.expt.ServiceRuntimeException;
import org.piangles.core.expt.UnauthenticatedException;
import org.piangles.core.expt.UnauthorizedException;
import org.piangles.core.expt.UnsupportedMediaException;
import org.piangles.core.expt.ValidationException;
import org.piangles.core.expt.VersionMismatchException;
import org.piangles.gateway.requests.dto.StatusCode;

public final class StatusCodeMapper
{
	private static StatusCodeMapper self = null;
	
	private Map<String, StatusCode> sreStatusCodeMap = null;
	
	private StatusCodeMapper()
	{
		sreStatusCodeMap = new HashMap<>();
		sreStatusCodeMap.put(BadRequestException.class.getSimpleName(), StatusCode.BadRequest);
		sreStatusCodeMap.put(NotFoundException.class.getSimpleName(), StatusCode.NotFound);
		sreStatusCodeMap.put(PayloadTooLargeException.class.getSimpleName(), StatusCode.PayloadTooLarge);
		sreStatusCodeMap.put(RequestedFormatNotSupportedException.class.getSimpleName(), StatusCode.RequestedFormatNotSupported);
		sreStatusCodeMap.put(UnauthenticatedException.class.getSimpleName(), StatusCode.Unauthenticated);
		sreStatusCodeMap.put(UnauthorizedException.class.getSimpleName(), StatusCode.Unauthorized);
		sreStatusCodeMap.put(UnsupportedMediaException.class.getSimpleName(), StatusCode.UnsupportedMedia);
		sreStatusCodeMap.put(ValidationException.class.getSimpleName(), StatusCode.ValidationFailure);
		sreStatusCodeMap.put(VersionMismatchException.class.getSimpleName(), StatusCode.VersionMismatch);
	}
	
	public static synchronized StatusCodeMapper getInstance()
	{
		if (self == null)
		{
			self = new StatusCodeMapper();
		}
		
		return self;
	}
	
	public StatusCode getStatusCode(ServiceRuntimeException sre)
	{
		return sreStatusCodeMap.get(sre.getClass().getSimpleName());
	}
}
