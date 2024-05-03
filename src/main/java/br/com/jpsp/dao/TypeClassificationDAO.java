package br.com.jpsp.dao;

import java.sql.SQLException;

import br.com.jpsp.model.TypeClassification;

/**
 * 
 * @author kleber
 *
 */
public class TypeClassificationDAO extends TaskSetDBDAOv1 {
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

	public void removeTypeClassification(TypeClassification toRemove) {
		try {
			execute("DELETE FROM " + TYPE_CLASS_TABLE + " WHERE ID = " + toRemove.getId() + ";");
			
			domainTablesCache.put(TYPE_CLASS_CACHE, getAllTypeClassification());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
