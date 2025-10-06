package com.project.netty.server.socket.netty;

import io.netty.channel.*;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class TcpServerHandler extends SimpleChannelInboundHandler<String> {
    private final ClientSessionManager sessionManager;
    private final Map<ChannelId, String> tempClientIds = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("클라이언트 연결됨: " + ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        Channel channel = ctx.channel();

        if (msg.startsWith("ID:")) {
            String clientId = msg.substring(3).trim();
            sessionManager.addClient(clientId, channel);
            tempClientIds.put(channel.id(), clientId);
            System.out.println("클라이언트 등록됨: " + clientId);
            channel.writeAndFlush("ID 등록 완료: " + clientId);
            return;
        }

        String clientId = tempClientIds.get(channel.id());
        System.out.printf("[%s] 수신 메시지: %s\n", clientId, msg);

        // 응답 예시
        channel.writeAndFlush("서버 응답: [" + msg + "]");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ChannelId id = ctx.channel().id();
        String clientId = tempClientIds.remove(id);
        if (clientId != null) {
            sessionManager.removeClient(clientId);
            System.out.println("클라이언트 연결 종료: " + clientId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
