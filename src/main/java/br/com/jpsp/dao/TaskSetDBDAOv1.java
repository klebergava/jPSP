package br.com.jpsp.dao;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.System;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskSet;
import br.com.jpsp.model.TypeClassification;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Utils;

/**
 *
 * @author kleber
 *
 */
public class TaskSetDBDAOv1 extends DAO {

	private final static Logger log = LogManager.getLogger(TaskSetDBDAOv1.class);

	protected final String TASK_TABLE = "TAREFA";
	protected final String DESC_HIST_TABLE = "HIST_DESC";
	protected final String ACTIVITY_TABLE = "ATIVIDADE";
	protected final String VERSION_TABLE = "VERSAO";
	protected final String TYPE_CLASS_TABLE = "TIPO_CLASS";
	protected final String SYSTEM_TABLE = "SISTEMA";

	protected final String VERSION = "1.0";

	public static final Map<String, Object> domainTablesCache = new HashMap<String, Object>();

	protected final String SYSTEM_CACHE = "SYSTEM_CACHE";
	protected final String TYPE_CLASS_CACHE = "TYPE_CLASS_CACHE";

	public static final TaskSetDBDAOv1 instance = new TaskSetDBDAOv1();

	protected TaskSetDBDAOv1() {
		log.trace("Starting TaskSetDBDAOv1");
		checkDBFile();
		loadDatabase();

		if (isNewDatabase()) {
			onCreate();
		} else if (isVersionDifferent()) {
			onUpgrade();
		}

		loadDomainTablesCache();

	}

	private void loadDomainTablesCache() {
		domainTablesCache.put(SYSTEM_CACHE, getAllSystems());
		domainTablesCache.put(TYPE_CLASS_CACHE, getAllTypeClassification());
	}

	private void checkDBFile() {
		File dataDir = new File(FilesUtils.DATA_FOLDER_NAME);
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}

		File file = new File(FilesUtils.DATABASE_FILE_V1);
		if (!file.exists()) {
			this.noDatabase = true;
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error("Database file not created: " + FilesUtils.DATABASE_FILE_V1);
				e.printStackTrace();
			}
		}
	}

	private void onUpgrade() {
	}

	protected void loadDatabase() {
		try {
			if (connection == null || connection.isClosed()) {
				try {
					Class.forName("org.sqlite.JDBC");

						File databasePath = new File(FilesUtils.DATABASE_FILE_V1);
						String url = "jdbc:sqlite:" + databasePath.getCanonicalPath();
						connection = DriverManager.getConnection(url);
						this.stmt = connection.createStatement();
				} catch (ClassNotFoundException e) {
					log.trace("loadDatabase() " + e.getMessage());
					e.printStackTrace();
				} catch (SQLException e) {
					log.trace("loadDatabase() " + e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					log.trace("loadDatabase() " + e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void onCreate() {
		log.trace("Creating database tables");
		try {
			execute("CREATE TABLE '" + VERSION_TABLE + "' ('versao' REAL NOT NULL);");
			execute("CREATE TABLE '" + DESC_HIST_TABLE + "' ('descricao' TEXT NOT NULL);");
			execute("CREATE TABLE '" + ACTIVITY_TABLE + "' ('atividade' TEXT NOT NULL, 'bloqueada' INTEGER NOT NULL);");
			execute("CREATE TABLE '" + TASK_TABLE
					+ "' ('id' INTEGER PRIMARY KEY NOT NULL,"
					+ " 'data_inicio' TEXT NOT NULL,"
					+ " 'data_fim' TEXT NOT NULL,"
					+ " 'class' TEXT NOT NULL,"
					+ " 'atividade' TEXT NOT NULL,"
					+ " 'sistema' TEXT NOT NULL,"
					+ " 'descricao' TEXT,"
					+ " 'delta' REAL NOT NULL);");

			execute("CREATE TABLE '" + TYPE_CLASS_TABLE
					+ "' ('id' INTEGER PRIMARY KEY NOT NULL , 'descricao' TEXT NOT NULL, 'bloqueado' INTEGER NOT NULL);");

			execute("CREATE TABLE '" + SYSTEM_TABLE
					+ "' ('id' INTEGER PRIMARY KEY NOT NULL , 'nome' TEXT NOT NULL);");

			insertDefaultData();

		} catch (Exception ex) {
			log.trace("onCreate() " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void insertDefaultData() {
		execute("INSERT INTO '" + VERSION_TABLE + "' (versao) values (" + VERSION + ");");

		execute("INSERT INTO '" + TYPE_CLASS_TABLE + "' (id, descricao, bloqueado) values (1, 'Desenvolvimento', 1);");
		execute("INSERT INTO '" + TYPE_CLASS_TABLE + "' (id, descricao, bloqueado) values (2, 'Correção', 1);");
		execute("INSERT INTO '" + TYPE_CLASS_TABLE + "' (id, descricao, bloqueado) values (3, 'Configuração', 1);");
		execute("INSERT INTO '" + TYPE_CLASS_TABLE + "' (id, descricao, bloqueado) values (4, 'Outros', 1);");

		execute("INSERT INTO '" + SYSTEM_TABLE + "' (id, nome) values (1, '" + Strings.SAGE + "');");
		execute("INSERT INTO '" + SYSTEM_TABLE + "' (id, nome) values (2, '" + Strings.AGILIS + "');");
		execute("INSERT INTO '" + SYSTEM_TABLE + "' (id, nome) values (3, '" + Strings.OTHER_SYS + "');");
	}

	/**
	 *
	 * @param newTask
	 */
	public void addTask(Task newTask) {
		if (newTask != null) {

			 if (newTask.getId() == 0) {
				try {
					long maxId = (getMaxTaskId() + 1);
					execute("INSERT INTO '" + TASK_TABLE
							+ "' (id,data_inicio,data_fim,class,atividade,sistema,descricao,delta) values (" + maxId + ", " + newTask.toValuesString() + ");");
				} catch (Exception e) {
					log.error("addTask() " + e.getMessage());
					e.printStackTrace();
				}
			 } else {
				 this.updateTask(newTask);
			 }
		}
	}

	public void insertNewTask(Task newTask) {
		if (newTask != null) {

				try {
					long maxId = (getMaxTaskId() + 1);
					execute("INSERT INTO '" + TASK_TABLE
							+ "' (id,data_inicio,data_fim,class,atividade,sistema,descricao,delta) values (" + maxId + ", " + newTask.toValuesString() + ");");
				} catch (Exception e) {
					log.error("insertNewTask() " + e.getMessage());
					e.printStackTrace();
				}
		}
	}

	public TaskSet getTaskList() {
		TaskSet instance = new TaskSet();
		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from " + TASK_TABLE
					+ " order by data_inicio ASC;");

			if (!r.isEmpty()) {
				List<Task> tasks = new ArrayList<Task>();

				while (r.moveToNext()) {
					tasks.add(getTask(r));
				}

				instance.setTasks(tasks);
			}

			r = executeQuery("select descricao from " + DESC_HIST_TABLE + " order by descricao DESC;");

			if (!r.isEmpty()) {
				Set<String> descs = new TreeSet<String>();
				while (r.moveToNext()) {
					descs.add(r.getString("descricao"));
				}
				instance.setDescHist(descs);
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

		return instance;
	}

	public int getMaxTaskId() {
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

	public void updateTask(Task updatedTask) {
		try {
			openConnection(true);
			executeUpdate("UPDATE " + TASK_TABLE + " SET data_inicio = '"
					+ Utils.date2String(updatedTask.getBegin(), "dd/MM/yyyy HH:mm:ss") + "'"
					+ ", data_fim = '"	+ Utils.date2String(updatedTask.getEnd(), "dd/MM/yyyy HH:mm:ss") + "'"
					+ ", class = '" + updatedTask.getTaskClass() + "'"
					+ ", atividade = '" + updatedTask.getActivity() + "'"
					+ ", sistema = '" + updatedTask.getSystem() + "'"
					+ ", descricao = '" + updatedTask.getDescription() + "'"
					+ ", delta = " + updatedTask.getDelta()
					+ " WHERE ID = " + updatedTask.getId() + ";");
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

	public void removeTask(Task toRemove) {
		try {
			execute("DELETE FROM " + TASK_TABLE + " WHERE ID = " + toRemove.getId() + ";");
		} catch (Exception e) {
			log.error("removeTask() " + e.getMessage());
			e.printStackTrace();
		}
	}

	public TaskSet filterTasksByDesc(String desc) {
		TaskSet instance = new TaskSet();

		try {
			openConnection(true);
			Result r = executeQuery(
					"select data_inicio,data_fim,class,atividade,sistema,descricao,delta from " + TASK_TABLE + " where lower(descricao) like '%"
							+ Utils.toLower(desc) + "%' order by data_inicio ASC;");
			if (!r.isEmpty()) {
				List<Task> tasks = new ArrayList<Task>();

				while (r.moveToNext()) {
					tasks.add(getTask(r));
				}

				instance.setTasks(tasks);
			}

			r = executeQuery("select descricao from " + DESC_HIST_TABLE + " order by descricao DESC;");

			if (!r.isEmpty()) {
				Set<String> descs = new TreeSet<String>();
				while (r.moveToNext()) {
					descs.add(r.getString("descricao"));
				}
				instance.setDescHist(descs);
			}
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

		return instance;
	}


	public Set<String> getAllDescriptions() {
		Set<String> descriptions = new TreeSet<String>();
		try {
			openConnection(false);
			Result r = executeQuery("select descricao from " + DESC_HIST_TABLE + " order by descricao;");
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

	public List<Task> filterTasksByActivity(String atividade) {
		List<Task> tasks = new ArrayList<Task>();

		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from " + TASK_TABLE
					+ " where lower(atividade) like '%" + Utils.toLower(atividade) + "%' order by data_inicio ASC;");
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

	public String countTasksByActivity(String atividade) {
		String count = "0 dias 0 horas 0 minutos";
		long timeSpent = 0;

		try {
			openConnection(true);
			Result r = executeQuery("select delta from " + TASK_TABLE
					+ " where lower(atividade) = '" + Utils.toLower(atividade) + "';");

			while (r.moveToNext()) {
				timeSpent = r.getLong("delta") + timeSpent;
			}

			long days = (long)(timeSpent / Utils._1_DAY_MILI);
			if (days > 0) {
				timeSpent -= (days * Utils._1_DAY_MILI);
			}

			long hours = (long)(timeSpent / Utils._1_HOUR_MILI);
			if (hours > 0) {
				timeSpent -= (hours * Utils._1_HOUR_MILI);
			}

			long minutes = (long)(timeSpent / Utils._1_MINUTE_MILI);

			count = Long.toString(days) + " dia(s) " + Long.toString(hours) + " hora(s) e " + Long.toString(minutes) + " minuto(s)";

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
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from " + TASK_TABLE
					+ " order by id ASC;");

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

	public Task getMostRecentTask() {
		Task t = null;
		try {
			openConnection(false);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,sistema,descricao,delta from " + TASK_TABLE
					+ " where id = (select max(id) from tarefa);");

			while (r.moveToNext()) {
				t = getTask(r);
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getMostRecentTask() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getMostRecentTask() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getMostRecentTask() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return t;
	}

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

	////////////////// SISTEMA
	/**
	 *
	 * @return
	 */
	public Set<System> getAllCachedSystems() {
		@SuppressWarnings("unchecked")
		Set<System> cached = (Set<System>) domainTablesCache.get(SYSTEM_CACHE);
		if (Utils.isEmpty(cached)) {
			cached = getAllSystems();
			domainTablesCache.put(SYSTEM_CACHE, cached);
		}
		return cached;
	}

	/**
	 *
	 * @return
	 */
	public Set<System> getAllSystems() {
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


	 ///////////////////// TIPO CLASSIFICA��O
	/**
	 *
	 * @return
	 */
	public Set<TypeClassification> getAllCachedTypeClassification() {
		@SuppressWarnings("unchecked")
		Set<TypeClassification> cached = (Set<TypeClassification>) domainTablesCache.get(TYPE_CLASS_CACHE);
		if (Utils.isEmpty(cached)) {
			cached = getAllTypeClassification();
			domainTablesCache.put(TYPE_CLASS_CACHE, cached);
		}
		return cached;
	}

	/**
	 *
	 * @return
	 */
	public Set<TypeClassification> getAllTypeClassification() {
		Set<TypeClassification> types = new TreeSet<TypeClassification>();
		try {
			openConnection(false);
			Result r = executeQuery("select id, descricao, bloqueado from " + TYPE_CLASS_TABLE + " order by id;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					types.add(new TypeClassification(r.getLong("id"), r.getString("descricao"), r.getLong("bloqueado")));
				}
			}
			r.close();
		} catch (SQLException ex) {
			log.error("getAllTypeClassification() " + ex.getMessage());
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("getAllTypeClassification() " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				closeConnection(false);
			} catch (SQLException e) {
				log.error("getAllTypeClassification() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return types;
	}

	/**
	 *
	 * @return
	 */
	public boolean deleteAllTasks() {
		boolean allDelted = false;
		try {
			execute("DELETE FROM " + TASK_TABLE + ";");
			allDelted = true;
		} catch (Exception e) {
			log.error("deleteAllTasks() " + e.getMessage());
			e.printStackTrace();
		}

		return allDelted;
	}
}
