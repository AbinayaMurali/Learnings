package com.concur.springtutorial.interservicecommunication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.ulisesbocchio.spring.boot.security.saml.annotation.EnableSAMLSSO;

@SpringBootApplication
public class OktaIntegrationApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(OktaIntegrationApplication.class, args);
	}
}
