package com.aitor3ml.server.networking;

import static org.junit.Assert.assertTrue;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import com.aitor3ml.avocado.server.ServerConfig;
import com.aitor3ml.avocado.server.networking.NetworkingConnection;
import com.aitor3ml.avocado.server.networking.NetworkingConnectionListener;
import com.aitor3ml.avocado.server.networking.NetworkingListener;
import com.aitor3ml.avocado.server.networking.NetworkingManager;
import com.aitor3ml.avocado.shared.networking.Message;

public class NetworkingManagerTest implements NetworkingListener {

	@Test
	public void testSomeLibraryMethod() throws Exception {
		ServerConfig config = new ServerConfig() {

			@Override
			public Integer getWsPort() {
				return 1666;
			}

			@Override
			public Integer getWssPort() {
				return 1667;
			}

		};
		NetworkingManager networkingManager = new NetworkingManager(null, config.getWsPort(), config.getWssPort(), this,
				null);
		networkingManager.start();

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					networkingManager.stop();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}, 60000);

		networkingManager.join();
		assertTrue("should not be null", networkingManager != null);
	}

	@Override
	public NetworkingConnectionListener connected(NetworkingConnection connection) {
		return new NetworkingConnectionListener() {

			@Override
			public void connected() {
				System.out.println("connected");
			}

			@Override
			public void closed(int statusCode) {
				System.out.println("closed->" + statusCode);
			}

			@Override
			public void message(Message msg) {
				System.out.println(msg);
			}
		};
	}

}
