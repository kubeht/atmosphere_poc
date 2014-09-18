package com.me.amps.apps;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.amps.apps.listener.WebSocketEventListenerAdapterImpl;
import com.me.amps.core.MessageExchangeService;
import com.me.amps.domain.MessageSubscribeServiceConstants;
import com.me.amps.domain.dto.Message;

@Path("/admin")
public class AdminResource {
	public static final Logger logger = LoggerFactory
			.getLogger(AdminResource.class);

	private MessageExchangeService subscriptionService = new MessageExchangeService();

	@Context
	private AtmosphereResource r;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Suspend(listeners = { WebSocketEventListenerAdapterImpl.class})
	public Response subscribe() {
		return subscriptionService.doGET(r);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void broadcast(
			@HeaderParam(MessageSubscribeServiceConstants.HT_AMPS_BROADCAST_NAMESPACE) String namespace,
			Message msg) {
		subscriptionService.doBroadcast(namespace, msg);
	}

}
