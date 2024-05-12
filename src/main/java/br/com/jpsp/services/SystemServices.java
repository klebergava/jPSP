package br.com.jpsp.services;

import java.util.Set;

import br.com.jpsp.model.System;

/**
 *
 * @author kleber
 *
 */
public class SystemServices extends RepositoryAccessServices implements CRUDServices<System> {

	public static final SystemServices instance = new SystemServices();

	private SystemServices() {
		super();
	}

	@Override
	public Set<System> getAll() {
		return systemDAO.getAllCachedSystems();
	}

	@Override
	public void add(System system) {
		systemDAO.add(system);
	}

	@Override
	public void remove(System system) throws Exception {
		if (system.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_SYSTEM));
		} else
			this.systemDAO.remove(system);
	}

	@Override
	public void update(System system) throws Exception {
		if (system.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_SYSTEM));
		} else
			this.systemDAO.update(system);
	}

}
