package com.me.amps.apps.listener;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.amps.core.MessageExchangeService;
import com.me.amps.domain.dto.Message;

public final class WebSocketEventListenerAdapterImpl extends
		WebSocketEventListenerAdapter {
	private final static Logger logger = LoggerFactory
			.getLogger(WebSocketEventListenerAdapterImpl.class);
	private final MessageExchangeService subscriptionService = new MessageExchangeService();

	public WebSocketEventListenerAdapterImpl() {
		logger.info("{} is being instantiated", this);
	}

	@Override
	public void onBroadcast(AtmosphereResourceEvent event) {
		subscriptionService.onBroadcast(event.getResource(),
				(Message) event.getMessage());
	}

	@Override
	public void onDisconnect(AtmosphereResourceEvent event) {
		if (event.isCancelled()) {
			logger.info("onDisconnect: Unexpected closing {}", event);
		} else if (event.isClosedByClient()) {
			logger.info("onDisconnect: client closing {}", event);
		} else if (event.isClosedByApplication()) {
			logger.info("onDisconnect: server closing {}", event);
		} else {
			logger.info("onDisconnect: {}", event);
		}
	}

	@Override
	public void onThrowable(AtmosphereResourceEvent event) {
		logger.warn("onThrowable:{}", event);
	}

	@Override
	public void onClose(AtmosphereResourceEvent event) {
		logger.info("onClose:{}", event);
	}

	@Override
	public void onHeartbeat(AtmosphereResourceEvent event) {
		logger.debug("onHeartbeat:{}", event);
	}

    @Override
    public void onHandshake(WebSocketEvent event) {
		logger.info("onHandshake:{}", event);
    }

    @Override
    public void onClose(WebSocketEvent event) {
		logger.info("onClose:{}", event);
    }

    @Override
    public void onDisconnect(WebSocketEvent event) {
		logger.info("onDisconnect:{}", event);
    }


}