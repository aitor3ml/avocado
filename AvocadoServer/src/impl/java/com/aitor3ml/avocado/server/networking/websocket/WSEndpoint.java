package com.aitor3ml.avocado.server.networking.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.aitor3ml.avocado.server.networking.NetworkingConnection;
import com.aitor3ml.avocado.server.networking.NetworkingConnectionListener;
import com.aitor3ml.avocado.server.networking.NetworkingManager;
import com.aitor3ml.avocado.server.tasks.Task;
import com.aitor3ml.avocado.shared.networking.Message;
import com.aitor3ml.avocado.shared.networking.binary.BinaryCoder;

@WebSocket
public class WSEndpoint implements NetworkingConnection {

	private final NetworkingManager networkingManager;

	private final long id;

	private final ReentrantLock lock;

	private Session session = null;

	private NetworkingConnectionListener listener;

	private long last = 0;

	public WSEndpoint(NetworkingManager networkingManager) {
		this.networkingManager = networkingManager;
		this.lock = new ReentrantLock(true);
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
		lock.lock();
		try {
			schedule(new WSEventHandler(nextStart()) {
				@Override
				public void run() {
					if (listener != null)
						listener.message(msg);
				}
			});
		} finally {
			lock.unlock();
		}
	}

	@OnWebSocketMessage
	public void onMessage(InputStream stream) throws IOException, ClassNotFoundException {
		lock.lock();
		try {
			Message msg = BinaryCoder.decode(stream, networkingManager.getAvocadoDeserializer());
			schedule(new WSEventHandler(nextStart()) {
				@Override
				public void run() {
					if (listener != null)
						listener.message(msg);
				}
			});
		} finally {
			lock.unlock();
		}
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
	public void send(Message message) throws IOException {
		ByteBuffer bb = BinaryCoder.encode(message);
		try {
			session.getRemote().sendBytes(bb, null);
		} catch (WebSocketException e) {
			throw new IOException(e);
		}
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
