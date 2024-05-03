package br.com.jpsp.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.jpsp.services.TypeClassificationServices;
import br.com.jpsp.utils.Utils;

public class TaskDateWrapper {
	private String date;
	private String activity;
	private String taskClass;
	private List<Integer> intervals;
	private long interruption;
	private String workedHours;
	private long workedHoursAsLong;
	private String description;
	private String beginHour;
	private String endHour;
	private Task previousTask;
	
	private final TypeClassificationServices services = TypeClassificationServices.instance;
	private final Map<String, Long> typeClass = new HashMap<String, Long>();

	public TaskDateWrapper(Task task) {
		this.previousTask = task;

		this.date = task.getBeginDateAsString();
		this.activity = task.getActivity();
		this.taskClass = task.getTaskClass();
		this.description = Utils.isEmpty(task.getDescription()) ? "" : task.getDescription();
		this.interruption = 0L;
		this.intervals = new ArrayList<Integer>();
		this.beginHour = Utils.date2String(task.getBegin(), Utils.HH_mm);
		this.endHour = Utils.date2String(task.getEnd(), Utils.HH_mm);
		this.workedHoursAsLong = task.getDelta();
		this.workedHours = Utils.getShortTimeByDelta(this.workedHoursAsLong);
		
		this.loadTypeClass();
	}

	private void loadTypeClass() {
		Set<TypeClassification> allTypeClass = this.services.getAll();
		for (TypeClassification tc : allTypeClass) {
			typeClass.put(tc.getDescription(), tc.getId());
		}
		
	}

	public void addTask(Task t) {
		if (t != null) {

			this.endHour = Utils.date2String(t.getEnd(), Utils.HH_mm);
			long intervalBetweenTasks = Utils.getInterval(this.previousTask, t);
			this.interruption += Long.parseLong(Utils.getMinutesByDelta(intervalBetweenTasks));
			this.intervals.add(Integer.valueOf(Integer.parseInt(Utils.getMinutesByDelta(intervalBetweenTasks))));
			if (!Utils.isEmpty(t.getDescription()) && !t.getDescription().equals(this.description)) {

				if (!Utils.isEmpty(this.description)) {
					this.description = String.valueOf(this.description) + " / " + t.getDescription();
				}
				this.description = String.valueOf(this.description) + t.getDescription();
			}

			this.workedHoursAsLong += t.getDelta();
			this.workedHours = Utils.getShortTimeByDelta(this.workedHoursAsLong);

			this.previousTask = t;
		}
	}

	@SuppressWarnings("unused")
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + ((this.activity == null) ? 0 : this.activity.hashCode());
		result = 31 * result + ((this.date == null) ? 0 : this.date.hashCode());
		result = 31 * result + ((this.taskClass == null) ? 0 : this.taskClass.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaskDateWrapper other = (TaskDateWrapper) obj;
		if (this.activity == null) {
			if (other.activity != null)
				return false;
		} else if (!this.activity.equals(other.activity)) {
			return false;
		}
		if (this.date == null) {
			if (other.date != null)
				return false;
		} else if (!this.date.equals(other.date)) {
			return false;
		}
		if (this.taskClass == null) {
			if (other.taskClass != null)
				return false;
		} else if (!this.taskClass.equals(other.taskClass)) {
			return false;
		}
		return true;
	}

	public String getDate() {
		return this.date;
	}

	public String getActivity() {
		return this.activity;
	}

	public String getTaskClass() {
		return this.taskClass;
	}

	public List<Integer> getIntervals() {
		return this.intervals;
	}

	public long getInterruption() {
		return this.interruption;
	}

	public String getWorkedHours() {
		return this.workedHours;
	}

	public String getDescription() {
		return this.description;
	}

	public String getBeginHour() {
		return this.beginHour;
	}

	public String getEndHour() {
		return this.endHour;
	}

	public int[] getIntervalsAsArrayOfSize(int size) {
		int[] i = new int[size];

		for (int x = 0; x < size; x++) {
			if (x < this.intervals.size()) {
				i[x] = ((Integer) this.intervals.get(x)).intValue();
			}
		}

		return i;
	}

	public int getTaskClassAsInt() {
		return this.typeClass.get(this.taskClass).intValue();
	}
}
