package com.ibm.utilities;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeoutException;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.ibm.constants.TestConstants;

public class StompUtil {
	// is never user, but looks cool :)

	private WebSocketStompClient stompClient;
	private BlockingQueue<String> blockingQueue;

	public StompUtil() {
		stompClient = new WebSocketStompClient(
				new SockJsClient(Arrays.asList(new WebSocketTransport(new StandardWebSocketClient()))));

		blockingQueue = new LinkedBlockingDeque<>();
	}

	public String interactWithServer(String subscription, String destination, String message)
			throws InterruptedException, ExecutionException, TimeoutException {
		StompSession session = stompClient.connect(TestConstants.WEBSOCKET_URI, new StompSessionHandlerAdapter() {
		}).get(1, SECONDS);
		session.subscribe(subscription, new DefaultStompFrameHandler());

		session.send(destination, message.getBytes());

		return blockingQueue.poll(1, SECONDS);
	}

	private class DefaultStompFrameHandler implements StompFrameHandler {
		@Override
		public Type getPayloadType(StompHeaders stompHeaders) {
			return byte[].class;
		}

		@Override
		public void handleFrame(StompHeaders stompHeaders, Object o) {
			blockingQueue.offer(new String((byte[]) o));
		}
	}

}
