package com.me.amps.core;

import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.amps.core.utils.ServiceRequestContextUtils;
import com.me.amps.domain.MessageSubscribeServiceConstants;
import com.ys.common.util.web.ServiceRequestContext;

class AuthenticationProvider {
	private final static Logger logger = LoggerFactory
			.getLogger(AuthenticationProvider.class);

	public boolean check(AtmosphereResource r) {
		try {
			ServiceRequestContext serviceRequestContext = null;
			serviceRequestContext = ServiceRequestContextUtils.retrieve(r);
			String token = serviceRequestContext.getAuthToken();
			if (token == null) {
				logger.warn(String.format(
						"[bid=%s, rid=%s, transport=%s] : %s", r
								.getBroadcaster().getID(), r.uuid(), r
								.transport(), "missing authentication token"));
				return false;
			} else if (MessageSubscribeServiceConstants.HT_SVC_REQUEST_DOOMED_TOKEN.equals(token)) {
				logger.warn(String.format(
						"[bid=%s, rid=%s, transport=%s] : %s", r
								.getBroadcaster().getID(), r.uuid(), r
								.transport(), "missing invalid authentication token"));
				return false;
			} else {
				// TODO: add logic here
				return true;
			}
		} catch (Exception e) {
			logger.error(String.format("[bid=%s, rid=%s, transport=%s] : %s", r
					.getBroadcaster().getID(), r.uuid(), r.transport(),
					"authentication error"), e);
			return false;
		}
	}
}
