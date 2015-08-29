package com.aitor3ml.avocado.client;

public interface ClientConnection {

	public void connect() throws Exception;

	public boolean isConnected();

	public void send(String msg);

	public void disconnect() throws Exception;

}
