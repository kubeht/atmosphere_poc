/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.amps.web.bootstrap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.BroadcasterLifeCyclePolicy.ATMOSPHERE_RESOURCE_POLICY;
import org.atmosphere.nettosphere.Config;
import org.atmosphere.nettosphere.Nettosphere;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.me.amps.apps.MessageResource;


/**
 *
 * @author ian.li
 */
@Service
public class ServiceFactory {
	private final static Logger logger = LoggerFactory
			.getLogger(ServiceFactory.class);
	
    @Value("${port}")
    private int port;

    @Value("${com.hightail.amps.plugin.hazelcast.HazelcastBroadcaster.enableHazelcastIntegration:false}")
	private boolean enableHazelcastIntegration;
    
	@Value("${org.atmosphere.cpr.Broadcaster.broadcasterClass:}")
	private String broadcasterClass;
	
    private Nettosphere nettosphere;

    @PostConstruct
    public void initialize() {
        Config.Builder b = new Config.Builder();
        b.resource(MessageResource.class)
                .resource("./webapps")
                .port(port)
                .host("127.0.0.1")
                .initParam("com.sun.jersey.api.json.POJOMappingFeature","true")
                .initParam(ApplicationConfig.BROADCASTER_SHARABLE_THREAD_POOLS, "true")
                .initParam(ApplicationConfig.BROADCASTER_MESSAGE_PROCESSING_THREADPOOL_MAXSIZE, "10")
                .initParam(ApplicationConfig.BROADCASTER_ASYNC_WRITE_THREADPOOL_MAXSIZE, "10")
                // setup a 30 second for HeartbeatInterceptor
                .initParam(ApplicationConfig.HEARTBEAT_INTERVAL_IN_SECONDS, "30")
                // setup a 60 second for max idle time for IdleResourceInterceptor
                .initParam(ApplicationConfig.MAX_INACTIVE, "60000")
                // broadcaster policy
                .initParam(ApplicationConfig.BROADCASTER_LIFECYCLE_POLICY, ATMOSPHERE_RESOURCE_POLICY.EMPTY.name());
        
        logger.info("enableHazelcastIntegration={}", enableHazelcastIntegration);
		if (enableHazelcastIntegration) {
			b.initParam(ApplicationConfig.BROADCASTER_CLASS, broadcasterClass);
		}

                b.build();
        nettosphere = new Nettosphere.Builder().config(b.build()).build();
        nettosphere.start();
    }

    @PreDestroy
    public void cleanUp() throws Exception {
        nettosphere.stop();
    }
    
    public boolean isStarted() {
        return nettosphere.isStarted();
    }
}
