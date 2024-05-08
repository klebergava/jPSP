package br.com.jpsp.dao;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.System;

public class SystemDAO extends TaskSetDBDAOv1 {
	private final static Logger log = LogManager.getLogger(SystemDAO.class);
	public static final SystemDAO instance = new SystemDAO();

	private SystemDAO() {
		super();
	}
	
	public int getMaxSystemId() {
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
			log.error("getMaxSystemId() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getMaxSystemId() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getMaxSystemId() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return maxSystemId;
	}
	
	public void addSystem(System sys) {
		if (sys != null) {
			try {
				long maxId = (getMaxSystemId() + 1);
				execute("INSERT INTO '" + SYSTEM_TABLE
						+ "' (id, nome) values (" + maxId + ", '" + sys.getName() + "');");
				
				domainTablesCache.put(SYSTEM_CACHE, getAllSystems());
			} catch (Exception e) {
				log.error("addSystem() " + e.getMessage());
				e.printStackTrace();
			}
			
		}
	}
	
	public void updateSystem(System updatedSystem) {
		try {
			openConnection(true);
			executeUpdate("UPDATE " + SYSTEM_TABLE + " SET nome = '" + updatedSystem.getName() + "' WHERE ID = " + updatedSystem.getId() + ";");
			
			domainTablesCache.put(SYSTEM_CACHE, getAllSystems());
		} catch (SQLException ex) {
			log.error("updateSystem() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("updateSystem() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("updateSystem() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void removeSystem(System toRemove) {
		try {
			execute("DELETE FROM " + SYSTEM_TABLE + " WHERE ID = " + toRemove.getId() + ";");
			
			domainTablesCache.put(SYSTEM_CACHE, getAllSystems());
		} catch (Exception e) {
			log.error("removeSystem() " + e.getMessage());
			e.printStackTrace();
		}
	}
	

}
