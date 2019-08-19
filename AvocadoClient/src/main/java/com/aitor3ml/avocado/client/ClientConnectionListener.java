package com.aitor3ml.avocado.client;

import com.aitor3ml.avocado.shared.networking.Message;

public interface ClientConnectionListener {

	void connected();

	void message(Message msg);

	void closed(int statusCode, String reason);

	void error(Throwable t);

}
