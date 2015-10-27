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
		schedule(new WSEventConnect());
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		lock.lock();
		try {
			schedule(new WSEventHandler() {
				@Override
				public void run() {
					if (listener != null)
						listener.message(msg);
				}

				@Override
				public String toString() {
					return super.toString() + ":" + msg;
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
			schedule(new WSEventMessage(msg));
		} finally {
			lock.unlock();
		}
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		schedule(new WSEventClose(statusCode, reason));
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
		session.close();
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

	private abstract class WSEventHandler extends Task {
		public WSEventHandler() {
			super(nextStart());
		}
	}

	private class WSEventConnect extends WSEventHandler {

		public WSEventConnect() {
			super();
		}

		@Override
		public void run() {
			listener = networkingManager.connected(WSEndpoint.this);
			if (listener != null)
				listener.connected();
		}

		@Override
		public String toString() {
			return "WSEventConnect";
		}
	}

	private class WSEventMessage extends WSEventHandler {

		private final Message msg;

		public WSEventMessage(Message msg) {
			super();
			this.msg = msg;
		}

		@Override
		public void run() {
			if (listener != null)
				listener.message(msg);
		}

		@Override
		public String toString() {
			return "WSEventMessage [msg=" + msg + "]";
		}
	}

	private class WSEventClose extends WSEventHandler {

		private final int statusCode;
		private final String reason;

		public WSEventClose(int statusCode, String reason) {
			super();
			this.statusCode = statusCode;
			this.reason = reason;
		}

		@Override
		public void run() {
			if (listener != null)
				listener.closed(statusCode);
			networkingManager.unregister(WSEndpoint.this);
		}

		@Override
		public String toString() {
			return "WSEventClose [statusCode=" + statusCode + ", reason=" + reason + "]";
		}
	}

}
