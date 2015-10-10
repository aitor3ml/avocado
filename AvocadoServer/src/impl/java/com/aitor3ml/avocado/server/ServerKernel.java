package com.aitor3ml.avocado.server;

import javax.servlet.ServletException;

import com.aitor3ml.avocado.server.networking.NetworkingListener;
import com.aitor3ml.avocado.server.networking.NetworkingManager;
import com.aitor3ml.avocado.server.tasks.TaskManager;

public class ServerKernel {

	private final ServerConfig serverConfig;

	private final TaskManager taskManager;
	private final NetworkingManager networkingManager;

	public ServerKernel() {
		this(new ServerConfig(), null);
	}

	public ServerKernel(ServerConfig serverConfig, NetworkingListener listener) {
		this.serverConfig = serverConfig;
		taskManager = new TaskManager();
		networkingManager = initNetworking(taskManager, listener);
	}

	private NetworkingManager initNetworking(TaskManager taskManager, NetworkingListener listener) {
		if (listener == null)
			return null;
		try {
			return new NetworkingManager(taskManager, serverConfig.port, listener);
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
