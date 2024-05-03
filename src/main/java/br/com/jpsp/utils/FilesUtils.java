package br.com.jpsp.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.io.FileUtils;

public class FilesUtils {
	public static final String DATA_DIR = "data";
	
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String USER_NAME = System.getProperty("user.name");
	
	public static final String DATA_FOLDER = "." + FILE_SEPARATOR + DATA_DIR;
	public static final String USER_CONFIG_DATA_FILE = DATA_FOLDER + FILE_SEPARATOR + USER_NAME + "_config.dat";
	public static final String OLD_USER_CONFIG_DATA_FILE = DATA_FOLDER + FILE_SEPARATOR + USER_NAME + ".dat";

	@Deprecated
	public static final String DEV_DATABASE_FILE = DATA_FOLDER + FILE_SEPARATOR + "jpsp.db";
	public static final String DATABASE_FILE_V1 = DATA_FOLDER + FILE_SEPARATOR + "jpsp_v1.db";

	public static final String OUTPUT_DIR = "output";
	public static final String DEFAULT_OUTPUT_FOLDER = "." + FILE_SEPARATOR + OUTPUT_DIR;

	public static final String DB_TXT_FILE_NAME = "jpsp_db.txt";
	public static final String DB_TXT_FILE = DATA_DIR + FILE_SEPARATOR + DB_TXT_FILE_NAME;

	public static final String DEFAULT_ENCODING = "ISO8859_1";
	public static final String USER_HOME_DIR = System.getProperty("user.home");

	public static final String BACKUP_EXT = ".dbkp";

	public static boolean fileExists(String filePath) {
		boolean exists = false;
		File file = new File(filePath);
		exists = file.exists();
		return exists;
	}

	public static boolean checkDirs() {
		boolean dataFolderOK = false;
		File dataFolder = new File(DATA_FOLDER);
		if (dataFolder.exists()) {
			dataFolderOK = true;
		} else {
			dataFolderOK = dataFolder.mkdir();
		}

		boolean outputFolderOK = false;
		File outputFolder = new File(DEFAULT_OUTPUT_FOLDER);
		if (outputFolder.exists()) {
			outputFolderOK = true;
		} else {
			outputFolderOK = outputFolder.mkdir();
		}

		return (dataFolderOK && outputFolderOK);
	}

	public static boolean writeTxtFile(File txtFile, String content) {
		boolean ok = false;
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			fw = new FileWriter(txtFile);
			bw = new BufferedWriter(fw);
			bw.write(content);
			ok = true;
		} catch (Exception exception)

		{
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return ok;
	}

	public static boolean backupDataBase() {
		return backupDataBase(
				String.valueOf(DATABASE_FILE_V1) + "_backup" + Utils.date2String(new Date(), "yyyyMMdd_HHmmss") + ".dbkp");
	}

	public static boolean backupDataBase(String fileName) {
		boolean ok = false;

		File db = new File(DATABASE_FILE_V1);
		if (db.exists()) {
			File destFile = new File(fileName);
			try {
				FileUtils.copyFile(db, destFile);
				ok = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ok;
	}

	public static void verifyDBBackup() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		int dayOfWeek = cal.get(7);
		if (dayOfWeek == 2) {
			backupDataBase();
		}

		delOldBackups();
	}

	private static void delOldBackups() {
		int currentYear = Utils.getCurrentYear();
		int currentMonth = Utils.getCurrentMonth() + 1;

		int fileYear = 0;
		int fileMonth = 0;

		File dataDir = new File("data");
		String name = "";
		byte b;
		int i;
		File[] arrayOfFile;
		for (i = (arrayOfFile = dataDir.listFiles()).length, b = 0; b < i;) {
			File f = arrayOfFile[b];
			name = f.getName();
			if (name.startsWith("jpsp.db_backup")) {
				name = name.replaceAll("jpsp.db_backup", "");
				try {
					fileYear = Integer.parseInt(name.substring(0, 4));
					fileMonth = Integer.parseInt(name.substring(4, 6));

					if (fileYear < currentYear) {
						f.delete();
					}

					if (fileMonth < currentMonth) {
						f.delete();
					}
				} catch (Exception exception) {
				}
			}
			b++;
		}

	}
}
