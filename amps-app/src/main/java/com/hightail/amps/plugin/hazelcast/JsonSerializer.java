package com.hightail.amps.plugin.hazelcast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import com.me.amps.domain.dto.Message;

public class JsonSerializer implements StreamSerializer<Message> {
    private final static ObjectMapper mapper = new ObjectMapper();

	@Override
	public int getTypeId() {
		return 1;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void write(ObjectDataOutput out, Message object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        mapper.writeValue(bos, object);
        out.write(bos.toByteArray());
    }

	@Override
	public Message read(ObjectDataInput in) throws IOException {
        final InputStream inputStream = (InputStream) in;
        return mapper.readValue(inputStream, Message.class);
	}
	
}