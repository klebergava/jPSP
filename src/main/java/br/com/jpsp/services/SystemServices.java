package br.com.jpsp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.jpsp.model.System;
import br.com.jpsp.utils.Utils;

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
		synchronized (SystemServices.this) {
			return systemDAO.getAllCachedSystems();
		}
	}

	@Override
	public void add(System system) {
		synchronized (SystemServices.this) {
			systemDAO.add(system);
		}
	}

	@Override
	public void remove(System system) throws Exception {
		if (system.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_SYSTEM));
		} else {
			synchronized (SystemServices.this) {
				this.systemDAO.remove(system);
			}
		}
	}

	@Override
	public void update(System system) throws Exception {
		if (system.isBlocked()) {
			throw new Exception(Strings.jPSP.ERROR_BLOCKED.replaceAll("&1", Strings.jPSP.TASK_SYSTEM));
		} else {
			synchronized (SystemServices.this) {
				this.systemDAO.update(system);
			}
		}
	}
	
	public List<String> getAllSystemsNames() {
		Set<br.com.jpsp.model.System> allSystems = this.systemDAO.getAllCachedSystems();
		List<String> allSystemsNames = new ArrayList<String>();
		if (!Utils.isEmpty(allSystems)) {
			for (br.com.jpsp.model.System sys : allSystems) {
				allSystemsNames.add(sys.getName());
			}
		}
		return allSystemsNames;
	}

	public Set<br.com.jpsp.model.System> getAllSystems() {
		Set<br.com.jpsp.model.System> allSystems = this.systemDAO.getAllCachedSystems();
		return allSystems;
	}

	/**
	 * 
	 * @param systems
	 */
	public void addSystems(Set<String> systems) {
		if (!Utils.isEmpty(systems)) {
			systems.forEach(sys -> {
				System newSys = new System().from(sys);
				if (systemDAO.exists(newSys)) {
					systemDAO.add(newSys);
				}
			});
		}
	}
}
