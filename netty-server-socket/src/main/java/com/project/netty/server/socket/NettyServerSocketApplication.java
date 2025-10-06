package com.project.netty.server.socket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NettyServerSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyServerSocketApplication.class, args);
    }

}
