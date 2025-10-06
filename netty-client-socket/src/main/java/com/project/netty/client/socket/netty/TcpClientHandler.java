package com.project.netty.client.socket.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class TcpClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.println("서버 응답 수신: " + msg);

        if (msg.startsWith("ID 등록 완료: ")) {
            String clientID = msg.substring(10).trim();
            ctx.channel().writeAndFlush("안녕하세요. 이것은 " + clientID + "입니다.");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
