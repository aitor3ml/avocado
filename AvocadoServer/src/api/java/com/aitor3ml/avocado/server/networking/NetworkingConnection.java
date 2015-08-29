package com.aitor3ml.avocado.server.networking;

public interface NetworkingConnection {

	void send(String text);

	void close();

	long getId();

}
