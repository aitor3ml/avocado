package com.aitor3ml.avocado.client;

import org.junit.Test;

public class ClientConnectionTest implements ClientConnectionListener {

	private static final String HOST = "localhost";
	private static final int PORT = 1666;
	private ClientConnection connection = null;

	@Test
	public void test() throws Exception {
		connection = ConnectionFactory.createConnection(HOST, PORT, this);
		connection.connect();
		Thread.sleep(5000);
	}

	@Override
	public void connected() {
		System.out.println("connected");
		connection.send("conectado");
	}

	@Override
	public void message(String msg) {
		System.out.println("message: " + msg);
		connection.send("echo " + msg);
	}

	@Override
	public void closed(int statusCode, String reason) {
		System.out.println("closed: " + statusCode + " - " + reason);
	}

}
