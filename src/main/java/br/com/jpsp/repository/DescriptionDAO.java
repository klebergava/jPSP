package br.com.jpsp.repository;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.Description;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class DescriptionDAO extends Repository {
	private final static Logger log = LogManager.getLogger(DescriptionDAO.class);
	public static final DescriptionDAO instance = new DescriptionDAO();
	protected final String DESC_CACHE = "DESC_CACHE";

	private DescriptionDAO() {
		super();
		cacheAllDescriptions();
	}

	/*
	 *
	 */
	private void cacheAllDescriptions() {
		Set<Description> cached = getAll();
		cache.put(DESC_CACHE, cached);
	}

	/**
	 *
	 * @return
	 */
	public Set<Description> getAllCachedDescriptions() {
		@SuppressWarnings("unchecked")
		Set<Description> cached = (Set<Description>) cache.get(DESC_CACHE);
		if (Utils.isEmpty(cached)) {
			cached = getAll();
			cache.put(DESC_CACHE, cached);
		}
		return cached;
	}

	/**
	 *
	 * @param toRemove
	 */
	public void remove(Description toRemove) {
		try {
			execute("DELETE FROM " + HIST_DESC_TABLE + " WHERE descricao = '" + toRemove.getDescription() + "';");
			cacheAllDescriptions();
		} catch (Exception e) {
			log.error("remove() " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param desc
	 */
	public void add(Description desc) {
		if (desc != null) {

			boolean exists = exists(desc);

			if (!exists) {
				try {
					execute("INSERT INTO '" + HIST_DESC_TABLE + "' (descricao) values ('" + desc.getDescription() + "');");
					cacheAllDescriptions();
				} catch (Exception e) {
					log.error("add() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * @param desc
	 * @return
	 */
	public boolean exists(Description desc) {
		boolean exists = false;
		try {
			openConnection(false);
			Result r = executeQuery("select count(*) from " + HIST_DESC_TABLE + " where lower(descricao) = '" + Utils.toLower(desc.getDescription()) + "';");
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

	/**
	 *
	 * @return
	 */
	public Set<Description> getAll() {
		Set<Description> descriptions = new TreeSet<Description>();
		try {
			openConnection(false);
			Result r = executeQuery("select descricao from " + HIST_DESC_TABLE + " order by descricao;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					descriptions.add(new Description(r.getString("descricao")));
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

		return descriptions;
	}

	/**
	 *
	 * @return
	 */
	public Set<String> getAllDescriptions() {
		Set<String> descriptions = new TreeSet<String>();
		try {
			openConnection(false);
			Result r = executeQuery("select descricao from " + HIST_DESC_TABLE + " order by descricao;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					descriptions.add(r.getString("descricao"));
				}
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getAllDescriptions() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getAllDescriptions() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getAllDescriptions() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return descriptions;
	}

	/**
	 *
	 * @return
	 */
	public boolean deleteAll() {
		boolean allDelted = false;
		try {
			execute("DELETE FROM " + HIST_DESC_TABLE + ";");
			allDelted = true;
		} catch (Exception e) {
			log.error("deleteAll() " + e.getMessage());
			e.printStackTrace();
		}
		return allDelted;
	}

}
