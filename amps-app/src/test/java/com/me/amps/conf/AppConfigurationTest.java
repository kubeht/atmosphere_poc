/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.amps.conf;

import com.me.amps.web.bootstrap.AppConfiguration;
import com.me.amps.web.bootstrap.ServiceFactory;

import javax.inject.Inject;
import static junit.framework.Assert.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 *
 * @author ian.li
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {AppConfiguration.class})
public class AppConfigurationTest extends AbstractTestNGSpringContextTests {

    @Value("${port}")
    private int port;

    @Inject
    private Environment environment;
    
    
    @Inject
    private ServiceFactory pushingService;
            
    @Test
    public void testEnvironment() {
        int myport = environment.getProperty("port", int.class);
        assertEquals(8080, myport);
        
    }
    @Test
    public void testValueInjection() {
        assertEquals(8080, port);
    }
    
    @Test
    public void testPushingServiceInjection() {
        assertNotNull(pushingService);
        assertTrue(pushingService.isStarted());
    }

}
