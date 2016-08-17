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
		Task aTask = new Task(now + 500L, 300L) {
			@Override
			public void run() {
				System.out.println("cada 300ms eternamente " + new Date());
			}
		};
		taskManager.schedule(aTask);
		taskManager.schedule(new Task(now + 800L) {
			@Override
			public void run() {
				System.out.println("fin" + new Date());
			}
		});
		taskManager.schedule(new Task(now + 175L, 50L, 10) {
			@Override
			public void run() {
				System.out.println("cada 50ms 10 veces " + new Date());
			}
		});
		Task bTask = new Task(now + 250L, 400L) {
			@Override
			public void run() {
				System.out.println("cada 800ms eternamente " + new Date());
			}
		};
		taskManager.schedule(bTask);
		taskManager.schedule(new Task(now + 100L) {
			@Override
			public void run() {
				System.out.println("inicio " + new Date());
			}
		});
		taskManager.schedule(new Task(now + 200L) {
			@Override
			public void run() {
				System.out.println("medio " + new Date());
			}
		});
		taskManager.schedule(new Task(now + 500L, 100L, 2) {
			@Override
			public void run() {
				System.out.println("cada 100ms 2 veces " + new Date());
			}
		});

		taskManager.schedule(new Task(now + 1000L) {
			@Override
			public void run() {
				aTask.cancel();
				bTask.cancel();
			}
		});

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				taskManager.stop();
			}
		}, 3000L);

		taskManager.run();
	}

}
