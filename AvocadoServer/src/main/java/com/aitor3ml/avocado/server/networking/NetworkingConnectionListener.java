package com.aitor3ml.avocado.server.networking;

import com.aitor3ml.avocado.shared.networking.Message;

public interface NetworkingConnectionListener {

	public void connected();

	public void message(Message msg);

	public void closed(int statusCode);

}
