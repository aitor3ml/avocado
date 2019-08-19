package com.aitor3ml.avocado.server.networking;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.aitor3ml.avocado.shared.networking.Message;

public interface NetworkingConnection {

	long getId();

	void send(String text);

	void send(Message message) throws IOException;

	void close();

	InetSocketAddress getRemoteAddress();

}
