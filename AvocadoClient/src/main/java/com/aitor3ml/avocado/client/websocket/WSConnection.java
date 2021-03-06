package com.aitor3ml.avocado.client.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.aitor3ml.avocado.client.ClientConnectionImpl;
import com.aitor3ml.avocado.shared.networking.Message;
import com.aitor3ml.avocado.shared.networking.binary.BinaryCoder;
import com.aitor3ml.avocado.shared.networking.text.TextCoder;

@WebSocket
public class WSConnection {

	private final ClientConnectionImpl clientConnection;

	private Session session = null;

	private final ReentrantLock lock;

	public WSConnection(ClientConnectionImpl clientConnection) {
		this.clientConnection = clientConnection;
		lock = new ReentrantLock(true);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		clientConnection.closed(statusCode, reason);
		this.session = null;
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		session.getPolicy().setMaxBinaryMessageSize(1024 * 1024);
		clientConnection.connected();
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		lock.lock();
		try {
			String[] parts = msg.split(":", 2);
			switch (parts[0]) {
			case "data":
				clientConnection.message(TextCoder.decode(parts[1]));
				break;
			case "ping":
				session.getRemote().sendString("pong:" + parts[1], null);
				break;
			default:
				throw new RuntimeException("unkown message type:" + parts[0]);
			}
		} finally {
			lock.unlock();
		}

	}

	@OnWebSocketMessage
	public void onMessage(InputStream stream) throws ClassNotFoundException, IOException {
		lock.lock();
		try {
			Message msg = BinaryCoder.decode(stream, clientConnection.getAvocadoDeserializer());
			clientConnection.message(msg);
		} finally {
			lock.unlock();
		}
	}

	@OnWebSocketError
	public void onError(Throwable t) {
		clientConnection.error(t);
	}

	public void send(Message msg) throws IOException {
		ByteBuffer bb = BinaryCoder.encode(msg);
		try {
			session.getRemote().sendBytes(bb, null);
		} catch (WebSocketException e) {
			throw new IOException(e);
		}
	}

}
