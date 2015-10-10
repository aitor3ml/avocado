package com.aitor3ml.avocado.server.networking.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.aitor3ml.avocado.server.networking.NetworkingConnection;
import com.aitor3ml.avocado.server.networking.NetworkingConnectionListener;
import com.aitor3ml.avocado.server.networking.NetworkingManager;
import com.aitor3ml.avocado.server.tasks.Task;

@WebSocket
public class WSEndpoint implements NetworkingConnection {

	private final NetworkingManager networkingManager;

	private final long id;

	private Session session = null;

	private NetworkingConnectionListener listener;

	private long last = 0;

	public WSEndpoint(NetworkingManager networkingManager) {
		this.networkingManager = networkingManager;
		this.id = networkingManager.register(this);
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		this.session = session;
		schedule(new WSEventHandler(nextStart()) {
			@Override
			public void run() {
				listener = networkingManager.connected(WSEndpoint.this);
				if (listener != null)
					listener.connected();
			}
		});

	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		if (listener == null)
			return;
		schedule(new WSEventHandler(nextStart()) {
			@Override
			public void run() {
				if (listener != null)
					listener.message(msg);
			}
		});
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		schedule(new WSEventHandler(nextStart()) {
			@Override
			public void run() {
				if (listener != null)
					listener.closed(statusCode);
				networkingManager.unregister(WSEndpoint.this);
			}
		});
	}

	@Override
	public void send(String text) {
		session.getRemote().sendString(text, null);
	}

	@Override
	public void close() {
		schedule(new WSEventHandler(nextStart()) {
			@Override
			public void run() {
				session.close();
			}
		});
	}

	public long getId() {
		return id;
	}

	private long nextStart() {
		last = Math.max(last + 1, System.currentTimeMillis());
		return last;
	}

	private void schedule(WSEventHandler task) {
		networkingManager.schedule(task);
	}

	private static abstract class WSEventHandler extends Task {

		public WSEventHandler(long start) {
			super(start);
		}

	}
}
