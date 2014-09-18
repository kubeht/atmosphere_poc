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
import com.me.amps.web.bootstrap.AppConfiguration;
import com.ys.common.domain.device.DeviceInfo;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfiguration.class })
public class PublishSubscribeTest extends AbstractTestNGSpringContextTests {
	private final static Logger logger = LoggerFactory
			.getLogger(PublishSubscribeTest.class);

	private final static String URI = "/message";
	// default connection options
	private final boolean TRACK_MESSAGE_LENGTH = false;
	private final static boolean RECONNECT = true;
	private final static int RECONNECT_PAUSE_SECONDS = 0;
	private final static Request.TRANSPORT TRANSPORT = Request.TRANSPORT.WEBSOCKET;

	// polling period
	private final static int TIMEOUT_SECONDS = 2;

	@Value("${port}")
	private int port;

	private UUID uuid1 = UUID.randomUUID();
	private UUID uuid2 = UUID.randomUUID();
	private String url1;
	private String url2;
	private String urlAdmin;

	private AtmosphereClient client1 = ClientFactory.getDefault().newClient(
			AtmosphereClient.class);
	private AtmosphereClient client2 = ClientFactory.getDefault().newClient(
			AtmosphereClient.class);

	private AtmosphereClient clientAdmin = ClientFactory.getDefault()
			.newClient(AtmosphereClient.class);

	private Socket socket1;
	private Socket socket2;
	private Socket socketAdmin;

	private RequestBuilder<AtmosphereRequestBuilder> requestBuilder1 = null;
	private RequestBuilder<AtmosphereRequestBuilder> requestBuilder2 = null;
	private RequestBuilder<AtmosphereRequestBuilder> requestBuilderAdmin = null;

	private MessageHandler messageHandler1 = new MessageHandler(
			Event.MESSAGE.name(), "DESKTOP_RECEIVER");

	private MessageHandler messageHandler2 = new MessageHandler(
			Event.MESSAGE.name(), "MOBILE_RECEIVER");

	private MessageHandler messageHandlerAdmin = new MessageHandler(
			Event.MESSAGE.name(), "PHI_RECEIVER");

	@BeforeClass
	public void setup() {
		url1 = "http://127.0.0.1:" + port + URI;
		url2 = "http://127.0.0.1:" + port + URI;
		urlAdmin = "http://127.0.0.1:" + port + "/admin";
	}

	@BeforeMethod
	public void setupTest() {
		OptionsBuilder<DefaultOptions, DefaultOptionsBuilder> optBuilder1 = client1
				.newOptionsBuilder().reconnect(RECONNECT)
				.pauseBeforeReconnectInSeconds(RECONNECT_PAUSE_SECONDS);

		OptionsBuilder<DefaultOptions, DefaultOptionsBuilder> optBuilder2 = client2
				.newOptionsBuilder().reconnect(RECONNECT)
				.pauseBeforeReconnectInSeconds(RECONNECT_PAUSE_SECONDS);

		OptionsBuilder<DefaultOptions, DefaultOptionsBuilder> optBuilderAdmin = clientAdmin
				.newOptionsBuilder().reconnect(RECONNECT)
				.pauseBeforeReconnectInSeconds(RECONNECT_PAUSE_SECONDS);

		socket1 = client1.create(optBuilder1.build())
				.on(Event.MESSAGE, messageHandler1)
				.on(new Function<Throwable>() {
					@Override
					public void on(Throwable t) {
						logger.error("onThrowable", t);
					}
				});

		socket2 = client2.create(optBuilder2.build())
				.on(Event.MESSAGE, messageHandler2)
				.on(new Function<Throwable>() {
					@Override
					public void on(Throwable t) {
						logger.error("onThrowable", t);
					}
				});

		socketAdmin = clientAdmin.create(optBuilderAdmin.build())
				.on(Event.MESSAGE, messageHandlerAdmin)
				.on(new Function<Throwable>() {
					@Override
					public void on(Throwable t) {
						logger.error("onThrowable", t);
					}
				});

		requestBuilder1 = client1.newRequestBuilder()
				.method(Request.METHOD.GET).transport(TRANSPORT).uri(url1)
				.trackMessageLength(TRACK_MESSAGE_LENGTH)
				.encoder(new MessageEncoder()).decoder(new MessageDecoder())
				.header("Accept", MediaType.APPLICATION_JSON)
				.header("Content-type", MediaType.APPLICATION_JSON);

		requestBuilder2 = client2.newRequestBuilder()
				.method(Request.METHOD.GET).transport(TRANSPORT).uri(url2)
				.trackMessageLength(TRACK_MESSAGE_LENGTH)
				.encoder(new MessageEncoder()).decoder(new MessageDecoder())
				.header("Accept", MediaType.APPLICATION_JSON)
				.header("Content-type", MediaType.APPLICATION_JSON);

		requestBuilderAdmin = clientAdmin.newRequestBuilder()
				.method(Request.METHOD.GET).transport(TRANSPORT).uri(urlAdmin)
				.trackMessageLength(TRACK_MESSAGE_LENGTH)
				.encoder(new MessageEncoder()).decoder(new MessageDecoder())
				.header("Accept", MediaType.APPLICATION_JSON)
				.header("Content-type", MediaType.APPLICATION_JSON);

	}

	@AfterMethod
	public void teardownTest() {
		socket1.close();
		socket2.close();
		socketAdmin.close();
		messageHandler1.clear();
		messageHandler2.clear();
		messageHandlerAdmin.clear();
	}

	@Test
	public void testPubSub() throws Exception {
		Long userId = 123L;
		requestBuilder1.header(
				MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT,
				new ServiceRequestContextBuilder().user(userId)
						.device(DeviceInfo.Type.DESKTOP)
						.authToken(uuid1.toString()).build());
		Request request1 = requestBuilder1.build();
		socket1.open(request1);
		ConnectionAssert.assertSuccessfulAuthentication(messageHandler1, TIMEOUT_SECONDS);

		requestBuilder2.header(
				MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT,
				new ServiceRequestContextBuilder().user(userId)
						.device(DeviceInfo.Type.MOBILE)
						.authToken(uuid2.toString()).build());
		Request request2 = requestBuilder2.build();

		socket2.open(request2);
		ConnectionAssert.assertSuccessfulAuthentication(messageHandler2, TIMEOUT_SECONDS);

		requestBuilderAdmin
				.header(MessageSubscribeServiceConstants.HT_SVC_REQUEST_CONTEXT,
						new ServiceRequestContextBuilder().user(userId)
								.device(DeviceInfo.Type.PHI)
								.authToken(UUID.randomUUID().toString()).build())
				.header(MessageSubscribeServiceConstants.HT_AMPS_BROADCAST_NAMESPACE,
						String.format("/%s/%s/*", userId,
								DeviceInfo.Type.DESKTOP))
				;
		
		Request requestAdmin = requestBuilderAdmin.build();
		socketAdmin.open(requestAdmin);
		ConnectionAssert.assertSuccessfulAuthentication(messageHandlerAdmin, TIMEOUT_SECONDS);

		Future future1 = null;
		future1 = socket1.fire(new Message.Builder().message("message1").build());
		Assert.assertTrue(future1.isDone());
		Assert.assertNull(messageHandlerAdmin.poll(TIMEOUT_SECONDS),
				"Should not receive message all all");
		Assert.assertNotNull(messageHandler1.poll(TIMEOUT_SECONDS),
				"Did not receive message in time ");
		Assert.assertNull(messageHandler2.poll(TIMEOUT_SECONDS),
				"Should not receive message at all ");

		Future future2 = null;
		future2 = socket2.fire(new Message.Builder().message("message2").build());
		Assert.assertTrue(future2.isDone());
		Assert.assertNull(messageHandlerAdmin.poll(TIMEOUT_SECONDS),
				"Should not receive message all all");
		Assert.assertNotNull(messageHandler2.poll(TIMEOUT_SECONDS),
				"Did not receive message in time ");
		Assert.assertNull(messageHandler1.poll(TIMEOUT_SECONDS),
				"Should not receive message at all ");

		Future futureAdmin = null;
		futureAdmin = socketAdmin.fire(new Message.Builder().message("message3").build());
		Assert.assertTrue(futureAdmin.isDone());
		Assert.assertNull(messageHandlerAdmin.poll(TIMEOUT_SECONDS),
				"Should not receive message all all");
		Assert.assertNotNull(messageHandler1.poll(TIMEOUT_SECONDS),
				"Did not receive message in time");
		Assert.assertNull(messageHandler2.poll(TIMEOUT_SECONDS),
				"Should not receive message all all");

	}

}
