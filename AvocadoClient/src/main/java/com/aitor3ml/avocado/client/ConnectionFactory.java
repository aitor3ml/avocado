package com.aitor3ml.avocado.client;

import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.google.gwt.core.client.GWT;

public class ConnectionFactory {

	public static ClientConnection createConnection(String host, int port, boolean ssl,
			ClientConnectionListener listener, AvocadoDeserializer avocadoDeserializer) throws Exception {
		if (GWT.isScript())
			return new ClientConnectionImplGwt(host, port, ssl, listener, avocadoDeserializer);
		return new ClientConnectionImpl(host, port, ssl, listener, avocadoDeserializer);
	}

}
