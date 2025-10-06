package com.project.netty.client.socket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NettyClient {
    @Value("${netty.host}")
    private String host;

    @Value("${netty.port}")
    private int port;

    @Value("${netty.clientID}")
    private String clientID;

    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService messageSenderExecutor = Executors.newSingleThreadScheduledExecutor();

    private EventLoopGroup group;
    private Channel channel;

    public void connect() {
        group = new NioEventLoopGroup();

        attemptConnection(0);
    }

    private void attemptConnection(long delaySeconds) {
        reconnectExecutor.schedule(() -> {
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new TcpClientInitializer())
                        .option(ChannelOption.SO_KEEPALIVE, true);

                ChannelFuture f = b.connect(host, port);
                f.addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        channel = future.channel();
                        System.out.println("서버 연결 성공: " + host + ":" + port);

                        // ID 전송
                        channel.writeAndFlush("ID:" + clientID);

                        // 주기적인 메시지 전송 시작
                        startMessageSending();

                        // 연결 종료 시 재연결 시도
                        channel.closeFuture().addListener(closeFuture -> {
                            System.out.println("서버와 연결 끊김. 재연결 시도...");
                            stopMessageSending();
                            attemptConnection(5); // 5초 후 재연결
                        });
                    } else {
                        System.out.println("서버 연결 실패. 재시도 대기 중...");
                        attemptConnection(5); // 5초 후 재시도
                    }
                });
            } catch (Exception e) {
                System.out.println("연결 중 예외 발생: " + e.getMessage());
                attemptConnection(5);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    private void startMessageSending() {
        messageSenderExecutor.scheduleAtFixedRate(() -> {
            if (channel != null && channel.isActive()) {
                String message = "Hello from " + clientID + " at " + System.currentTimeMillis();
                channel.writeAndFlush(message);
            }
        }, 5, 5, TimeUnit.SECONDS); // 5초 간격
    }

    private void stopMessageSending() {
        messageSenderExecutor.shutdownNow();
    }
}
