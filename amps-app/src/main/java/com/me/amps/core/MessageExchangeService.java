package com.me.amps.core;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.MetaBroadcaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.amps.core.SubscriptionStatusListener.Status;
import com.me.amps.core.utils.ContextException;
import com.me.amps.core.utils.ServiceRequestContextUtils;
import com.me.amps.domain.dto.Error;
import com.me.amps.domain.dto.Message;
import com.me.amps.domain.dto.MessageType;

public class MessageExchangeService {
	private final static Logger logger = LoggerFactory
			.getLogger(MessageExchangeService.class);
	private final SubscriptionStatusListener authenticationService = new SubscriptionStatusListener();
	private final AuthenticationProvider authenticationProvider = new AuthenticationProvider();
	private final Set<MessageType> defaultSystemMessageType = Collections
			.unmodifiableSet(EnumSet.of(MessageType.SUSPEND_RESPONSE,
					MessageType.SUCCESSFUL_AUTHENTICATION_RESPONSE,
					MessageType.FAILED_AUTHENTICATED_RESPONSE,
					MessageType.CONTEXT_ERROR_RESPONSE));

	public enum ProcessAction {
		GO, STOP
	};

	public MessageExchangeService() {
		super();
		logger.info("{} instanstiated", this);
	}

	
	/**
	 * Called from {@link com.me.amps.apps.interceptor.AtmosphereInterceptorImpl}
	 * By intercepting the request, we block messages coming in during the authentication hand shaking period.
	 *   
	 * @param r
	 * @return {@link ProcessAction}
	 */
	public ProcessAction interceptRequest(AtmosphereResource r) {
		ProcessAction pa = ProcessAction.GO;
		Status currentStatus = authenticationService.status(r);
		// initial state
		if (SubscriptionStatusListener.Status.UNAVAILABLE == authenticationService
				.status(r)) {
			authenticationService.onAuthenticationPending(r);
			pa = ProcessAction.GO;
		} else if (SubscriptionStatusListener.Status.AUTH_SUCCESSFUL == authenticationService
				.status(r)) { // authenticated state
			pa = ProcessAction.GO;
		} else {
			logger.warn(
					"[bid={}, rid={}, transport={}, status={}] : dropping request",
					r.getBroadcaster().getID(), r.uuid(), r.transport(),
					currentStatus);
			pa = ProcessAction.STOP;
		}
		return pa;

	}

	/**
	 * Called from {@link com.me.amps.apps.filter.BroadcastFilterImpl}
	 * By intercepting the out-going broadcast, we block messages being sent out during the authentication hand shaking period.
	 * 
	 * @param broadcasterId
	 * @param r
	 * @param originalMessage
	 * @param message
	 * @return
	 */
	public ProcessAction interceptBroadcast(String broadcasterId,
			AtmosphereResource r, Object originalMessage, Object message) {
		SubscriptionStatusListener.Status currentStatus = authenticationService
				.status(r);
		MessageType messageType = ((Message) message).getType();
		if (defaultSystemMessageType.contains(messageType)) {
			return logAndReturn(r, currentStatus, ProcessAction.GO, message);
		} else if (currentStatus == SubscriptionStatusListener.Status.AUTH_SUCCESSFUL) {
			return logAndReturn(r, currentStatus, ProcessAction.GO, message);
		} else if (currentStatus == SubscriptionStatusListener.Status.AUTH_PENDING) {
			return logAndReturn(r, currentStatus, ProcessAction.STOP, message);
		} else {
			return logAndReturn(r, currentStatus, ProcessAction.STOP, message);
		}
	}

	public Response doGET(AtmosphereResource r) {
		try {
			setupChannel(r);
			boolean ok = authenticationProvider.check(r);
			if (!ok) {
				handleClientError(r, MessageType.FAILED_AUTHENTICATED_RESPONSE,
						"authentication error");
			} else {
				handleAuthentionSuccess(r);
			}
		} catch (ContextException e) {
			handleClientError(r, MessageType.CONTEXT_ERROR_RESPONSE,
					e.getMessage());
		}

		return Response
				.status(javax.ws.rs.core.Response.Status.OK)
				.entity(new Message.Builder()
						.type(MessageType.SUSPEND_RESPONSE).build()).build();
	}

	public Response doPOST(AtmosphereResource r, Message message) {
		try {
			setupChannel(r);
		} catch (ContextException e) {
			handleClientError(r, MessageType.CONTEXT_ERROR_RESPONSE,
					e.getMessage());
		}

		return Response.status(javax.ws.rs.core.Response.Status.OK)
				.entity(message).build();

	}

	/**
	 * Called from {@link com.me.amps.apps.listener.WebSocketEventListenerAdapterImpl#onBroadcast()}, listening on broadcast 
	 * and delegate the event to {@link SubscriptionStatusListener}
	 *   
	 * @param r
	 * @param message
	 */
	public void onBroadcast(AtmosphereResource r, Message message) {
		if (MessageType.SUCCESSFUL_AUTHENTICATION_RESPONSE == message.getType()) {
			authenticationService.onAuthenticationSuccess(r);
		} else if (MessageType.FAILED_AUTHENTICATED_RESPONSE == message
				.getType()) {
			authenticationService.onAuthenticationFailure(r);
		}
	}

	/**
	 * broadcast message to the matched broadcasterID pattern
	 * @param broadcasterID
	 * @param message
	 */
	@SuppressWarnings("deprecation")
	public void doBroadcast(String broadcasterID, Message message) {
		MetaBroadcaster.getDefault().broadcastTo(broadcasterID, message);
	}

	private void handleAuthentionSuccess(final AtmosphereResource r) {
		r.addEventListener(new AtmosphereResourceEventListenerAdapter() {
			@Override
			public void onSuspend(AtmosphereResourceEvent event) {
				event.getResource()
						.getBroadcaster()
						.broadcast(
								new Message.Builder()
										.type(MessageType.SUCCESSFUL_AUTHENTICATION_RESPONSE)
										.build());
			}
		});
	}

	private void handleClientError(final AtmosphereResource r,
			final MessageType messageType, final String errorMessage) {
		r.addEventListener(new AtmosphereResourceEventListenerAdapter() {
	
			@Override
			public void onSuspend(AtmosphereResourceEvent event) {
				event.getResource()
						.getBroadcaster()
						.broadcast(
								new Message.Builder().type(messageType)
										.error(new Error(errorMessage)).build());
			}
	
			@Override
			public void onBroadcast(AtmosphereResourceEvent event) {
				AtmosphereResource r = event.getResource();
				logger.info(
						"[bid={}, rid={}, transport={}] : resuming connection after sent the message: {}",
						r.getBroadcaster().getID(), r.uuid(), r.transport(),
						event.getMessage());
				event.getResource().resume();
			}
	
		});
	
	}

	private ProcessAction logAndReturn(AtmosphereResource r,
			SubscriptionStatusListener.Status currentStatus, ProcessAction pa,
			Object message) {
		if (ProcessAction.GO == pa) {
			logger.info(
					"[bid={}, rid={}, transport={}, status={}] : pushing message {}",
					r.getBroadcaster().getID(), r.uuid(), r.transport(),
					currentStatus, message);

		} else {
			logger.warn(
					"[bid={}, rid={}, transport={}, status={}] : discarding message {}",
					r.getBroadcaster().getID(), r.uuid(), r.transport(),
					currentStatus, message);
		}
		return pa;

	}

	private void setupChannel(AtmosphereResource r) throws ContextException {
		String broadcasterId = null;
		try {
			broadcasterId = ServiceRequestContextUtils.getBroadCasterId(r);
		} catch (ContextException ce) {
			logger.error(
					"Not able to derive broadcaster id, using {}. Error: {}",
					r.uuid(), ce.getMessage());
			broadcasterId = r.uuid();
			throw ce;
		} finally {
			Broadcaster br = r.getAtmosphereConfig().getBroadcasterFactory()
					.lookup(broadcasterId, true);
			r.setBroadcaster(br);
		}
	}

}
