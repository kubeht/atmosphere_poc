package com.me.amps.core;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceSession;
import org.atmosphere.cpr.AtmosphereResourceSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SubscriptionStatusListener {
	private final static String HT_SVC_RESOURCE_AUTHENTICATION_STATE = "HT_SVC_RESOURCE_AUTHENTICATION_STATE";
	private AtmosphereResourceSessionFactory factory = AtmosphereResourceSessionFactory
			.getDefault();
	private final static Logger logger = LoggerFactory
			.getLogger(SubscriptionStatusListener.class);

	public enum Status {
		UNAVAILABLE, AUTH_PENDING, AUTH_SUCCESSFUL, AUTH_FAILED, SYSTEM_ERROR
	}

	public SubscriptionStatusListener() {
		super();
	}

	public Status status(AtmosphereResource resource) {
		AtmosphereResourceSession session = factory.getSession(resource);
		Status status = session.getAttribute(
				HT_SVC_RESOURCE_AUTHENTICATION_STATE, Status.class);
		return status == null ? Status.UNAVAILABLE : status;
	}

	private void setStatus(AtmosphereResource resource, Status value) {
		AtmosphereResourceSession session = factory.getSession(resource);
		session.setAttribute(HT_SVC_RESOURCE_AUTHENTICATION_STATE, value);
		logger.info("[bid={}, rid={}, transport={}] : set status = {}",
				resource.getBroadcaster().getID(), resource.uuid(),
				resource.transport(), value);

	}

	public void onAuthenticationSuccess(AtmosphereResource resource) {
		setStatus(resource, Status.AUTH_SUCCESSFUL);
	}

	public void onAuthenticationFailure(AtmosphereResource resource) {
		setStatus(resource, Status.AUTH_FAILED);
	}

	public void onAuthenticationPending(AtmosphereResource resource) {
		setStatus(resource, Status.AUTH_PENDING);
	}

}
