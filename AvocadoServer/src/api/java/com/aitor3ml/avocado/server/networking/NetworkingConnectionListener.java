package com.aitor3ml.avocado.server.networking;

public interface NetworkingConnectionListener {

	public void connected();

	public void message(String msg);

	public void closed(int statusCode);

}
