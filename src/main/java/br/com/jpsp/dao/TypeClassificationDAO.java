package br.com.jpsp.dao;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.TypeClassification;

/**
 * 
 * @author kleber
 *
 */
public class TypeClassificationDAO extends TaskSetDBDAOv1 {
	private final static Logger log = LogManager.getLogger(TaskSetDBDAOv1.class);
	public static final TypeClassificationDAO instance = new TypeClassificationDAO();

	private TypeClassificationDAO() {
		super();
	}
	
	public int getMaxTypeClassificationId() {
		int maxtYPEId = 0;
		try {
			openConnection(false);
			Result r = executeQuery("select max(id) from " + TYPE_CLASS_TABLE + ";");
			if (!r.isEmpty()) {
				r.moveToNext();
				maxtYPEId = r.getInt(1);
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getMaxTypeClassificationId() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getMaxTypeClassificationId() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getMaxTypeClassificationId() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return maxtYPEId;
	}
	
	public void addTypeClassification(TypeClassification type) {
		if (type != null) {
			try {
				long maxId = (getMaxTypeClassificationId() + 1);
				execute("INSERT INTO '" + TYPE_CLASS_TABLE
						+ "' (id, descricao, bloqueado) values (" + maxId + ", '" + type.getDescription() + "', " + type.getBlocked() + ");");
				
				domainTablesCache.put(TYPE_CLASS_CACHE, getAllTypeClassification());
			} catch (Exception e) {
				log.error("addTypeClassification() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void updateTypeClassification(TypeClassification updatedType) {
		try {
			openConnection(true);
			executeUpdate("UPDATE " + TYPE_CLASS_TABLE + " SET descricao = '" + updatedType.getDescription() + "', bloqueado = " + updatedType.getBlocked() + " WHERE ID = " + updatedType.getId() + ";");
		
			domainTablesCache.put(TYPE_CLASS_CACHE, getAllTypeClassification());
		} catch (SQLException ex) {
			log.error("updateTypeClassification() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("updateTypeClassification() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("updateTypeClassification() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void removeTypeClassification(TypeClassification toRemove) {
		try {
			execute("DELETE FROM " + TYPE_CLASS_TABLE + " WHERE ID = " + toRemove.getId() + ";");
			
			domainTablesCache.put(TYPE_CLASS_CACHE, getAllTypeClassification());
		} catch (Exception e) {
			log.error("removeTypeClassification() " + e.getMessage());
			e.printStackTrace();
		}
	}
}
