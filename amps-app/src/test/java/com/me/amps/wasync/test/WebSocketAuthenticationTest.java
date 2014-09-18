/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.amps.wasync.test;

import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.atmosphere.wasync.ClientFactory;
import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.atmosphere.wasync.Future;
import org.atmosphere.wasync.OptionsBuilder;
import org.atmosphere.wasync.Request;
import org.atmosphere.wasync.RequestBuilder;
import org.atmosphere.wasync.Socket;
import org.atmosphere.wasync.impl.AtmosphereClient;
import org.atmosphere.wasync.impl.AtmosphereRequest.AtmosphereRequestBuilder;
import org.atmosphere.wasync.impl.DefaultOptions;
import org.atmosphere.wasync.impl.DefaultOptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.me.amps.domain.MessageSubscribeServiceConstants;
import com.me.amps.domain.dto.Message;
import com.me.amps.wasync.common.ConnectionAssert;
import com.me.amps.wasync.common.MessageDecoder;
import com.me.amps.wasync.common.MessageEncoder;
import com.me.amps.wasync.common.MessageHandler;
import com.me.amps.wasync.common.ServiceRequestContextBuilder;
import com.me.amps.wasync.common.SocketEventHandler;
import com.me.amps.web.bootstrap.AppConfiguration;
import com.ys.common.domain.device.DeviceInfo;

/**
 * @author ian.li
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfiguration.class })
public class WebSocketAuthenticationTest extends AbstractTestNGSpringContextTests {

	private final static Logger logger = LoggerFactory
			.getLogger(WebSocketAuthenticationTest.class);
	
	private final static String URI = "/message";
	// default connection options
	private final boolean TRACK_MESSAGE_LENGTH = false;
	private final static boolean RECONNECT = true;
	// use 2 to avoid excessive # of retry
	private final static int RECONNECT_PAUSE_SECONDS = 2;
	private final static Request.TRANSPORT TRANSPORT = Request.TRANSPORT.WEBSOCKET;
	
	// polling period
	private final static int TIMEOUT_SECONDS = 4;
	
	@Value("${port}")
	private int port;

	private UUID uuid = UUID.randomUUID();
	private String url;

	private AtmosphereClient client = ClientFactory.getDefault().newClient(
			AtmosphereClient.class);
	private Socket socket;
	private RequestBuilder<AtmosphereRequestBuilder> requestBuilder = null;
	private MessageHandler messageHandler = new MessageHandler(
			Event.MESSAGE.name());

	public WebSocketAuthenticationTest() {

	}

	@BeforeClass
	public void setup() {
		url = "http://127.0.0.1:" + port + URI;
	}

	@BeforeMethod
	public void setupTest() {
		OptionsBuilder<DefaultOptions, DefaultOptionsBuilder> optBuilder = client
				.newOptionsBuilder()
				.reconnect(RECONNECT)
				.pauseBeforeReconnectInSeconds(RECONNECT_PAUSE_SECONDS);

		socket = client.create(optBuilder.build())
				.on(Event.MESSAGE, messageHandler)
				.on(new Function<Throwable>() {
					@Override
					public void on(Throwable t) {
						logger.error("onThrowable", t);
					}
				});

		// register handlers for all the socket events
		for (SocketEventHandler seh : SocketEventHandler.values()) {
			socket.on(seh.getEvent(), seh);
		}

		requestBuilder = client.newRequestBuilder().method(Request.METHOD.GET)
				.transport(TRANSPORT)
				.uri(url)
				.trackMessageLength(TRACK_MESSAGE_LENGTH)
				.encoder(new MessageEncoder()).decoder(new MessageDecoder())
				.header("Accept", MediaType.APPLICATION_JSON)
				.header("Content-type", MediaType.APPLICATION_JSON);

	}

	@AfterMethod
	public void teardownTest() {
		socket.close();
		messageHandler.clear();

		for (SocketEventHandler seh : SocketEventHandler.values()) {
			seh.clear();
		}

	}

	@Test
	public void testWebSocketAuthenticateSuccess() throws Exception {
		Long userId = 234L;
		requestBuilder
				.header(
				MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT,
				new ServiceRequestContextBuilder().user(userId)
						.device(DeviceInfo.Type.DESKTOP)
						.authToken(uuid.toString()).build());
		Request request = requestBuilder.build();
		
		socket.open(request);
		ConnectionAssert.assertSuccessfulAuthentication(messageHandler, TIMEOUT_SECONDS);
		
		Future future = null;
		future = socket.fire(new Message.Builder().message("message1").build());
		Assert.assertTrue(future.isDone());
		Assert.assertNotNull(messageHandler.poll(TIMEOUT_SECONDS),
				"Did not receive message in time ");

		future = socket.fire(new Message.Builder().message("message2").build());
		Assert.assertTrue(future.isDone());
		Assert.assertNotNull(messageHandler.poll(TIMEOUT_SECONDS),
				"Did not receive message in time ");

	}

	@Test
	public void testWebSocketContextError() throws Exception {
		Long userId = 1234L;
		requestBuilder
				.header(
				MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT,
				new ServiceRequestContextBuilder().user(userId)
						.device(DeviceInfo.Type.DESKTOP).build())		;
		Request request = requestBuilder.build();
		
		socket.open(request);
		ConnectionAssert.assertContextError(messageHandler, TIMEOUT_SECONDS);

	}

	@Test
	public void testWebSocketAuthenticateFailure() throws Exception {
		Long userId = 1234L;
		requestBuilder
				.header(
				MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT,
				new ServiceRequestContextBuilder().user(userId)
						.device(DeviceInfo.Type.DESKTOP)
						.authToken(MessageSubscribeServiceConstants.HT_SVC_REQUEST_DOOMED_TOKEN).build());
		Request request = requestBuilder.build();
		
		socket.open(request);
		ConnectionAssert.assertFailedAuthentication(messageHandler, TIMEOUT_SECONDS);

	}

}
