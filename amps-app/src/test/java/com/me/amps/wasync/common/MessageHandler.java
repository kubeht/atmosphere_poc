package com.me.amps.wasync.common;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.atmosphere.wasync.Function;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.amps.domain.dto.Message;

public class MessageHandler implements Function<Message> {
    private final static ObjectMapper mapper = new ObjectMapper();
    private final static Logger logger = LoggerFactory
			.getLogger(MessageHandler.class);

	private final String eventName;
	private final String id;
	private BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<Message>(16);
	
	public MessageHandler(String eventName) {
		this(eventName, "default");
	}

	public MessageHandler(String eventName, String id) {
		this.eventName = eventName;
		this.id = id;
	}

	@Override
	public void on(Message t) {
		if (t == null) {
			logger.warn("ignored a null message");
			return;
		}
		String message = null;
        try {
        	message = mapper.writeValueAsString(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
			messageQueue.put(t);
		} catch (InterruptedException e) { }
		logger.info("{} : received [{}, {}]", id, eventName, message);
	}

	public void clear() {
		messageQueue.clear();
	}
	
	public Message poll(long timeout) throws InterruptedException {
		logger.info("{}: polling", id);
		Message result =  messageQueue.poll(timeout, TimeUnit.SECONDS);
		logger.info("{}: polled and {}", id, result == null ? "missed" : "got " + result);
		return result;
	} 

}
