package com.hightail.amps.plugin.hazelcast;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereConfig.ShutdownHook;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.util.AbstractBroadcasterProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
/**
 * 
 * modified from org.atmosphere.plugin.hazelcast.HazelcastBroadcaster 2.2.0
 * @author ian.li
 *
 */
public class HazelcastBroadcaster extends AbstractBroadcasterProxy {
    private static final Logger logger = LoggerFactory.getLogger(org.atmosphere.plugin.hazelcast.HazelcastBroadcaster.class);
    private ITopic<com.me.amps.domain.dto.Message> topic;
    private final AtomicBoolean isClosed = new AtomicBoolean();
    private String messageListenerRegistrationId;

    private final static HazelcastInstance HAZELCAST_INSTANCE;
	static {
		SerializerConfig sc = new SerializerConfig().setImplementation(
				new JsonSerializer()).setTypeClass(com.me.amps.domain.dto.Message.class);
		Config config = new Config();
		config.getSerializationConfig().addSerializerConfig(sc);
		HAZELCAST_INSTANCE = Hazelcast.newHazelcastInstance(config);
	}

    public Broadcaster initialize(String id, AtmosphereConfig config) {
        return super.initialize(id, URI.create("http://localhost:6379"), config);
    }

    public Broadcaster initialize(String id, URI uri, AtmosphereConfig config) {
        return super.initialize(id, uri, config);
    }

    public void setUp() {
        topic = HAZELCAST_INSTANCE.<com.me.amps.domain.dto.Message>getTopic(getID());
        config.shutdownHook(new ShutdownHook() {
            @Override
            public void shutdown() {
                HAZELCAST_INSTANCE.shutdown();
                isClosed.set(true);
            }
        });
    }
    
    private synchronized void addMessageListener() {
      if (getAtmosphereResources().size() > 0 && messageListenerRegistrationId == null) {
        messageListenerRegistrationId = topic.addMessageListener(new MessageListener<com.me.amps.domain.dto.Message>() {
          @Override
          public void onMessage(Message<com.me.amps.domain.dto.Message> message) {
              broadcastReceivedMessage(message.getMessageObject());
          }
        });
        
        logger.info("Added message listener to topic");
      }
    }
    
    private synchronized void removeMessageListener() {
      if (getAtmosphereResources().size() == 0 && messageListenerRegistrationId != null) {
        getTopic().removeMessageListener(messageListenerRegistrationId);
        messageListenerRegistrationId = null;
        
        logger.info("Removed message listener from topic");
      }   
    }
    
    @Override
    public Broadcaster addAtmosphereResource(AtmosphereResource resource) {
      Broadcaster result = super.addAtmosphereResource(resource);
      addMessageListener();
      return result;
    }

    @Override
    public Broadcaster removeAtmosphereResource(AtmosphereResource resource) {
      Broadcaster result = super.removeAtmosphereResource(resource);
      removeMessageListener();
      return result;
    }

    @Override
    public synchronized void setID(String id) {
        super.setID(id);
        setUp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (!isClosed.get()) {
            topic.destroy();
            topic = null;
        }
        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void incomingBroadcast() {
        logger.info("Subscribing to: {}", getID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outgoingBroadcast(Object message) {
        topic.publish((com.me.amps.domain.dto.Message)message);
    }

    /**
     * Get the Hazelcast topic
     * @return topic
     */
    protected ITopic getTopic() {
        return topic;
    }

}
