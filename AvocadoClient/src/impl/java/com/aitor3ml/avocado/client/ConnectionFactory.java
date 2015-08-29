package com.aitor3ml.avocado.client;

public class ConnectionFactory {

	public static ClientConnection createConnection(String host, int port, ClientConnectionListener listener)
			throws Exception {
		return new ClientConnectionImpl(host, port, listener);
	}

}
