package br.com.jpsp.repository;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.TypeClassification;
import br.com.jpsp.utils.Utils;

/**
 *
 * @author kleber
 *
 */
public class TypeClassificationDAO extends Repository {
	private final static Logger log = LogManager.getLogger(TaskDAO.class);
	public static final TypeClassificationDAO instance = new TypeClassificationDAO();

	protected final String TYPE_CLASS_CACHE = "TYPE_CLASS_CACHE";

	private TypeClassificationDAO() {
		super();
		this.cacheAllTypeClassifications();
	}

	/**
	 *
	 * @return
	 */
	public int getMaxId() {
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

		return maxtYPEId;
	}

	/**
	 *
	 * @param type
	 */
	public void add(TypeClassification type) {
		if (type != null) {
			try {
				long maxId = (getMaxId() + 1);
				execute("INSERT INTO '" + TYPE_CLASS_TABLE
						+ "' (id, descricao, bloqueado) values (" + maxId + ", '" + type.getDescription() + "', " + type.getBlocked() + ");");

				cacheAllTypeClassifications();
			} catch (Exception e) {
				log.error("add() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param updatedType
	 */
	public void update(TypeClassification updatedType) {
		try {
			openConnection(true);
			executeUpdate("UPDATE " + TYPE_CLASS_TABLE + " SET descricao = '" + updatedType.getDescription() + "', bloqueado = " + updatedType.getBlocked() + " WHERE ID = " + updatedType.getId() + ";");

			cacheAllTypeClassifications();
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
	public void remove(TypeClassification toRemove) {
		try {
			execute("DELETE FROM " + TYPE_CLASS_TABLE + " WHERE ID = " + toRemove.getId() + ";");

			cacheAllTypeClassifications();
		} catch (Exception e) {
			log.error("removeTypeClassification() " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @return
	 */
	public Set<TypeClassification> getAllCachedTypeClassification() {
		@SuppressWarnings("unchecked")
		Set<TypeClassification> cached = (Set<TypeClassification>) cache.get(TYPE_CLASS_CACHE);
		if (Utils.isEmpty(cached)) {
			cached = getAll();
			cacheAllTypeClassifications();
		}
		return cached;
	}

	/**
	 *
	 * @return
	 */
	public Set<TypeClassification> getAll() {
		Set<TypeClassification> types = new TreeSet<TypeClassification>();
		try {
			openConnection(false);
			Result r = executeQuery("select id, descricao, bloqueado from " + TYPE_CLASS_TABLE + " order by id;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					types.add(
							new TypeClassification(r.getLong("id"), r.getString("descricao"), r.getLong("bloqueado")));
				}
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getAll() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getAll() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getAll() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return types;
	}

	/**
	 *
	 */
	private void cacheAllTypeClassifications() {
		Set<TypeClassification> cached = getAll();
		cache.put(TYPE_CLASS_CACHE, cached);
	}

	/**
	 *
	 * @return
	 */
	public boolean deleteAll() {
		boolean allDelted = false;
		try {
			execute("DELETE FROM " + TYPE_CLASS_TABLE + ";");
			allDelted = true;
		} catch (Exception e) {
			log.error("deleteAll() " + e.getMessage());
			e.printStackTrace();
		}

		return allDelted;
	}

	/**
	 * 
	 * @param typeClass
	 * @return
	 */
	public boolean exists(TypeClassification typeClass) {
		boolean exists = false;
		try {
			openConnection(false);
			Result r = executeQuery("select count(*) from " + TYPE_CLASS_TABLE + " where lower(descricao) = '" + Utils.toLower(typeClass.getDescription()) + "';");
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
