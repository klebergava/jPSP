package br.com.jpsp.model;

public class Description extends CRUD {
	
	private String description;
	
	public Description() {}
	
	public Description(String description) {
		this();
		this.description = description;
	}

	@Override
	public CRUD from(String description) {
		Description desc = new Description(description);
		return desc;
	}
	
	@Override
	public int compareTo(CRUD crud) {
		Description that = (Description)crud;
		return this.description.compareTo(that.description);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return this.description;
	}
	
	@Override
	public boolean isBlocked() {
		return false;
	}
	
}
