package br.com.jpsp.model;

import java.io.Serializable;

import br.com.jpsp.services.Strings;

/**
 * 
 * @author kleber
 *
 */
public class System extends CRUD implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5648756000434035472L;
	
	private String name;
	
	public System() {
		
	}
	
	public System(long id, String name) {
		this();
		this.id = id;
		this.name = name;
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
		System other = (System) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBlocked() {
		return Strings.SAGE.equals(this.name) || Strings.AGILIS.equals(this.name) || Strings.OTHER_SYS.equals(this.name);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public System from(String name) {
		System sys = new System();
		sys.name = name;
		return sys;
	}

}
