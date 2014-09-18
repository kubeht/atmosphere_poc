package com.me.amps.wasync.common;

import java.io.IOException;

import org.atmosphere.wasync.Decoder;
import org.atmosphere.wasync.Event;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.me.amps.apps.listener.WebSocketEventListenerAdapterImpl;
import com.me.amps.domain.dto.Message;

public class MessageDecoder implements Decoder<String, Message> {
    private final static ObjectMapper mapper = new ObjectMapper();
	private final static Logger logger = LoggerFactory
			.getLogger(MessageDecoder.class);

    @Override
    public Message decode(Event type, String data) {
        /*
    	data = data.trim();
        if (type.equals(Event.ERROR)) {
            return new Message(data);
        } else 
        */
        if (type.equals(Event.MESSAGE)) {
            try {
                return mapper.readValue(data, Message.class);
            } catch (IOException e) {
            	//return new Message.Builder().message(data).build();
            	logger.warn("not able to decode data {}",  data);
            	return null;
            }
        } else {
            return null;
        }
    }

}
