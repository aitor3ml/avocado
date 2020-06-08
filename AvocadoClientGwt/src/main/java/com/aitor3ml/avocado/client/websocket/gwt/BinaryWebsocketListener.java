package com.aitor3ml.avocado.client.websocket.gwt;

public interface BinaryWebsocketListener extends WebsocketListener {

	void onMessage(byte[] bytes);
}
