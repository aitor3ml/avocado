package com.aitor3ml.avocado.server.tasks;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TaskManager {

	private static final long SLEEP_TIME = 5;

	private final PriorityBlockingQueue<Task> queue;

	private long last = 0L;

	private boolean stoping = false;
	private boolean stopped = false;

	public TaskManager() {
		queue = new PriorityBlockingQueue<Task>();
	}

	public void schedule(final Task task) {
		if (stoping) {
			System.out.println("rejected task " + task);
			return;
		}
		queue.add(task);
	}

	public void run() {
		while (!stoping) {
			Task task = null;
			try {
				task = queue.poll(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			if (task == null)
				continue;
			if (task.isCanceled())
				continue;

			long start = task.getStart();
			assert start >= last;
			last = start;

			long delay = System.currentTimeMillis() - start;
			if (delay < 0) {
				queue.put(task);
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}

			if (delay > 50)
				System.err.println(delay + "ms late:" + task.toString());

			try {
				long t = System.currentTimeMillis();
				task.run();
				long time = System.currentTimeMillis() - t;
				if (time > 50)
					System.err.println(time + "ms to run " + task.toString());
			} catch (Exception e) {
				System.err.println(task.toString());
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
		stopped = true;
	}

	public void stop() {
		stoping = true;
	}

	public boolean isStopped() {
		return stopped;
	}

}
