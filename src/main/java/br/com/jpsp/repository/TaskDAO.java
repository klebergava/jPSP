package br.com.jpsp.repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.Task;
import br.com.jpsp.utils.Utils;

/**
 *
 * @author kleber
 *
 */
public class TaskDAO extends Repository {

	private final static Logger log = LogManager.getLogger(TaskDAO.class);

	public static final TaskDAO instance = new TaskDAO();

	protected TaskDAO() {
		super();
	}

	public Set<Task> getAll() {
		Set<Task> tasks = new TreeSet<Task>();

		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from "
					+ TASK_TABLE + " order by id ASC;");

			if (!r.isEmpty()) {

				while (r.moveToNext()) {
					tasks.add(getTask(r));
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

		return tasks;
	}

	/**
	 *
	 * @param newTask
	 */
	public void add(Task newTask) {
		if (newTask != null) {

			if (newTask.getId() == 0) {
				try {
					long maxId = (getMaxId() + 1);
					execute("INSERT INTO '" + TASK_TABLE
							+ "' (id,data_inicio,data_fim,class,atividade,sistema,descricao,delta) values (" + maxId
							+ ", " + newTask.toValuesString() + ");");
				} catch (Exception e) {
					log.error("addTask() " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				this.updateTask(newTask);
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public List<Task> getTaskList() {
		List<Task> tasks = new ArrayList<Task>();
		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from "
					+ TASK_TABLE + " order by data_inicio ASC;");

			if (!r.isEmpty()) {

				while (r.moveToNext()) {
					tasks.add(getTask(r));
				}
			}

			r.close();
		} catch (SQLException ex) {
			log.error("getTaskList() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getTaskList() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getTaskList() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return tasks;
	}

	/**
	 *
	 * @return
	 */
	public int getMaxId() {
		int maxTaskId = 0;

		try {
			openConnection(false);
			Result r = executeQuery("select max(id) from " + TASK_TABLE + ";");
			if (!r.isEmpty()) {
				r.moveToNext();
				maxTaskId = r.getInt(1);
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getMaxTaskId() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getMaxTaskId() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getMaxTaskId() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return maxTaskId;
	}

	/**
	 *
	 * @param updatedTask
	 */
	public void updateTask(Task updatedTask) {
		try {
			openConnection(true);
			executeUpdate("UPDATE " + TASK_TABLE + " SET data_inicio = '"
					+ Utils.date2String(updatedTask.getBegin(), "dd/MM/yyyy HH:mm:ss") + "'" + ", data_fim = '"
					+ Utils.date2String(updatedTask.getEnd(), "dd/MM/yyyy HH:mm:ss") + "'" + ", class = '"
					+ updatedTask.getTaskClass() + "'" + ", atividade = '" + updatedTask.getActivity() + "'"
					+ ", sistema = '" + updatedTask.getSystem() + "'" + ", descricao = '" + updatedTask.getDescription()
					+ "'" + ", delta = " + updatedTask.getDelta() + " WHERE ID = " + updatedTask.getId() + ";");
		} catch (SQLException ex) {
			log.error("updateTask() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("updateTask() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("updateTask() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param toRemove
	 */
	public void removeTask(Task toRemove) {
		try {
			execute("DELETE FROM " + TASK_TABLE + " WHERE ID = " + toRemove.getId() + ";");
		} catch (Exception e) {
			log.error("removeTask() " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param desc
	 * @return
	 */
	public Set<Task> filterTasksByDesc(String desc) {
		Set<Task> tasks = new TreeSet<Task>();

		try {
			openConnection(true);
			Result r = executeQuery(
					"select data_inicio,data_fim,class,atividade,sistema,descricao,delta from " + TASK_TABLE
							+ " where lower(descricao) like '%" + Utils.toLower(desc) + "%' order by data_inicio ASC;");
			if (!r.isEmpty()) {

				while (r.moveToNext()) {
					tasks.add(getTask(r));
				}
			}

			r = executeQuery("select descricao from " + HIST_DESC_TABLE + " order by descricao DESC;");
			r.close();
		} catch (SQLException ex) {
			log.error("filterTasksByDesc() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("filterTasksByDesc() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("filterTasksByDesc() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return tasks;
	}

	/**
	 *
	 * @param atividade
	 * @return
	 */
	public List<Task> filterTasksByActivity(String activity) {
		List<Task> tasks = new ArrayList<Task>();

		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from "
					+ TASK_TABLE + " where lower(atividade) like '%" + Utils.toLower(activity)
					+ "%' order by data_inicio ASC;");
			if (!r.isEmpty()) {

				while (r.moveToNext()) {
					tasks.add(getTask(r));
				}
			}

			r.close();
		} catch (SQLException ex) {
			log.error("filterTasksByActivity() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("filterTasksByActivity() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("filterTasksByActivity() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return tasks;
	}

	/**
	 *
	 * @param activity
	 * @return
	 */
	public String countTasksByActivity(String activity) {
		String count = "0 dias 0 horas 0 minutos";
		long timeSpent = 0;

		try {
			openConnection(true);
			Result r = executeQuery("select delta from " + TASK_TABLE + " where lower(atividade) = '"
					+ Utils.toLower(activity) + "';");

			while (r.moveToNext()) {
				timeSpent = r.getLong("delta") + timeSpent;
			}

			long days = (long) (timeSpent / Utils._1_DAY_MILI);
			if (days > 0) {
				timeSpent -= (days * Utils._1_DAY_MILI);
			}

			long hours = (long) (timeSpent / Utils._1_HOUR_MILI);
			if (hours > 0) {
				timeSpent -= (hours * Utils._1_HOUR_MILI);
			}

			long minutes = (long) (timeSpent / Utils._1_MINUTE_MILI);

			count = Long.toString(days) + " dia(s) " + Long.toString(hours) + " hora(s) e " + Long.toString(minutes)
					+ " minuto(s)";

			r.close();
		} catch (SQLException ex) {
			log.error("countTasksByActivity() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("countTasksByActivity() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("countTasksByActivity() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return count;
	}

	public List<Task> getAllTasks() {
		List<Task> tasks = new ArrayList<Task>();

		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from "
					+ TASK_TABLE + " order by id ASC;");

			if (!r.isEmpty()) {

				while (r.moveToNext()) {
					tasks.add(getTask(r));
				}
			}

			r.close();
		} catch (SQLException ex) {
			log.error("getAllTasks() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getAllTasks() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getAllTasks() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return tasks;
	}

	/**
	 *
	 * @return
	 */
	public Task getMostRecent() {
		Task t = null;
		try {
			openConnection(false);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from "
					+ TASK_TABLE + " where id = (select max(id) from tarefa);");

			while (r.moveToNext()) {
				t = getTask(r);
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getMostRecent() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getMostRecent() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getMostRecent() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return t;
	}

	/**
	 *
	 * @param r
	 * @return
	 */
	private Task getTask(Result r) {
		Task t = new Task();
		t.setId(r.getLong("id"));
		t.setBegin(Utils.string2Date(r.getString("data_inicio"), "dd/MM/yyyy HH:mm:ss"));
		t.setEnd(Utils.string2Date(r.getString("data_fim"), "dd/MM/yyyy HH:mm:ss"));
		t.setTaskClass(r.getString("class"));
		t.setActivity(r.getString("atividade"));
		t.setSystem(r.getString("sistema"));
		t.setDescription(r.getString("descricao"));
		t.setDelta(r.getLong("delta"));

		return t;
	}

	/**
	 *
	 * @return
	 */
	public boolean deleteAll() {
		boolean allDelted = false;
		try {
			execute("DELETE FROM " + TASK_TABLE + ";");
			allDelted = true;
		} catch (Exception e) {
			log.error("deleteAll() " + e.getMessage());
			e.printStackTrace();
		}

		return allDelted;
	}

}
