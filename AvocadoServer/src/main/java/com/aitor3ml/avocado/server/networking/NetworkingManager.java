package com.aitor3ml.avocado.server.networking;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.aitor3ml.avocado.server.networking.websocket.WSHandler;
import com.aitor3ml.avocado.server.tasks.Task;
import com.aitor3ml.avocado.server.tasks.TaskManager;
import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.aitor3ml.avocado.shared.networking.AvocadoKeyStore;

public class NetworkingManager {

	private final Server server;

	private final TaskManager taskManager;

	private final Map<Long, NetworkingConnection> connections;

	private final NetworkingListener listener;

	private final AvocadoDeserializer avocadoDeserializer;

	private long nextId = 1L;

	public NetworkingManager(TaskManager taskManager, Integer wsPort, Integer wssPort, NetworkingListener listener,
			AvocadoDeserializer avocadoDeserializer) throws ServletException {
		this.taskManager = taskManager;
		this.listener = listener;
		this.avocadoDeserializer = avocadoDeserializer;

		server = new Server();
		server.setHandler(new WSHandler(this));

		if (wsPort != null) {
			ServerConnector connector = new ServerConnector(server);
			connector.setPort(wsPort);
			server.addConnector(connector);
		}

		if (wssPort != null) {
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStoreResource(Resource.newResource(AvocadoKeyStore.getUrl()));
			sslContextFactory.setKeyStorePassword(AvocadoKeyStore.getStorePassword());
			sslContextFactory.setKeyManagerPassword(AvocadoKeyStore.getKeyPassword());
			ServerConnector sslConnector = new ServerConnector(server, sslContextFactory);
			sslConnector.setPort(wssPort);
			server.addConnector(sslConnector);
		}

		connections = new ConcurrentHashMap<Long, NetworkingConnection>();
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

	public boolean schedule(Task task) {
		if (task == null)
			return false;
		taskManager.schedule(task);
		return true;
	}

	public AvocadoDeserializer getAvocadoDeserializer() {
		return avocadoDeserializer;
	}

}
