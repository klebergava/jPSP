package br.com.jpsp.model;

import java.util.HashMap;
import java.util.Map;

public class TaskTypeWrapper {
	private String taskClass;
	private long totalTime;
	private Map<String, Long> taskTimes = new HashMap<String, Long>();

	public String getTaskClass() {
		return this.taskClass;
	}

	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}

	public void addTask(Task t) {
		String key = t.getDescription();
		Long value = this.taskTimes.get(key);

		if (value == null) {
			value = new Long(0L);
		}

		value = Long.valueOf(value.longValue() + t.getDelta());

		this.totalTime += t.getDelta();

		this.taskTimes.put(key, value);
	}

	public long getTotalTime() {
		return this.totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public Map<String, Long> getTaskTimes() {
		return this.taskTimes;
	}

	public void setTaskTimes(Map<String, Long> taskTimes) {
		this.taskTimes = taskTimes;
	}
}
