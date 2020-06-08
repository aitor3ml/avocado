package com.aitor3ml.avocado.client.websocket.gwt;

public interface WebsocketListener {

	void onClose(CloseEvent event);

	void onMessage(String msg);

	void onOpen();
}
