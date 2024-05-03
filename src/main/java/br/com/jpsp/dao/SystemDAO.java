package br.com.jpsp.dao;

import java.sql.SQLException;

import br.com.jpsp.model.System;

public class SystemDAO extends TaskSetDBDAOv1 {
	
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
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
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
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void removeSystem(System toRemove) {
		try {
			execute("DELETE FROM " + SYSTEM_TABLE + " WHERE ID = " + toRemove.getId() + ";");
			
			domainTablesCache.put(SYSTEM_CACHE, getAllSystems());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
