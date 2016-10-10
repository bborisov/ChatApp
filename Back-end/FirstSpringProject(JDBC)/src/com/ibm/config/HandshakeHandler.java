package com.ibm.config;

import java.security.Principal;
import java.util.Map;

import org.apache.catalina.tribes.util.UUIDGenerator;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class HandshakeHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		return new UserPrincipal(UUIDGenerator.randomUUID(true).toString());
	}
}
