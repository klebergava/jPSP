package br.com.jpsp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TaskSet implements Serializable {
	private static final long serialVersionUID = -3134200546394564271L;
	private List<Task> tasks = new ArrayList<Task>();
	private Set<String> descHist = new TreeSet<String>();

	public void addTask(Task task) {
		this.tasks.add(task);
	}

	public void removeTask(Task toRemove) {
		this.tasks.remove(toRemove);
	}

	public List<Task> getTasks() {
		return this.tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public void updateTaskList(TaskSet taskList) {
		if (taskList != null) {
			this.tasks.addAll(taskList.getTasks());
			if (this.descHist == null) {
				this.descHist = new TreeSet<String>();
			}
			this.descHist.addAll(taskList.getDescHist());
		}
	}

	public void reverseOrder() {
		List<Task> tmp = new ArrayList<Task>(this.tasks);
		Collections.reverse(tmp);
		this.tasks = new ArrayList<Task>();
		this.tasks.addAll(tmp);
	}

	public Set<String> getDescHist() {
		return this.descHist;
	}

	public void setDescHist(Set<String> descHist) {
		this.descHist = descHist;
	}

	public void addDesc(String desc) {
		if (this.descHist == null) {
			this.descHist = new TreeSet<String>();
		}
		this.descHist.add(desc);
	}

	public void removeDesc(String desc) {
		if (this.descHist != null)
			this.descHist.remove(desc);
	}
	
}
