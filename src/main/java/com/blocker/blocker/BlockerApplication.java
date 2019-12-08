package com.blocker.blocker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BlockerApplication {

	@Value("${blocker.honeypot_port}")
	private int port;
	
	@Autowired
	private IPLogger ipDropper;

	@Autowired
	private IPAddressFormatValidator ipValidator;

	public static void main(String[] args) {
		SpringApplication.run(BlockerApplication.class, args);
	}

	@Bean
	public void listener() throws IOException {
		while (true) {
			ServerSocket sSock = new ServerSocket(port);
			Socket cSocket = sSock.accept();
            String ipAndPort[] = cSocket.getRemoteSocketAddress().toString().split(":");
            String ip = ipAndPort[0].replace("/", "");
			cSocket.close();
			sSock.close();
			if (ipValidator.validate(ip)) {
				ipDropper.log(ip);
			}
		}
	}
}
