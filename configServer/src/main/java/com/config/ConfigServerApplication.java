package com.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication(exclude= { RabbitAutoConfiguration.class })
@EnableConfigServer
public class ConfigServerApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}

}
