package com.me.amps.core.utils;

import org.atmosphere.cpr.AtmosphereResource;
import org.codehaus.jackson.map.ObjectMapper;

import com.me.amps.domain.MessageSubscribeServiceConstants;
import com.ys.common.domain.device.DeviceInfo;
import com.ys.common.util.web.ServiceRequestContext;

public class ServiceRequestContextUtils {
	
	private final static ObjectMapper mapper = new ObjectMapper();

	public static ServiceRequestContext retrieve(AtmosphereResource r) throws ContextException  {
		String serviceRequestContextString = r.getRequest().getHeader(
				MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT);
		if (serviceRequestContextString == null) {
			throw new ContextException(String.format(
					"client %s: missing header %s",
					r.uuid(),
					MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT));
		}
		ServiceRequestContext src = null;
		try {
			src = mapper.readValue(serviceRequestContextString,
					ServiceRequestContext.class);
		} catch (Exception e) {
			throw new ContextException(String.format(
					"client %s: ServiceRequestContext {%s} is maleformed",
					r.uuid(),
					serviceRequestContextString));
		}
		return src;
	}
	
	
	public static String getBroadCasterId(AtmosphereResource r) throws ContextException {
		ServiceRequestContext src = ServiceRequestContextUtils.retrieve(r);

		String serviceRequestContextString = r.getRequest().getHeader(
				MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT);
		
		Long userId = src.getUserId();
		if (userId == null) {
			throw new ContextException(String.format(
					"userId is missing from {%s}", serviceRequestContextString));
		}
		DeviceInfo.Type deviceType = src.getDeviceInfo() == null ? null : src
				.getDeviceInfo().getType();
		if (deviceType == null) {
			throw new ContextException(String.format(
					"deviceType is missing from {%s}",
					serviceRequestContextString));
		}

		String token = src.getAuthToken();
		if (token == null) {
			throw new ContextException(String.format(
					"authToken is missing from {%s}",
					serviceRequestContextString));
		}

		String broadCasterId = String.format("/%s/%s/%s", userId, deviceType,
				token);

		return broadCasterId;

	}

}
