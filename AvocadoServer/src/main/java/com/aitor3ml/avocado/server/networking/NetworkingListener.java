package com.aitor3ml.avocado.server.networking;

public interface NetworkingListener {

	NetworkingConnectionListener connected(NetworkingConnection connection);

}
