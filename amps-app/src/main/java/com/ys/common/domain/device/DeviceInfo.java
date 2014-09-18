package com.ys.common.domain.device;


public class DeviceInfo {
	public enum Type{DESKTOP, MOBILE, PHI};
	private Type type; 
	
	
	public DeviceInfo() {}
	
	public DeviceInfo(Type deviceType) {
		type = deviceType;
	}


	public Type getType() {
		return type;
	}
}
