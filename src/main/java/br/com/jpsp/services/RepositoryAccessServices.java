package br.com.jpsp.services;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.repository.ActivityDAO;
import br.com.jpsp.repository.DescriptionDAO;
import br.com.jpsp.repository.SystemDAO;
import br.com.jpsp.repository.TaskDAO;
import br.com.jpsp.repository.TypeClassificationDAO;
import br.com.jpsp.utils.FilesUtils;

/**
 * 
 * @author kleber
 *
 */
public abstract class RepositoryAccessServices {
	private final static Logger log = LogManager.getLogger(RepositoryAccessServices.class);

	protected final TaskDAO taskDAO = TaskDAO.instance;
	protected final ActivityDAO activityDAO = ActivityDAO.instance;
	protected final DescriptionDAO descDAO = DescriptionDAO.instance;
	protected final SystemDAO systemDAO = SystemDAO.instance;
	protected final TypeClassificationDAO classDAO = TypeClassificationDAO.instance;

	public RepositoryAccessServices() {
		FilesUtils.checkDirs();
	}

	/**
	 *
	 */
	public void purgeDatabase() {
		synchronized (RepositoryAccessServices.this) {
			try {
				taskDAO.deleteAll();
				activityDAO.deleteAll();
				descDAO.deleteAll();
				systemDAO.deleteAll();
				classDAO.deleteAll();
			} catch (Exception ex) {
				log.error("purgeDatabase() " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}
	

	/**
	 * 
	 * @param fileToRestore
	 * @return
	 */
	public boolean restoreDB(File fileToRestore) {
		boolean ok = false;
		synchronized (RepositoryAccessServices.this) {
			String fn = String.valueOf(FilesUtils.DATABASE_FILE_PATH) + "_backup_before_restore" + ".dbkp";
			FilesUtils.backupDataBase(fn);
	
			try {
				FileUtils.copyFile(fileToRestore, new File(FilesUtils.DATABASE_FILE_PATH));
				ok = true;
			} catch (IOException e) {
				log.error("restoreDB() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return ok;
	}
	
	/**
	 * 
	 */
	public File backupDatabase() {
		return FilesUtils.backupDataBase();
	}

}
