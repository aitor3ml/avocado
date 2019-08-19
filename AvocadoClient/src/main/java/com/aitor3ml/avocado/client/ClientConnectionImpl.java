package com.aitor3ml.avocado.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.aitor3ml.avocado.client.websocket.WSConnection;
import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.aitor3ml.avocado.shared.networking.AvocadoKeyStore;
import com.aitor3ml.avocado.shared.networking.Message;

public class ClientConnectionImpl implements ClientConnection {

	private final URI uri;

	private final ClientConnectionListener listener;

	private final WebSocketClient client;

	private final WSConnection socket;

	private final AvocadoDeserializer avocadoDeserializer;

	public ClientConnectionImpl(String host, int port, boolean ssl, ClientConnectionListener listener,
			AvocadoDeserializer avocadoDeserializer) throws URISyntaxException {
		if (ssl) {
			uri = new URI("wss://" + host + ":" + port);
			SslContextFactory sslContextFactory = new SslContextFactory();
			sslContextFactory.setKeyStoreResource(Resource.newResource(AvocadoKeyStore.getUrl()));
			sslContextFactory.setKeyStorePassword(AvocadoKeyStore.getStorePassword());
			sslContextFactory.setKeyManagerPassword(AvocadoKeyStore.getKeyPassword());
			client = new WebSocketClient(sslContextFactory);
		} else {
			uri = new URI("ws://" + host + ":" + port);
			client = new WebSocketClient();
		}
		this.listener = listener;
		this.avocadoDeserializer = avocadoDeserializer;

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
