package br.com.jpsp.dao;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.Activity;
import br.com.jpsp.utils.Utils;

public class ActivityDAO extends TaskSetDBDAOv1 {
	private final static Logger log = LogManager.getLogger(ActivityDAO.class);
	public static final ActivityDAO instance = new ActivityDAO();
	
	private ActivityDAO() {
		super();
	}
	
	public void addActivity(Activity activity) {
		if (activity != null) {

			boolean exists = existsActivity(activity);

			if (!exists) {
				try {
					execute("INSERT INTO '" + ACTIVITY_TABLE + "' (atividade, bloqueada) values ('" + activity.getDescription() + "', " + activity.getBlocked() + ");");
				} catch (Exception e) {
					log.error("addActivity() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public void updateActivity(Activity activity) {
		if (activity != null) {

			boolean exists = existsActivity(activity);

			if (!exists) {
				try {
					execute("UPDATE '" + ACTIVITY_TABLE + "' set atividade = '" + activity.getDescription() +"', bloqueada = " + activity.getBlocked() + " WHERE atividade = '" + activity.getDescription() + "';");
				} catch (Exception e) {
					log.error("updateActivity() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean existsActivity(Activity activity) {
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
			log.error("existsActivity() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("existsActivity() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("existsActivity() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return exists;
	}

	public void removeActivity(Activity activity) {
		try {
			execute("DELETE FROM " + ACTIVITY_TABLE + " WHERE atividade = '" + activity.getDescription() + "';");
		} catch (Exception e) {
			log.error("removeActivity() " + e.getMessage());
			e.printStackTrace();
		}
	}

	public Set<Activity> getAllActivities() {
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
}
