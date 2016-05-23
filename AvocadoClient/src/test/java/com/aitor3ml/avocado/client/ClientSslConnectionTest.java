package com.aitor3ml.avocado.client;

import java.io.IOException;
import java.io.Serializable;

import org.junit.Test;

import com.aitor3ml.avocado.shared.networking.Message;

public class ClientSslConnectionTest implements ClientConnectionListener {

	private static final String HOST = "localhost";
	private static final int PORT = 1666;
	private ClientConnection connection = null;

	@Test
	public void test() throws Exception {
		connection = ConnectionFactory.createConnection(HOST, PORT, true, this, null);
		connection.connect();
		Thread.sleep(5000);
	}

	@Override
	public void connected() {
		System.out.println("connected");
		send("conectado");
	}

	@Override
	public void message(Message msg) {
		System.out.println("message: " + msg);
		send("echo " + msg);
	}

	@Override
	public void closed(int statusCode, String reason) {
		System.out.println("closed: " + statusCode + " - " + reason);
	}

	private void send(String msg) {
		try {
			connection.send(new TextMessage(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class TextMessage implements Message, Serializable {
		private static final long serialVersionUID = 1L;

		private final String text;

		private TextMessage(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}

	@Override
	public void error(Throwable t) {
		t.printStackTrace();
	}

}
