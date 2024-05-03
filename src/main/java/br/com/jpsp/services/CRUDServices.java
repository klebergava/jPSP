package br.com.jpsp.services;

import java.util.Set;

/**
 * 
 * @author kleber
 */
public interface CRUDServices<T> {
	public Set<T> getAll();
	public void add(T crud);
	public void remove(T crud) throws Exception;
	public void update(T crud) throws Exception;
}
