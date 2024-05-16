package br.com.jpsp.repository;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.System;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class SystemDAO extends Repository {
	private final static Logger log = LogManager.getLogger(SystemDAO.class);
	public static final SystemDAO instance = new SystemDAO();
	protected final String SYSTEM_CACHE = "SYSTEM_CACHE";

	private SystemDAO() {
		super();
		cacheAllSystems();
	}

	/*
	 *
	 */
	private void cacheAllSystems() {
		Set<System> cached = getAll();
		cache.put(SYSTEM_CACHE, cached);
	}

	/**
	 *
	 * @return
	 */
	public Set<System> getAllCachedSystems() {
		@SuppressWarnings("unchecked")
		Set<System> cached = (Set<System>) cache.get(SYSTEM_CACHE);
		if (Utils.isEmpty(cached)) {
			cached = getAll();
			cache.put(SYSTEM_CACHE, cached);
		}
		return cached;
	}

	/**
	 *
	 * @return
	 */
	public Set<System> getAll() {
		Set<System> systems = new TreeSet<System>();

		try {
			openConnection(false);
			Result r = executeQuery("select id, nome from " + SYSTEM_TABLE + " order by id;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					systems.add(new System(r.getLong("id"), r.getString("nome")));
				}
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getAllSystems() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getAllSystems() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getAllSystems() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return systems;
	}

	/**
	 *
	 * @return
	 */
	public int getMaxId() {
		int maxSystemId = 0;

		try {
			openConnection(false);
			Result r = executeQuery("select max(id) from " + SYSTEM_TABLE + ";");
			if (!r.isEmpty()) {
				r.moveToNext();
				maxSystemId = r.getInt(1);
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getMaxId() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getMaxId() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getMaxId() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return maxSystemId;
	}

	/**
	 *
	 * @param sys
	 */
	public void add(System sys) {
		if (sys != null) {
			try {
				long maxId = (getMaxId() + 1);
				execute("INSERT INTO '" + SYSTEM_TABLE + "' (id, nome) values (" + maxId + ", '" + sys.getName()
						+ "');");

				cacheAllSystems();
			} catch (Exception e) {
				log.error("add() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param updatedSystem
	 */
	public void update(System updatedSystem) {
		try {
			openConnection(true);
			executeUpdate("UPDATE " + SYSTEM_TABLE + " SET nome = '" + updatedSystem.getName() + "' WHERE ID = "
					+ updatedSystem.getId() + ";");

			cacheAllSystems();
		} catch (SQLException ex) {
			log.error("update() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("update() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("update() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param toRemove
	 */
	public void remove(System toRemove) {
		try {
			execute("DELETE FROM " + SYSTEM_TABLE + " WHERE ID = " + toRemove.getId() + ";");
			cacheAllSystems();
		} catch (Exception e) {
			log.error("remove() " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean deleteAll() {
		boolean allDelted = false;
		try {
			execute("DELETE FROM " + SYSTEM_TABLE + ";");
			allDelted = true;
		} catch (Exception e) {
			log.error("deleteAll() " + e.getMessage());
			e.printStackTrace();
		}

		return allDelted;
	}

	public boolean exists(System system) {
		boolean exists = false;
		try {
			openConnection(false);
			Result r = executeQuery("select count(*) from " + SYSTEM_TABLE + " where lower(nome) = '" + Utils.toLower(system.getName()) + "';");
			if (!r.isEmpty()) {
				r.moveToNext();
				int count = r.getInt(1);
				exists = (count > 0);
			}
			r.close();

		} catch (SQLException ex) {
			log.error("exists() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("exists() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("exists() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return exists;
	}

}
