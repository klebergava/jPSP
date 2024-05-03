package br.com.jpsp.model;

import java.io.Serializable;

/**
 * 
 * @author kleber
 *
 */
public class TypeClassification extends CRUD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 690323954671573806L;
	
	private String description;
	private long blocked;
	
	public static final long BLOCKED = 1;
	public static final long UNBLOCKED = 0;
	
	public TypeClassification() {
	}
	
	public TypeClassification(long id, String description, long blocked) {
		this();
		this.id = id;
		this.description = description;
		this.blocked = blocked;
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
			this.blocked = BLOCKED;
		} else
			this.blocked = blocked;
	}
	
	public boolean isBlocked() {
		return this.blocked == BLOCKED;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		TypeClassification other = (TypeClassification) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	@Override
	public String toString() {
		return this.description;
	}

	@Override
	public TypeClassification from(String description) {
		TypeClassification type = new TypeClassification();
		type.description = description;
		type.blocked = UNBLOCKED;
		return type;
	}
}
