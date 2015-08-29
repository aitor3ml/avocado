package com.aitor3ml.avocado.server.tasks;

public abstract class Task implements Runnable {

	private long start;
	private final Long period;
	private Integer times;

	private boolean canceled = false;

	public Task() {
		this(System.currentTimeMillis());
	}

	public Task(long start) {
		this(start, null, null);
	}

	public Task(long start, Long period) {
		this(start, period, null);
	}

	public Task(long start, Long period, Integer times) {
		super();
		this.start = start;
		this.period = period;
		this.times = times;
	}

	public final long getStart() {
		return start;
	}

	public final Long getPeriod() {
		return period;
	}

	public Integer getTimes() {
		return times;
	}

	public long reschedule() {
		if (period == null)
			throw new RuntimeException("period is null");
		start += period;
		return start;
	}

	public int subtractTime() {
		if (times == null)
			throw new RuntimeException("times is null");
		times -= 1;
		return times;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		this.canceled = true;
	}

}
