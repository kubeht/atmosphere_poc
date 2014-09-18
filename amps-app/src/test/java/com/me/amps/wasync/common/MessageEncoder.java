package com.me.amps.wasync.common;

import java.io.IOException;

import org.atmosphere.wasync.Encoder;
import org.codehaus.jackson.map.ObjectMapper;

import com.me.amps.domain.dto.Message;

public class MessageEncoder implements Encoder<Message, String> {

    private final static ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(Message data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
