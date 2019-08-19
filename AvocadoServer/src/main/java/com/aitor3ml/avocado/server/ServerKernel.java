package com.aitor3ml.avocado.server;

import javax.servlet.ServletException;

import com.aitor3ml.avocado.server.networking.NetworkingListener;
import com.aitor3ml.avocado.server.networking.NetworkingManager;
import com.aitor3ml.avocado.server.tasks.TaskManager;
import com.aitor3ml.avocado.shared.networking.AvocadoDeserializer;

public class ServerKernel {

	private final ServerConfig serverConfig;

	private final TaskManager taskManager;
	private final NetworkingManager networkingManager;

	public ServerKernel() {
		this(new ServerConfig() {

			@Override
			public Integer getWsPort() {
				return 1666;
			}

			@Override
			public Integer getWssPort() {
				return 1667;
			}

		}, null, null);
	}

	public ServerKernel(ServerConfig serverConfig, NetworkingListener listener,
			AvocadoDeserializer avocadoDeserializer) {
		this.serverConfig = serverConfig;
		taskManager = new TaskManager();
		networkingManager = initNetworking(taskManager, listener, avocadoDeserializer);
	}

	private NetworkingManager initNetworking(TaskManager taskManager, NetworkingListener listener,
			AvocadoDeserializer avocadoDeserializer) {
		if (listener == null)
			return null;
		try {
			return new NetworkingManager(taskManager, serverConfig.getWsPort(), serverConfig.getWssPort(), listener,
					avocadoDeserializer);
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public NetworkingManager getNetworkingManager() {
		return networkingManager;
	}

}
