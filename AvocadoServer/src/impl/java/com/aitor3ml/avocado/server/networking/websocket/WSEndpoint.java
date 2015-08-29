package com.aitor3ml.avocado.server.networking.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.aitor3ml.avocado.server.networking.NetworkingConnection;
import com.aitor3ml.avocado.server.networking.NetworkingConnectionListener;
import com.aitor3ml.avocado.server.networking.NetworkingManager;

@WebSocket
public class WSEndpoint implements NetworkingConnection {

	private final NetworkingManager networkingManager;

	private final long id;

	private Session session = null;

	private NetworkingConnectionListener listener;

	public WSEndpoint(NetworkingManager networkingManager) {
		this.networkingManager = networkingManager;
		this.id = networkingManager.register(this);
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		listener = networkingManager.connected(this);
		if (listener != null)
			listener.connected();
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		if (listener != null)
			listener.message(msg);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		if (listener != null)
			listener.closed(statusCode);
		networkingManager.unregister(this);
	}

	@Override
	public void send(String text) {
		session.getRemote().sendString(text, null);
	}

	@Override
	public void close() {
		session.close();
	}

	public long getId() {
		return id;
	}

}
