package com.me.amps.apps;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.AtmosphereResource;

import com.me.amps.apps.listener.WebSocketEventListenerAdapterImpl;
import com.me.amps.core.MessageExchangeService;
import com.me.amps.domain.dto.Message;

@Path("/message")
public class MessageResource {
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
	@Broadcast(writeEntity = false)
	@Produces(MediaType.APPLICATION_JSON)
	public Response publish(Message message) {
		return subscriptionService.doPOST(r, message) ;
	}

}
