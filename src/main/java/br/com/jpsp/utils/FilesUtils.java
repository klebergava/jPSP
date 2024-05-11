package br.com.jpsp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FilesUtils {

	private final static Logger log = LogManager.getLogger(FilesUtils.class);

	public static final String DATA_FOLDER_NAME = "data";

	public static final String JAR_FILE_NAME = "jPSP_v1.jar";

	public static final String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String USER_NAME = System.getProperty("user.name");

	public static final String DATA_FOLDER = "." + FILE_SEPARATOR + DATA_FOLDER_NAME;
	public static final String USER_CONFIG_DATA_FILE = DATA_FOLDER + FILE_SEPARATOR + USER_NAME + "_config.dat";
	public static final String OLD_USER_CONFIG_DATA_FILE = DATA_FOLDER + FILE_SEPARATOR + USER_NAME + ".dat";

	public static final String DATABASE_FILE_V1 = DATA_FOLDER + FILE_SEPARATOR + "jpsp_v1.db";

	public static final String OUTPUT_FOLDER_NAME = "output";
	public static final String OUTPUT_FOLDER = "." + FILE_SEPARATOR + OUTPUT_FOLDER_NAME;
	public static final String LOG_FILE = OUTPUT_FOLDER + FILE_SEPARATOR + "jPSP.log";

	public static final String DB_TXT_FILE_NAME = "jpsp_db.txt";
	public static final String DB_TXT_FILE = DATA_FOLDER_NAME + FILE_SEPARATOR + DB_TXT_FILE_NAME;

//	public static final String DEFAULT_ENCODING = "ISO8859_1";
	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final String USER_HOME_DIR = System.getProperty("user.home");

	public static final String BACKUP_EXT = ".dbkp";

	public static final String GPL3_LICENCE_FILE = "license.html";
	public static final String README_FILE = "readme.html";
	public static final String ABOUT_FILE = "about.html";

	public static final String REPORT_FILE_NAME = "report.html";
	public static final String PIE_CHART_TYPE_FILE_NAME = "piecharttype.png";

	public static final String PIE_CHART_ACTIVITY_FILE_NAME = "piechartactivity.png";

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
			log.trace(dataFolder.getAbsolutePath() + " created = " + dataFolderOK);
		}

		boolean outputFolderOK = false;
		File outputFolder = new File(OUTPUT_FOLDER);
		if (outputFolder.exists()) {
			outputFolderOK = true;
		} else {
			outputFolderOK = outputFolder.mkdir();
			log.trace(outputFolder.getAbsolutePath() + " created = " + outputFolderOK);
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
		} catch (Exception exception) {
			log.error("writeTxtFile() " + exception.getMessage());
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				log.error("writeTxtFile() " + ex.getMessage());
				ex.printStackTrace();
			}
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				log.error("writeTxtFile() " + ex.getMessage());
				ex.printStackTrace();
			}
		}

		return ok;
	}

	public static File backupDataBase() {
		return backupDataBase(
				String.valueOf(DATABASE_FILE_V1) + "_backup" + Utils.date2String(new Date(), "yyyyMMdd_HHmmss") + ".dbkp");
	}

	/**
	 *
	 * @param fileName
	 * @return
	 */
	public static File backupDataBase(String fileName) {
		File destFile = null;
		File db = new File(DATABASE_FILE_V1);
		if (db.exists()) {
			destFile = new File(fileName);
			try {
				FileUtils.copyFile(db, destFile);
			} catch (IOException e) {
				destFile = null;
				log.error("backupDataBase() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return destFile;
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
					log.info("Could not dlete " + name);
				}
			}
			b++;
		}

	}

	/**
	 *
	 * @return
	 */
	public static List<String> readAppJARS() {

		List<String> jars = new ArrayList<String>();

		String zipFilePath = findJarFile();

		try (ZipFile zipFile = new ZipFile(zipFilePath)) {
		    Enumeration<? extends ZipEntry> entries = zipFile.entries();
		    while (entries.hasMoreElements()) {
		        ZipEntry entry = entries.nextElement();
		        // Check if entry is a directory
		        if (!entry.isDirectory()) {
		        	String name = entry.getName();
		        	if (name.endsWith(".jar")) {
		        		jars.add(name);
		        	}
		        }
		    }
		} catch (IOException e) {
			log.error("readAppJARS() "+ e.getMessage());
			e.printStackTrace();
		}

		return jars;
	}

	/**
	 *
	 * @return
	 */
	private static String findJarFile() {
		String jarPath = "." + FILE_SEPARATOR + JAR_FILE_NAME;
		File jarFile = new File(jarPath);

		if (!jarFile.exists()) {

		}

		return jarPath;
	}

	public static List<String> readTxtFile(File toRead) {
		List<String> lines = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(toRead))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line + "\n");
            }
        } catch (IOException e) {
        	log.error("readTxtFile(File toRead) "+ e.getMessage());
            e.printStackTrace();
        }

        return lines;
	}

	public static List<String> readTxtFile(File toRead, String encoding) {

		List<String> lines = new ArrayList<String>();

		if (Utils.isEmpty(encoding)) {
			return readTxtFile(toRead);
		} else {

	        try (BufferedReader br = new BufferedReader(new FileReader(toRead))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                lines.add(new String(line.getBytes(), encoding) + "\n");
	            }
	        } catch (IOException e) {
	        	log.error("readTxtFile(File toRead) "+ e.getMessage());
	            e.printStackTrace();
	        }
		}

        return lines;
	}

	/**
	 *
	 * @param inputStream
	 * @return
	 */
	public static List<String> readTxtFile(InputStream inputStream) {
		List<String> lines = new ArrayList<String>();
        // Verificando se o arquivo foi encontrado
        if (inputStream != null) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            	String line = "";
                while ((line = br.readLine()) != null) {
                	lines.add(line);
                }
            } catch (IOException e) {
            	log.error("readTxtFile(InputStream inputStream) "+ e.getMessage());
                e.printStackTrace();
            }
        }

        return lines;
	}

	/**
	 *
	 * @return
	 */
	public static List<String> readLogFile() {
		return readTxtFile(new File(LOG_FILE));
	}

}
