package com.aitor3ml.avocado.server.tasks;

import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskManager {

	private final Queue<Task> queue;
	private final Queue<Task> added;

	public TaskManager() {
		queue = new PriorityQueue<Task>(new TaskComparator());
		added = new ConcurrentLinkedQueue<Task>();
	}

	public void schedule(Task task) {
		added.add(task);
	}

	public boolean hasTasks() {
		return queue.size() > 0 || added.size() > 0;
	}

	public void run() {
		runTasks();
		addTasks();
	}

	private void addTasks() {
		Iterator<Task> iterator = added.iterator();
		while (iterator.hasNext()) {
			Task task = iterator.next();
			queue.add(task);
			iterator.remove();
		}
	}

	private void runTasks() {
		Task task = null;
		while ((task = queue.peek()) != null) {
			if (task.isCanceled()) {
				queue.remove();
				continue;
			}
			if (task.getStart() > System.currentTimeMillis())
				continue;
			task = queue.poll();
			task.run();

			if (!task.isCanceled()) {
				Long period = task.getPeriod();
				if (period != null) {
					Integer times = task.getTimes();
					if (times != null)
						times = task.subtractTime();
					if (times == null || times > 0) {
						task.reschedule();
						queue.add(task);
					}
				}
			}
		}
	}

}
