package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.io.InputStream;

@SpringBootApplication
@EnableWebSecurity
public class DemoApplication {

	public static void main(String[] args){SpringApplication.run(DemoApplication.class, args);}




}
