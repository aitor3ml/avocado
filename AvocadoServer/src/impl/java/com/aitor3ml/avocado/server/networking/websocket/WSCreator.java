package com.aitor3ml.avocado.server.networking.websocket;

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import com.aitor3ml.avocado.server.networking.NetworkingManager;

public class WSCreator implements WebSocketCreator {

	private final NetworkingManager networkingManager;

	public WSCreator(NetworkingManager networkingManager) {
		this.networkingManager = networkingManager;
	}

	@Override
	public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
		return new WSEndpoint(networkingManager);
	}

}