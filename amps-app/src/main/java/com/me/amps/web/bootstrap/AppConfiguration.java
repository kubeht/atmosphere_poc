/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.me.amps.web.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * 
 * @author ian.li
 */
@Configuration
@PropertySource(value = { "classpath:default.properties",
		"classpath:environment.properties" }, ignoreResourceNotFound = true)
@ComponentScan(basePackages = { 
		"com.me.amps.apps"
		, "com.me.amps.web"
		})
public class AppConfiguration {
	private static final Logger logger = LoggerFactory
			.getLogger(AppConfiguration.class);

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
