package com.project.netty.client.socket.config;

import com.project.netty.client.socket.netty.NettyClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupTask implements ApplicationListener<ApplicationReadyEvent> {
    private final NettyClient nettyClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        nettyClient.connect();
    }
}
