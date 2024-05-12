package br.com.jpsp.dao;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.Activity;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class ActivityDAO extends Repository {
	private final static Logger log = LogManager.getLogger(ActivityDAO.class);
	public static final ActivityDAO instance = new ActivityDAO();
	protected final String ACTIVITY_CACHE = "ACTIVITY_CACHE";

	private ActivityDAO() {
		super();
		cacheAllActivities();
	}

	/*
	 *
	 */
	private void cacheAllActivities() {
		Set<Activity> cached = getAll();
		cache.put(ACTIVITY_CACHE, cached);
	}

	/**
	 *
	 * @return
	 */
	public Set<Activity> getAllCachedActivities() {
		@SuppressWarnings("unchecked")
		Set<Activity> cached = (Set<Activity>) cache.get(ACTIVITY_CACHE);
		if (Utils.isEmpty(cached)) {
			cached = getAll();
			cache.put(ACTIVITY_CACHE, cached);
		}
		return cached;
	}

	/**
	 *
	 * @param activity
	 */
	public void add(Activity activity) {
		if (activity != null) {

			boolean exists = exists(activity);

			if (!exists) {
				try {
					execute("INSERT INTO '" + ACTIVITY_TABLE + "' (atividade, bloqueada) values ('" + activity.getDescription() + "', " + activity.getBlocked() + ");");
					cacheAllActivities();
				} catch (Exception e) {
					log.error("add() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * @param activity
	 */
	public void update(Activity activity) {
		if (activity != null) {

			boolean exists = exists(activity);

			if (!exists) {
				try {
					execute("UPDATE '" + ACTIVITY_TABLE + "' set atividade = '" + activity.getDescription() +"', bloqueada = " + activity.getBlocked() + " WHERE atividade = '" + activity.getDescription() + "';");
					cacheAllActivities();
				} catch (Exception e) {
					log.error("update() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 *
	 * @param activity
	 * @return
	 */
	private boolean exists(Activity activity) {
		boolean exists = false;
		try {
			openConnection(false);
			Result r = executeQuery(
					"select count(*) from " + ACTIVITY_TABLE + " where lower(atividade) = '" + Utils.toLower(activity.getDescription()) + "';");
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
	 * @param activity
	 */
	public void remove(Activity activity) {
		try {
			execute("DELETE FROM " + ACTIVITY_TABLE + " WHERE atividade = '" + activity.getDescription() + "';");
			cacheAllActivities();
		} catch (Exception e) {
			log.error("remove() " + e.getMessage());
			e.printStackTrace();
		}
	}

	public Set<Activity> getAll() {
		Set<Activity> activities = new TreeSet<Activity>();
		try {
			openConnection(false);
			Result r = executeQuery("select atividade, bloqueada from " + ACTIVITY_TABLE + " order by atividade;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					activities.add(new Activity(r.getString("atividade"), r.getLong("bloqueada")));
				}
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getAllActivities() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getAllActivities() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getAllActivities() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return activities;
	}

	/**
	 *
	 * @return
	 */
	public boolean deleteAll() {
		boolean allDelted = false;
		try {
			execute("DELETE FROM " + ACTIVITY_TABLE + ";");
			allDelted = true;
		} catch (Exception e) {
			log.error("deleteAll() " + e.getMessage());
			e.printStackTrace();
		}
		return allDelted;
	}
}
