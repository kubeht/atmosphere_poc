package com.me.amps.wasync.common;

import org.atmosphere.wasync.Event;
import org.atmosphere.wasync.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum SocketEventHandler implements Function<String> {
    OPEN(Event.OPEN),
    CLOSE(Event.CLOSE),
    MESSAGE(Event.MESSAGE),
    REOPENED(Event.REOPENED),
    ERROR(Event.ERROR),
    STATUS(Event.STATUS),
    HEADERS(Event.HEADERS),
    MESSAGE_BYTES(Event.MESSAGE_BYTES),
    TRANSPORT(Event.TRANSPORT);

	private final static Logger logger = LoggerFactory.getLogger(SocketEventHandler.class);
	private final Event event;
	//private final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<String>(1024);
	
	public Event getEvent() {
		return event;
	}

	private SocketEventHandler(Event event) {
		this.event = event;
	}

	@Override
	public void on(String t) {
		logger.info("[{}, {}] <=======", event, t);
		//messageQueue.add(t);
	}

	public void clear() {
		//messageQueue.clear();
	}
	
	public String poll(long timeout) throws InterruptedException {
		return null;
		//messageQueue.poll(timeout, TimeUnit.SECONDS);
	} 
}
