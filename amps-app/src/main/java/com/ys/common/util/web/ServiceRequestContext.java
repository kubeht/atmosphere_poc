package com.ys.common.util.web;

import com.ys.common.domain.device.DeviceInfo;

public class ServiceRequestContext {
	private String authToken;
	private Long userId;
	private DeviceInfo deviceInfo;
	
	public String getAuthToken() {
		return authToken;
	}

	public Long getUserId() {
		return userId;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setAuthToken(String token) {
		authToken = token;
	}

}
