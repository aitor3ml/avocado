package com.aitor3ml.avocado.server.tasks;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.PriorityBlockingQueue;

public class TaskManager {

	private final Timer timer = new Timer();
	private final PriorityBlockingQueue<Task> queue;

	public TaskManager() {
		queue = new PriorityBlockingQueue<Task>();
	}

	public void schedule(Task task) {
		long delay = task.getStart() - System.currentTimeMillis();
		if (delay > 0)
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					queue.add(task);
				}
			}, delay);
		else
			queue.add(task);
	}

	public void run() {
		while (true) {
			Task task = null;
			try {
				task = queue.take();
				assert task.getStart() >= System.currentTimeMillis();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			if (task.isCanceled())
				continue;

			long delay = System.currentTimeMillis() - task.getStart();
			if (delay > 50)
				System.err.println(delay + "ms late:" + task);

			try {
				long start = System.currentTimeMillis();
				task.run();
				long time = System.currentTimeMillis() - start;
				if (time > 50)
					System.err.println(time + "ms to run " + task);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (!task.isCanceled()) {
				Long period = task.getPeriod();
				if (period != null) {
					Integer times = task.getTimes();
					if (times != null)
						times = task.subtractTime();
					if (times == null || times > 0) {
						task.reschedule();
						schedule(task);
					}
				}
			}
		}
	}

}
