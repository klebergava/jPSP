package br.com.jpsp.dao;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.FilesUtils;

/**
 *
 */
public abstract class Repository extends DAO {
	private final static Logger log = LogManager.getLogger(Repository.class);

	protected final String TASK_TABLE = "TAREFA";
	protected final String HIST_DESC_TABLE = "HIST_DESC";
	protected final String ACTIVITY_TABLE = "ATIVIDADE";
	protected final String VERSION_TABLE = "VERSAO";
	protected final String TYPE_CLASS_TABLE = "TIPO_CLASS";
	protected final String SYSTEM_TABLE = "SISTEMA";

	protected static final Map<String, Object> cache = new HashMap<String, Object>();

	protected final String VERSION = "1.0";

	public Repository() {
		log.trace("Starting Repository");
		checkDBFile();
		loadDatabase();

		if (isNewDatabase()) {
			onCreate();
		} else if (isVersionDifferent()) {
			onUpgrade();
		}

	}

	/**
	 *
	 */
	private void checkDBFile() {
		File dataDir = new File(FilesUtils.DATA_FOLDER_NAME);
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}

		File file = new File(FilesUtils.DATABASE_FILE_PATH);
		if (!file.exists()) {
			this.noDatabase = true;
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error("Database file not created: " + FilesUtils.DATABASE_FILE_PATH);
				e.printStackTrace();
			}
		}
	}

	private void onUpgrade() {
	}

	/**
	 *
	 */
	protected void loadDatabase() {
		try {
			if (connection == null || connection.isClosed()) {
				try {
					Class.forName("org.sqlite.JDBC");

					File databasePath = new File(FilesUtils.DATABASE_FILE_PATH);
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

	/**
	 *
	 */
	public void onCreate() {
		log.trace("Creating database tables");
		try {
			execute("CREATE TABLE '" + VERSION_TABLE + "' ('versao' REAL NOT NULL);");
			execute("CREATE TABLE '" + HIST_DESC_TABLE + "' ('descricao' TEXT NOT NULL);");
			execute("CREATE TABLE '" + ACTIVITY_TABLE + "' ('atividade' TEXT NOT NULL, 'bloqueada' INTEGER NOT NULL);");
			execute("CREATE TABLE '" + TASK_TABLE + "' ('id' INTEGER PRIMARY KEY NOT NULL,"
					+ " 'data_inicio' TEXT NOT NULL," + " 'data_fim' TEXT NOT NULL," + " 'class' TEXT NOT NULL,"
					+ " 'atividade' TEXT NOT NULL," + " 'sistema' TEXT NOT NULL," + " 'descricao' TEXT,"
					+ " 'delta' REAL NOT NULL);");

			execute("CREATE TABLE '" + TYPE_CLASS_TABLE
					+ "' ('id' INTEGER PRIMARY KEY NOT NULL , 'descricao' TEXT NOT NULL, 'bloqueado' INTEGER NOT NULL);");

			execute("CREATE TABLE '" + SYSTEM_TABLE + "' ('id' INTEGER PRIMARY KEY NOT NULL , 'nome' TEXT NOT NULL);");

			insertDefaultData();

		} catch (Exception ex) {
			log.trace("onCreate() " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 *
	 */
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
	 */
	protected void notifyAllThreads() {
		try {
			notifyAll();
		} catch (IllegalMonitorStateException ex) {
			log.info(ex.getMessage());
		}
	}
}
