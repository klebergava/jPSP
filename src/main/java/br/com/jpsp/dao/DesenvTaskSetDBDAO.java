package br.com.jpsp.dao;

import java.awt.BorderLayout;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import br.com.jpsp.model.Configuration;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskSet;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Utils;

@Deprecated
public class DesenvTaskSetDBDAO extends DAO {
	public static final DesenvTaskSetDBDAO instance = new DesenvTaskSetDBDAO();

	private final String TASK_TABLE = "TAREFA";
	private final String DESC_HIST_TABLE = "HIST_DESC";
	private final String ACTIVITY_TABLE = "ATIVIDADE";
	private final String CONFIG_TABLE = "CONFIGURACOES";

	public DesenvTaskSetDBDAO() {
		checkDBFile();
		loadDatabase();
		if (isNewDatabase()) {
			onCreate();
		} else if (isVersionDifferent()) {
			onUpgrade();
		}
		applyPatch();
	}

	private void applyPatch() {
		Configuration c = getConfiguration();
		if (c == null) {
			createConfigTable();
		}
	}

	private void checkDBFile() {
		File dataDir = new File(FilesUtils.DATA_DIR);
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
	}

	private void onUpgrade() {
	}

	protected void loadDatabase() {
		File file = new File(FilesUtils.DEV_DATABASE_FILE);
		if (!file.exists())
			this.noDatabase = true;
		try {
			Class.forName("org.sqlite.JDBC");
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection("jdbc:sqlite:" + FilesUtils.DEV_DATABASE_FILE);
				this.stmt = connection.createStatement();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void onCreate() {
		execute("CREATE TABLE '" + DESC_HIST_TABLE + "' ('descricao' TEXT NOT NULL);");
		execute("CREATE TABLE '" + ACTIVITY_TABLE + "' ('atividade' TEXT NOT NULL);");
		execute("CREATE TABLE '" + TASK_TABLE
				+ "' ('id' INTEGER PRIMARY KEY NOT NULL , 'data_inicio' TEXT NOT NULL, 'data_fim' TEXT NOT NULL, 'class' TEXT NOT NULL, 'atividade' TEXT NOT NULL, 'descricao' TEXT, 'delta' REAL NOT NULL);");

		createConfigTable();
		
		final JFrame wait = new JFrame();
		
		SwingUtilities.invokeLater(() -> {
			wait.setDefaultCloseOperation(0);
			wait.getContentPane().setLayout(new BorderLayout());
			wait.getContentPane().add(new JLabel("Atualizando banco de dados, por favor aguarde..."), "Center");
			wait.pack();
			wait.setLocationRelativeTo(null);
			wait.setVisible(true);
		});

	}

	private void createConfigTable() {
		execute("CREATE TABLE '" + CONFIG_TABLE
				+ "' ('auto_pausa' INTEGER , 'hora_alerta' TEXT, 'aparencia' TEXT, 'nome' TEXT, 'auto_inicio' INTEGER );");

		try {
			String lookAndFeel = null;
			byte b;
			int i;
			UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo;
			for (i = (arrayOfLookAndFeelInfo = UIManager.getInstalledLookAndFeels()).length, b = 0; b < i;) {
				UIManager.LookAndFeelInfo info = arrayOfLookAndFeelInfo[b];

				if ("Nimbus".equals(info.getName())) {
					lookAndFeel = info.getClassName();
					break;
				}
				b++;
			}

			if (lookAndFeel == null) {
				lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			}

			openConnection(true);
			execute("INSERT INTO '" + CONFIG_TABLE
					+ "' (auto_pausa, hora_alerta, aparencia,nome,auto_inicio) values (1, '17:10', '" +

					lookAndFeel + "', '" + FilesUtils.USER_NAME + "', 0);");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addHistDesc(String s) {
		if (s != null) {

			boolean exists = existsDesc(s);

			if (!exists) {
				try {
					openConnection(true);
					execute("INSERT INTO '" + DESC_HIST_TABLE + "' (descricao) values ('" +

							s + "');");
				} catch (SQLException ex) {
					ex.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean existsDesc(String s) {
		boolean exists = false;
		try {
			openConnection(false);
			Result r = executeQuery("select count(*) from " + DESC_HIST_TABLE + " where lower(descricao) = '"
					+ Utils.toLower(s) + "';");
			if (!r.isEmpty()) {
				r.moveToNext();
				int count = r.getInt(1);
				exists = (count > 0);
			}
			r.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return exists;
	}

	public void addTask(Task t) {
		if (t != null) {
			try {
				openConnection(true);
				long maxId = (getMaxTaskId() + 1);
				execute("INSERT INTO '" + TASK_TABLE
						+ "' (id,data_inicio,data_fim,class,atividade,descricao,delta) values (" +

						maxId + ", " + t.toValuesString() + ");");
			} catch (SQLException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public TaskSet getTaskList() {
		TaskSet instance = new TaskSet();

		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,descricao,delta from " + TASK_TABLE
					+ " order by data_inicio ASC;");

			if (!r.isEmpty()) {
				List<Task> tasks = new ArrayList<Task>();

				while (r.moveToNext()) {
					Task t = new Task();
					t.setId(r.getLong("id"));
					t.setBegin(Utils.string2Date(r.getString("data_inicio"), "dd/MM/yyyy HH:mm:ss"));
					t.setEnd(Utils.string2Date(r.getString("data_fim"), "dd/MM/yyyy HH:mm:ss"));
					t.setTaskClass(r.getString("class"));
					t.setActivity(r.getString("atividade"));
					t.setDescription(r.getString("descricao"));
					t.setDelta(r.getLong("delta"));
					tasks.add(t);
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

		return maxTaskId;
	}

	public void updateTask(Task updatedTask) {
		try {
			openConnection(true);
			executeUpdate("UPDATE " + TASK_TABLE + " SET data_inicio = '"
					+ Utils.date2String(updatedTask.getBegin(), "dd/MM/yyyy HH:mm:ss") + "'" + ", data_fim = '"
					+ Utils.date2String(updatedTask.getEnd(), "dd/MM/yyyy HH:mm:ss") + "'" + ", class = '"
					+ updatedTask.getTaskClass() + "'" + ", atividade = '" + updatedTask.getActivity() + "'"
					+ ", descricao = '" + updatedTask.getDescription() + "'" + ", delta = " + updatedTask.getDelta()
					+ " WHERE ID = " + updatedTask.getId() + ";");
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

	public void removeTask(Task toRemove) {
		try {
			openConnection(true);
			execute("DELETE FROM " + TASK_TABLE + " WHERE ID = " + toRemove.getId() + ";");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void removeDesc(String toRemove) {
		try {
			openConnection(true);
			execute("DELETE FROM " + DESC_HIST_TABLE + " WHERE descricao = '" + toRemove + "';");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public TaskSet filterTasksByDesc(String desc) {
		TaskSet instance = new TaskSet();

		try {
			openConnection(true);
			Result r = executeQuery(
					"select data_inicio,data_fim,class,atividade,descricao,delta from " + TASK_TABLE + " where lower(descricao) like '%"
							+ Utils.toLower(desc) + "%' order by data_inicio ASC;");
			if (!r.isEmpty()) {
				List<Task> tasks = new ArrayList<Task>();

				while (r.moveToNext()) {
					Task t = new Task();
					t.setBegin(Utils.string2Date(r.getString("data_inicio"), "dd/MM/yyyy HH:mm:ss"));
					t.setEnd(Utils.string2Date(r.getString("data_fim"), "dd/MM/yyyy HH:mm:ss"));
					t.setTaskClass(r.getString("class"));
					t.setActivity(r.getString("atividade"));
					t.setDescription(r.getString("descricao"));
					t.setDelta(r.getLong("delta"));
					tasks.add(t);
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

		return instance;
	}

	public void addActivity(String s) {
		if (s != null) {

			boolean exists = existsActivity(s);

			if (!exists) {
				try {
					openConnection(true);
					execute("INSERT INTO '" + ACTIVITY_TABLE + "' (atividade) values ('" +

							s + "');");
				} catch (SQLException ex) {
					ex.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean existsActivity(String s) {
		boolean exists = false;
		try {
			openConnection(false);
			Result r = executeQuery(
					"select count(*) from " + ACTIVITY_TABLE + " where lower(atividade) = '" + Utils.toLower(s) + "';");
			if (!r.isEmpty()) {
				r.moveToNext();
				int count = r.getInt(1);
				exists = (count > 0);
			}
			r.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return exists;
	}

	public void removeActivity(String toRemove) {
		try {
			openConnection(true);
			execute("DELETE FROM " + ACTIVITY_TABLE + " WHERE atividade = '" + toRemove + "';");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Set<String> getAllActivities() {
		Set<String> activities = new TreeSet<String>();
		try {
			openConnection(false);
			Result r = executeQuery("select atividade from " + ACTIVITY_TABLE + " order by atividade;");
			if (!r.isEmpty()) {
				while (r.moveToNext()) {
					activities.add(r.getString("atividade"));
				}
			}
			r.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return activities;
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
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return descriptions;
	}

	public Set<Task> filterTasksByActivity(String atividade) {
		Set<Task> tasks = new TreeSet<Task>();

		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,descricao,delta from " + TASK_TABLE
					+ " where lower(atividade) like '%" + Utils.toLower(atividade) + "%' order by data_inicio ASC;");
			if (!r.isEmpty()) {

				while (r.moveToNext()) {
					Task t = new Task();
					t.setId(r.getLong("id"));
					t.setBegin(Utils.string2Date(r.getString("data_inicio"), "dd/MM/yyyy HH:mm:ss"));
					t.setEnd(Utils.string2Date(r.getString("data_fim"), "dd/MM/yyyy HH:mm:ss"));
					t.setTaskClass(r.getString("class"));
					t.setActivity(r.getString("atividade"));
					t.setDescription(r.getString("descricao"));
					t.setDelta(r.getLong("delta"));
					tasks.add(t);
				}
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

		return tasks;
	}
	
	public String countTasksByActivity(String atividade) {
		String count = "0 dias 00 horas 00 minutos";
		long timeSpent = 0;
		
		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,descricao,delta from " + TASK_TABLE
					+ " where lower(atividade) = '" + Utils.toLower(atividade) + "' order by data_inicio ASC;");
			
			while (r.moveToNext()) {
				timeSpent = r.getLong("delta") + timeSpent;
			}
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timeSpent);
			int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
			int hours = calendar.get(Calendar.HOUR_OF_DAY);
			int minutes = calendar.get(Calendar.MINUTE);
			
			count = dayOfYear + " dia(s) " + hours + " hora(s) e " + minutes + " minuto(s)";
				

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

		return count;
	}
	

	public Set<Task> getAllTasks() {
		Set<Task> tasks = new TreeSet<Task>();

		try {
			openConnection(true);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,descricao,delta from " + TASK_TABLE
					+ " order by data_inicio ASC;");

			if (!r.isEmpty()) {

				while (r.moveToNext()) {
					Task t = new Task();
					t.setId(r.getLong("id"));
					t.setBegin(Utils.string2Date(r.getString("data_inicio"), "dd/MM/yyyy HH:mm:ss"));
					t.setEnd(Utils.string2Date(r.getString("data_fim"), "dd/MM/yyyy HH:mm:ss"));
					t.setTaskClass(r.getString("class"));
					t.setActivity(r.getString("atividade"));
					t.setDescription(r.getString("descricao"));
					t.setDelta(r.getLong("delta"));
					tasks.add(t);
				}
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

		return tasks;
	}

	public Configuration getConfiguration() {
		Configuration c = new Configuration();

		try {
			openConnection(false);
			Result r = executeQuery(
					"select auto_pausa, aparencia, hora_alerta, nome, auto_inicio from " + CONFIG_TABLE + ";");
			if (!r.isEmpty()) {
				r.moveToNext();
				c.setAutoPause(r.getInt("auto_pausa"));
				c.setLookAndFeel(r.getString("aparencia"));
				c.setAlertTime(r.getString("hora_alerta"));
				c.setName(r.getString("nome"));
				c.setAutoStart(r.getInt("auto_inicio"));
			}
			r.close();
		} catch (SQLException ex) {
			c = null;
		} catch (ClassNotFoundException e) {
			c = null;
		} catch (Exception e) {
			c = null;
		}

		return c;
	}

	public void updateConfiguration(Configuration c) {
		try {
			openConnection(true);
			executeUpdate("UPDATE '" + CONFIG_TABLE + "' SET  auto_pausa = " + c.getAutoPause() + ", aparencia = '"
					+ c.getLookAndFeel() + "'" + ", hora_alerta = '" + c.getAlertTime() + "'" + ", nome = '"
					+ c.getName() + "'" + ", auto_inicio = " + c.getAutoStart() + ";");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public Task getMostRecentTask() {
		Task t = null;
		try {
			openConnection(false);
			Result r = executeQuery("select id,data_inicio,data_fim,class,atividade,descricao,delta from " + TASK_TABLE
					+ " where id = (select max(id) from tarefa);");

			while (r.moveToNext()) {
				t = new Task();
				t.setId(r.getLong("id"));
				t.setBegin(Utils.string2Date(r.getString("data_inicio"), "dd/MM/yyyy HH:mm:ss"));
				t.setEnd(Utils.string2Date(r.getString("data_fim"), "dd/MM/yyyy HH:mm:ss"));
				t.setTaskClass(r.getString("class"));
				t.setActivity(r.getString("atividade"));
				t.setDescription(r.getString("descricao"));
				t.setDelta(r.getLong("delta"));
			}
			r.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return t;
	}
}
