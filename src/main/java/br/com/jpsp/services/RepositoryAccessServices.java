package br.com.jpsp.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.dao.ActivityDAO;
import br.com.jpsp.dao.DescriptionDAO;
import br.com.jpsp.dao.SystemDAO;
import br.com.jpsp.dao.TaskDAO;
import br.com.jpsp.dao.TypeClassificationDAO;
import br.com.jpsp.utils.FilesUtils;

/**
 *
 * @param <T>
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
