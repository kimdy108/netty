package com.project.netty.server.socket.config;

import com.project.netty.server.socket.netty.NettyServer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationStartupTask implements ApplicationListener<ApplicationReadyEvent> {
    private final NettyServer nettyServer;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        nettyServer.start();
    }
}
