package br.com.jpsp.model;

import java.io.Serializable;

public class Activity extends CRUD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1123056520963009036L;
	
	private String description;
	private long blocked = 0;
	public static final long BLOCKED = 1;
	public static final long UNBLOCKED = 0;
	
	public Activity() {
	}
	
	public Activity(String description, long blocked) {
		this();
		this.description = description;
		this.blocked = blocked;
	}

	public Activity(String newTxt) {
		this.description = newTxt;
	}

	@Override
	public int compareTo(CRUD crud) {
		Activity that = (Activity)crud;
		return this.description.compareTo(that.description);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getBlocked() {
		return blocked;
	}

	public void setBlocked(long blocked) {
		if (blocked > 1 || blocked < 0) {
			this.blocked = 1;
		} else
			this.blocked = blocked;
	}
	
	public boolean isBlocked() {
		return this.blocked == 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Activity other = (Activity) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return this.description;
	}
	
	@Override
	public Activity from(String description) {
		Activity activity = new Activity();
		activity.description = description;
		activity.blocked = UNBLOCKED;
		return activity;
	}
}
