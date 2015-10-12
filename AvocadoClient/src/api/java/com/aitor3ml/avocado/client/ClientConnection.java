package com.aitor3ml.avocado.client;

import java.io.IOException;

import com.aitor3ml.avocado.shared.networking.Message;

public interface ClientConnection {

	public void connect() throws Exception;

	public boolean isConnected();

	public void send(String msg);

	public void send(Message msg) throws IOException;

	public void disconnect() throws Exception;

}
