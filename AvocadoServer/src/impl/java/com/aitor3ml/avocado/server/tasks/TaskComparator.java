package com.aitor3ml.avocado.server.tasks;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {

	@Override
	public int compare(Task o1, Task o2) {
		long t = o1.getStart() - o2.getStart();
		if (t != 0)
			t = t / Math.abs(t);
		return (int) t;
	}

}
