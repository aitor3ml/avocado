package com.aitor3ml.server.tasks;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import com.aitor3ml.avocado.server.tasks.Task;
import com.aitor3ml.avocado.server.tasks.TaskManager;

public class TaskManagerTest {

	@Test
	public void testSomeLibraryMethod() {
		TaskManager taskManager = new TaskManager();
		long now = System.currentTimeMillis();
		Task aTask = new Task(now + 5000L, 3000L) {
			@Override
			public void run() {
				System.out.println("cada 3000ms eternamente " + new Date());
			}
		};
		taskManager.schedule(aTask);
		taskManager.schedule(new Task(now + 8000L) {
			@Override
			public void run() {
				System.out.println("fin" + new Date());
			}
		});
		taskManager.schedule(new Task(now + 1750L, 500L, 10) {
			@Override
			public void run() {
				System.out.println("cada 500ms 10 veces " + new Date());
			}
		});
		Task bTask = new Task(now + 2500L, 4000L) {
			@Override
			public void run() {
				System.out.println("cada 8000ms eternamente " + new Date());
			}
		};
		taskManager.schedule(bTask);
		taskManager.schedule(new Task(now + 1000L) {
			@Override
			public void run() {
				System.out.println("inicio " + new Date());
			}
		});
		taskManager.schedule(new Task(now + 2000L) {
			@Override
			public void run() {
				System.out.println("medio " + new Date());
			}
		});
		taskManager.schedule(new Task(now + 5000L, 1000L, 2) {
			@Override
			public void run() {
				System.out.println("cada 1000ms 2 veces " + new Date());
			}
		});

		taskManager.schedule(new Task(now + 10000L) {
			@Override
			public void run() {
				aTask.cancel();
				bTask.cancel();
			}
		});

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				System.exit(0);
			}
		}, 30000L);

		taskManager.run();
	}

}
