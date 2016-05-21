package com.aitor3ml.avocado.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.aitor3ml.avocado.client.websocket.WSConnection;
import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.aitor3ml.avocado.shared.networking.Message;

public class ClientConnectionImpl implements ClientConnection {

	private final URI uri;

	private final ClientConnectionListener listener;

	private final WebSocketClient client;

	private final WSConnection socket;

	private final AvocadoDeserializer avocadoDeserializer;

	public ClientConnectionImpl(String host, int port, ClientConnectionListener listener,
			AvocadoDeserializer avocadoDeserializer) throws URISyntaxException {
		uri = new URI("ws://" + host + ":" + port);
		this.listener = listener;
		this.avocadoDeserializer = avocadoDeserializer;

		client = new WebSocketClient();
		socket = new WSConnection(this);
	}

	@Override
	public void connect() throws Exception {
		client.start();
		ClientUpgradeRequest request = new ClientUpgradeRequest();
		client.connect(socket, uri, request);
	}

	@Override
	public void disconnect() throws Exception {
		client.stop();
	}

	public void connected() {
		listener.connected();
	}

	public void message(Message msg) {
		listener.message(msg);
	}

	public void closed(int statusCode, String reason) {
		listener.closed(statusCode, reason);
	}

	@Override
	public void send(Message msg) throws IOException {
		socket.send(msg);
	}

	@Override
	public boolean isConnected() {
		return client.isStarted();
	}

	public AvocadoDeserializer getAvocadoDeserializer() {
		return avocadoDeserializer;
	}

	public void error(Throwable t) {
		listener.error(t);
	}

}
