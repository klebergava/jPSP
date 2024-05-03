package br.com.jpsp.model;

/**
 * 
 * @author kleber
 *
 */
public abstract class CRUD implements Comparable<CRUD> {
	protected long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public abstract CRUD from(String txt);
	
	@Override
	public int compareTo(CRUD that) {
		return new Long(this.id).compareTo(new Long(that.id));
	}
	
	public abstract boolean isBlocked();
	
}
