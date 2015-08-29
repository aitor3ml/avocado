package com.aitor3ml.avocado.server.networking;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;

import org.eclipse.jetty.server.Server;

import com.aitor3ml.avocado.server.networking.websocket.WSHandler;

public class NetworkingManager {

	private final Server server;

	private final Map<Long, NetworkingConnection> connections;

	private final NetworkingListener listener;

	private long nextId = 1L;

	public NetworkingManager(int port, NetworkingListener listener) throws ServletException {
		server = new Server(port);
		server.setHandler(new WSHandler(this));
		connections = new ConcurrentHashMap<Long, NetworkingConnection>();

		this.listener = listener;
	}

	public void start() throws Exception {
		server.start();
	}

	public void join() throws InterruptedException {
		server.join();
	}

	public void stop() throws Exception {
		server.stop();
	}

	public synchronized long register(NetworkingConnection connection) {
		connections.put(nextId, connection);
		return nextId++;
	}

	public synchronized void unregister(NetworkingConnection connection) {
		connections.remove(connection.getId());
	}

	public NetworkingConnectionListener connected(NetworkingConnection connection) {
		return listener.connected(connection);
	}

}
