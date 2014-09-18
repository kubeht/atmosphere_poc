package com.me.amps.wasync.common;

import org.codehaus.jackson.map.ObjectMapper;

import com.ys.common.domain.device.DeviceInfo;
import com.ys.common.util.web.ServiceRequestContext;

public class ServiceRequestContextBuilder {
    private final static ObjectMapper mapper = new ObjectMapper();
	ServiceRequestContext result = new ServiceRequestContext();

	public ServiceRequestContextBuilder() {}
	
	public ServiceRequestContextBuilder user(Long userId) {
		result.setUserId(userId);
		return this;
	}

	public ServiceRequestContextBuilder device(DeviceInfo.Type deviceType) {
		result.setDeviceInfo(new DeviceInfo(deviceType));
		return this;
	}
	
	public ServiceRequestContextBuilder authToken(String token) {
		result.setAuthToken(token);
		return this;
	}
	
	public String build() {
		try {
			return mapper.writeValueAsString(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
}
