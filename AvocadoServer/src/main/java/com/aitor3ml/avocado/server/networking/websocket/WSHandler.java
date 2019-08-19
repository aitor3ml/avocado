package com.aitor3ml.avocado.server.networking.websocket;

import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.aitor3ml.avocado.server.networking.NetworkingManager;

public class WSHandler extends WebSocketHandler {

	private final WSCreator wsCreator;

	public WSHandler(NetworkingManager networkingManager) {
		super();
		wsCreator = new WSCreator(networkingManager);
	}

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.setCreator(wsCreator);
	}

}
