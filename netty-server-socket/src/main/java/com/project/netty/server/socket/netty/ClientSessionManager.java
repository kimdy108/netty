package com.project.netty.server.socket.netty;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class ClientSessionManager {
    private final ConcurrentMap<String, Channel> clientMap = new ConcurrentHashMap<>();

    public void addClient(String clientId, Channel channel) {
        clientMap.put(clientId, channel);
    }

    public void removeClient(String clientId) {
        clientMap.remove(clientId);
    }

    public Channel getChannel(String clientId) {
        return clientMap.get(clientId);
    }

    public Collection<String> getAllClientIds() {
        return clientMap.keySet();
    }

    public void broadcast(String message) {
        clientMap.forEach((id, ch) -> {
            if (ch.isActive()) {
                ch.writeAndFlush(message);
            }
        });
    }
}
