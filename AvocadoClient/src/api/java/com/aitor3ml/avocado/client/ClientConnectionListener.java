package com.aitor3ml.avocado.client;

public interface ClientConnectionListener {

	void connected();

	void message(String msg);

	void closed(int statusCode, String reason);

}
