package br.com.jpsp.services;

import java.util.Set;

import br.com.jpsp.dao.SystemDAO;
import br.com.jpsp.model.System;

/**
 * 
 * @author kleber
 *
 */
public class SystemServices implements CRUDServices<System> {
	
	public static final SystemServices instance = new SystemServices();
	
	private final SystemDAO dao = SystemDAO.instance;
	
	private SystemServices() {
		super();
	}

	@Override
	public Set<System> getAll() {
		return dao.getAllSystems();
	}

	@Override
	public void add(System system) {
		dao.addSystem(system);
	}

	@Override
	public void remove(System system) throws Exception {
		if (system.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_SYSTEM));
		} else 
			this.dao.removeSystem(system);
	}

	@Override
	public void update(System system) throws Exception {
		if (system.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_SYSTEM));
		} else 
			this.dao.updateSystem(system);
	}

}
