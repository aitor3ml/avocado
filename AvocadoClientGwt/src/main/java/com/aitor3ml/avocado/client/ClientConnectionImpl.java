package com.aitor3ml.avocado.client;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.aitor3ml.avocado.client.websocket.WSConnection;
import com.aitor3ml.avocado.client.websocket.gwt.Websocket;
import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.aitor3ml.avocado.shared.networking.Message;
import com.aitor3ml.avocado.shared.networking.binary.BinaryCoder;

public class ClientConnectionImpl implements ClientConnection {

	private final String uri;

	private final ClientConnectionListener listener;

	private final Websocket client;

	private final WSConnection socket;

	private final AvocadoDeserializer avocadoDeserializer;

	public ClientConnectionImpl(String host, int port, boolean ssl, ClientConnectionListener listener,
			AvocadoDeserializer avocadoDeserializer) {
		Websocket client = null;
		if (ssl)
			throw new RuntimeException("ssl unsupported");

		uri = "ws://" + host + ":" + port;
		client = new Websocket(uri);

		this.client = client;
		this.listener = listener;
		this.avocadoDeserializer = avocadoDeserializer;

		socket = new WSConnection(client, this);
		client.open();
	}

	@Override
	public void connect() throws Exception {
		client.open();
	}

	@Override
	public void disconnect() throws Exception {
		client.close();
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
		ByteBuffer bb = BinaryCoder.encode(msg);
		client.send(bb.array());
	}

	@Override
	public boolean isConnected() {
		return client.getState() == 1;
	}

	public AvocadoDeserializer getAvocadoDeserializer() {
		return avocadoDeserializer;
	}

	public void error(Throwable t) {
		listener.error(t);
	}

}
