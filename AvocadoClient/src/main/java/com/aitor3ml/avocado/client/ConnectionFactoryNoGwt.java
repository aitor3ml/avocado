package com.aitor3ml.avocado.client;

import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;
import com.google.common.annotations.GwtIncompatible;

public class ConnectionFactoryNoGwt extends ConnectionFactory {

	@GwtIncompatible
	@Override
	public ClientConnection createConnectionInt(String host, int port, boolean ssl, ClientConnectionListener listener,
			AvocadoDeserializer avocadoDeserializer) throws Exception {
		return new ClientConnectionImpl(host, port, ssl, listener, avocadoDeserializer);
	}

}
