package com.me.amps.web.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class MessageServiceBootstraper {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceBootstraper.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = 
				   new AnnotationConfigApplicationContext(AppConfiguration.class);
		ctx.start();
		
	}
	
}
