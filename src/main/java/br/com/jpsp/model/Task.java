package br.com.jpsp.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.gson.JsonObject;

import br.com.jpsp.utils.Utils;

/**
 *
 */
public class Task extends CRUD implements Serializable {
	private static final long serialVersionUID = -2320799310216267754L;
	private Date begin;
	private Date end;
	private String activity;
	private String description;
	private String taskClass;
	private long delta;
	private String dateAsString;
	private String system;

	public Date getBegin() {
		return this.begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
		this.dateAsString = Utils.date2String(this.begin, Utils.DD_MM_YYYY);
	}

	public Date getEnd() {
		return this.end;
	}

	public void setEnd(Date end) {
		this.end = end;

		if (this.begin != null && this.end != null && this.end.after(this.begin)) {
			setDelta(this.end.getTime() - this.begin.getTime());
		}
	}

	public String getTaskClass() {
		return this.taskClass;
	}

	public void setTaskClass(String taskClass) {
		this.taskClass = taskClass;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getDelta() {
		return this.delta;
	}

	public void setDelta(long delta) {
		this.delta = delta;
	}

	public int getMonth() {
		int month = 0;

		Calendar c = new GregorianCalendar();
		c.setTime(getBegin());
		month = c.get(2);

		return month;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", begin=" + begin + ", end=" + end + ", activity=" + activity + ", description="
				+ description + ", taskClass=" + taskClass + ", delta=" + delta + ", system=" + system + "]";
	}

	@SuppressWarnings("unused")
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = 31 * result + (int) (this.id ^ this.id >>> 32L);
		return result;
	}

	public boolean equals(Object that) {
		if (this == that)
			return true;

		if (that == null)
			return false;

		if (getClass() != that.getClass())
			return false;

		Task other = (Task) that;
		if (this.activity == null) {
			if (other.activity != null)
				return false;
		} else if (!this.activity.equals(other.activity)) {
			return false;
		}

		if (this.dateAsString == null) {
			if (other.dateAsString != null)
				return false;
		} else if (!this.dateAsString.equals(other.dateAsString)) {
			return false;
		}

		if (!this.taskClass.equals(other.taskClass))
			return false;
		return true;
	}

	@Override
	public int compareTo(CRUD crud) {
		java.lang.System.out.println(crud);
		java.lang.System.out.println(this.begin + "\n");
		if (crud != null) {
		Task that = (Task)crud;
			return this.begin.compareTo(that.begin);
		} else return 0;
	}

	public int getDay() {
		int day = 0;

		Calendar c = new GregorianCalendar();
		c.setTime(getBegin());
		day = c.get(5);

		return day;
	}

	public int getYear() {
		int year = 0;

		Calendar c = new GregorianCalendar();
		c.setTime(getBegin());
		year = c.get(1);

		return year;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public int getDayOfWeek() {
		int dayOfWeek = 0;

		Calendar c = new GregorianCalendar();
		c.setTime(getBegin());
		dayOfWeek = c.get(7);

		return dayOfWeek;
	}

	public Task clone() {
		Task clone = new Task();
		clone.setId(0L);
		clone.setBegin(this.begin);
		clone.setEnd(this.end);
		clone.setActivity(this.activity);
		clone.setSystem(this.system);
		clone.setDescription(this.description);
		clone.setDelta(this.delta);
		clone.setTaskClass(this.taskClass);
		return clone;
	}

	public String toValuesString() {
		StringBuffer sb = new StringBuffer("");

		sb.append("'" + Utils.date2String(this.begin, "dd/MM/yyyy HH:mm:ss") + "'");
		sb.append(", '" + Utils.date2String(this.end, "dd/MM/yyyy HH:mm:ss") + "'");
		sb.append(", '" + ((this.taskClass == null) ? " " : this.taskClass) + "'");
		sb.append(", '" + ((this.activity == null) ? " " : this.activity) + "'");
		sb.append(", '" + ((this.system == null) ? " " : this.system) + "'");
		sb.append(", '" + this.description + "'");
		sb.append(", " + this.delta);
		return sb.toString();
	}

	public String getActivity() {
		return this.activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getBeginDateAsString() {
		return Utils.date2String(this.begin, "dd/MM/yyyy");
	}

	@Override
	public Task from(String txt) {
		Task task = new Task();
		task.setActivity(txt);
		return task;
	}

	@Override
	public boolean isBlocked() {
		return false;
	}

	/**
	 *
	 * @return
	 */
	public JsonObject getJson() {
		JsonObject json = new JsonObject();
		json.addProperty("begin", Utils.date2String(this.begin, Utils.DD_MM_YYYY_HH_mm_ss));
		json.addProperty("end", Utils.date2String(this.end, Utils.DD_MM_YYYY_HH_mm_ss));
		json.addProperty("delta", this.delta);
		json.addProperty("activity", Utils.isEmpty(this.activity) ?  "" : this.activity);
		json.addProperty("description", Utils.isEmpty(this.description) ?  "" : this.description);
		json.addProperty("taskClass", Utils.isEmpty(this.taskClass) ?  "" : this.taskClass);
		json.addProperty("system", Utils.isEmpty(this.system) ?  "" : this.system);
		return json;
	}
}
