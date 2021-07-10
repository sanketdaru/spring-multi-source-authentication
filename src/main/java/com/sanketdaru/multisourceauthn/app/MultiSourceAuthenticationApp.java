package com.sanketdaru.multisourceauthn.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = { "com.sanketdaru.multisourceauthn" }, exclude = {
		SecurityAutoConfiguration.class })
public class MultiSourceAuthenticationApp {

	public static void main(String[] args) {
		SpringApplication.run(MultiSourceAuthenticationApp.class, args);
	}
}
