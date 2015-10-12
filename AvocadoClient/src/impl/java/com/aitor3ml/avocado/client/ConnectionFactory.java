package com.aitor3ml.avocado.client;

import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;

public class ConnectionFactory {

	public static ClientConnection createConnection(String host, int port, ClientConnectionListener listener,
			AvocadoDeserializer avocadoDeserializer) throws Exception {
		return new ClientConnectionImpl(host, port, listener, avocadoDeserializer);
	}

}
