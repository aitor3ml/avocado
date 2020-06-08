package com.aitor3ml.avocado.client.websocket;

import java.io.IOException;
import java.io.InputStream;

import com.aitor3ml.avocado.client.ClientConnectionImpl;
import com.aitor3ml.avocado.client.websocket.gwt.CloseEvent;
import com.aitor3ml.avocado.client.websocket.gwt.Websocket;
import com.aitor3ml.avocado.client.websocket.gwt.WebsocketListener;
import com.aitor3ml.avocado.shared.networking.Message;
import com.aitor3ml.avocado.shared.networking.binary.BinaryCoder;

public class WSConnection implements WebsocketListener {

	private final ClientConnectionImpl clientConnection;
	private final Websocket client;

	public WSConnection(Websocket client, ClientConnectionImpl clientConnectionImplGwt) {
		this.client = client;
		this.clientConnection = clientConnectionImplGwt;
		client.addListener(this);
	}

	@Override
	public void onClose(CloseEvent event) {
		System.out.println(event);
		clientConnection.error(new Throwable(event.toString()));
	}

	@Override
	public void onOpen() {
		clientConnection.connected();
	}

	public void onMessage(String msg) {
		String[] parts = msg.split(":", 2);
		switch (parts[0]) {
		case "data":
			System.out.println("unsupported data");
			break;
		case "ping":
			client.send("pong:" + parts[1]);
			break;
		default:
			throw new RuntimeException("unkown message type:" + parts[0]);
		}

	}

	public void onMessage(InputStream stream) throws ClassNotFoundException, IOException {
		Message msg = BinaryCoder.decode(stream, clientConnection.getAvocadoDeserializer());
		clientConnection.message(msg);
	}

}
