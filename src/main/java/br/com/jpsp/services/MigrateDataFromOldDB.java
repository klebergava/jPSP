package br.com.jpsp.services;

import java.io.File;
import java.util.Set;

import br.com.jpsp.dao.ActivityDAO;
import br.com.jpsp.dao.DescriptionDAO;
import br.com.jpsp.dao.DesenvTaskSetDBDAO;
import br.com.jpsp.dao.TaskSetDBDAOv1;
import br.com.jpsp.model.Activity;
import br.com.jpsp.model.Configuration;
import br.com.jpsp.model.Description;
import br.com.jpsp.model.Task;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Utils;

@SuppressWarnings("deprecation")
public class MigrateDataFromOldDB {
	
	private final DesenvTaskSetDBDAO oldDBDAO = DesenvTaskSetDBDAO.instance;
	private final TaskSetDBDAOv1 newDBDAO = TaskSetDBDAOv1.instance;
	private final ActivityDAO activityDao = ActivityDAO.instance;
	private final DescriptionDAO descriptionDAO = DescriptionDAO.instance;

	public void migrateData() {
		
		// migrando a configura��o para arquivo
		Configuration oldConfig = oldDBDAO.getConfiguration();
		ConfigServices configServices = ConfigServices.instance;
		configServices.updateConfiguration(oldConfig);
		
		// migrando atividades
		Set<String> activities = oldDBDAO.getAllActivities();
		if (!Utils.isEmpty(activities)) {
			for (String activity : activities) {
				System.out.println("Incluindo atividade '" + activity + "'...");
				activityDao.addActivity(new Activity(activity, Activity.UNBLOCKED));
				
			}
		}
		
		// migrando descri��es
		Set<String> descriptions = oldDBDAO.getAllDescriptions();
		if (!Utils.isEmpty(descriptions)) {
			for (String desc : descriptions) {
				System.out.println("Incluindo descri��o '" + desc + "'...");
				descriptionDAO.addHistDesc(new Description(desc));
			}
		}
		
		// migrando tarefas
		Set<Task> tasks = oldDBDAO.getAllTasks();
		if (!Utils.isEmpty(tasks)) {
			for (Task task : tasks) {
				if ("1".equals(task.getTaskClass())) {
					task.setTaskClass("Desenvolvimento");
				} else if ("2".equals(task.getTaskClass())) {
					task.setTaskClass("Corre��o");
				} else if ("3".equals(task.getTaskClass())) {
					task.setTaskClass("Outros");
				}
				
				System.out.println("Atualizando tarefa '" + task.getTaskClass() + "'...");
				newDBDAO.insertNewTask(task);
			}
		}
		
		File oldDBFile = new File(FilesUtils.DATABASE_FILE_V1);
		oldDBFile.delete();		
	}

}
