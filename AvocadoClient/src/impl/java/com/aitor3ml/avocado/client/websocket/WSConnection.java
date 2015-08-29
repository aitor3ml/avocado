package com.aitor3ml.avocado.client.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.aitor3ml.avocado.client.ClientConnectionImpl;

@WebSocket
public class WSConnection {

	private final ClientConnectionImpl clientConnection;

	private Session session = null;

	public WSConnection(ClientConnectionImpl clientConnection) {
		this.clientConnection = clientConnection;
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		clientConnection.closed(statusCode, reason);
		this.session = null;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		clientConnection.connected();
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		clientConnection.message(msg);
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		t.printStackTrace();
	}

	public void send(String msg) {
		session.getRemote().sendString(msg, null);
	}

}
