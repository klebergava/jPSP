package br.com.jpsp.services;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.dao.TaskSetDBDAOv1;
import br.com.jpsp.gui.Refreshable;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskActivityWrapper;
import br.com.jpsp.model.TaskDateWrapper;
import br.com.jpsp.model.TaskSet;
import br.com.jpsp.model.TaskTypeWrapper;
import br.com.jpsp.model.TypeClassification;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Utils;

/**
 *
 * @author kleber
 *
 */
public class TaskSetServices {
	private final static Logger log = LogManager.getLogger(TaskSetServices.class);

	public enum Order {
		ASC, DESC;
	}

	private final TaskSetDBDAOv1 dao = TaskSetDBDAOv1.instance;
	public static final TaskSetServices instance = new TaskSetServices();

	private TaskSetServices() {
		log.trace("Starting TaskSetServices");
		FilesUtils.checkDirs();
	}

	public TaskSet getTaskList() {
		return this.dao.getTaskList();
	}

	public void addTask(Task task) {
		this.dao.addTask(task);
	}

	public void updateTask(Task updatedTask) {
		this.dao.updateTask(updatedTask);
	}

	public void removeTask(Task toRemove) {
		this.dao.removeTask(toRemove);
	}

	public List<Task> getTasksOfPeriod(int month, int year) {
		List<Task> tasks = new ArrayList<Task>();

		TaskSet taskSet = getTaskList();
		if (taskSet != null && !taskSet.getTasks().isEmpty()) {
			for (Task t : taskSet.getTasks()) {
				if (t.getMonth() == month && t.getYear() == year) {
					tasks.add(t);
				}
			}
		}

		return tasks;
	}

	public Map<String, TaskTypeWrapper> wrapType(List<Task> tasks) {
		Map<String, TaskTypeWrapper> wrapped = new HashMap<String, TaskTypeWrapper>();

		if (tasks != null) {
			String key = "";

			for (Task t : tasks) {
				key = t.getTaskClass();

				TaskTypeWrapper wrapper = wrapped.get(key);
				if (wrapper == null) {
					wrapper = new TaskTypeWrapper();
					wrapper.setTaskClass(key);
				}

				wrapper.addTask(t);

				wrapped.put(key, wrapper);
			}
		}

		return wrapped;
	}

	public Map<String, TaskActivityWrapper> wrapActivity(List<Task> tasks) {
		Map<String, TaskActivityWrapper> wrapped = new HashMap<String, TaskActivityWrapper>();

		if (tasks != null) {
			String key = "";

			for (Task t : tasks) {
				key = t.getActivity();

				TaskActivityWrapper wrapper = wrapped.get(key);
				if (wrapper == null) {
					wrapper = new TaskActivityWrapper();
					wrapper.setActivity(key);
				}

				wrapper.addTask(t);

				wrapped.put(key, wrapper);
			}
		}

		return wrapped;
	}

	public SortedMap<String, TaskDateWrapper> wrapDate(Set<Task> tasks) {
		SortedMap<String, TaskDateWrapper> wrapped = new TreeMap<String, TaskDateWrapper>();

		if (tasks != null) {

			for (Task t : tasks) {
				String key = t.getBeginDateAsString();
				TaskDateWrapper wrapper = wrapped.get(key);

				if (wrapper == null) {
					wrapper = new TaskDateWrapper(t);
				} else {
					wrapper.addTask(t);
				}

				wrapped.put(key, wrapper);
			}
		}

		return wrapped;
	}

	public List<Map<String, TaskDateWrapper>> wrapDates(List<Task> tasks) {
		List<Map<String, TaskDateWrapper>> wrapped = new ArrayList<Map<String, TaskDateWrapper>>();

		if (tasks != null) {
			TaskDateWrapper wrapper = null;
			Map<String, TaskDateWrapper> map = new HashMap<String, TaskDateWrapper>();
			Task previous = null;
			for (Task current : tasks) {

				if (previous != null && previous.equals(current)) {
					wrapper.addTask(current);
				} else {
					wrapper = new TaskDateWrapper(current);
					map = new HashMap<String, TaskDateWrapper>();
					map.put(current.getBeginDateAsString(), wrapper);

					wrapped.add(map);
				}

				previous = current;
			}
		}

		return wrapped;
	}

	public List<Task> filterTasksByDayMonthAndYear(int day, int month, int year) {
		List<Task> allTasks = getAllTasks();
		List<Task> filteredTasks = new ArrayList<Task>();

		for (Task t : allTasks) {
			if (t.getMonth() == month && t.getYear() == year && ((t.getDay() > 0 && t.getDay() == day) || day == 0)) {
				filteredTasks.add(t);
			}
		}

		return filteredTasks;
	}

	public TaskSet filterTasksByDescription(String desc) {
		TaskSet taskSet = this.dao.filterTasksByDesc(desc);
		return taskSet;
	}

	public void removeTasks(List<Task> tasks) {
		if (tasks != null && !tasks.isEmpty()) {
			for (Task t : tasks) {
				this.dao.removeTask(t);
			}
		}
	}

	public Set<String> getAllDescriptions() {
		return this.dao.getAllDescriptions();
	}

	public List<Task> filterTasksByActivity(String text) {
		return this.dao.filterTasksByActivity(text);
	}

	public List<Task> getAllTasks() {
		return this.dao.getAllTasks();
	}

	public List<Task> filterTasksByMonthAndYear(int month, int year) {
		List<Task> allTasks = getAllTasks();
		List<Task> filteredTasks = new ArrayList<Task>();

		for (Task t : allTasks) {
			if (t.getMonth() == month && t.getYear() == year) {
				filteredTasks.add(t);
			}
		}

		return filteredTasks;
	}

	public Task getLastTask() {
		return this.dao.getMostRecentTask();
	}

	public boolean exportDB2Txt(File target, String separator, String encoding) {
		boolean ok = false;
		List<Task> tasks = getAllTasks();

		if (Utils.isEmpty(separator)) {
			separator = Utils.DEFAULT_SEPARATOR;
		}

		StringBuilder content = new StringBuilder("");

		if (!Utils.isEmpty(tasks)) {

			content.append("data_inicio" + separator);
			content.append("hora_inicio" + separator);
			content.append("hora_fim" + separator);
			content.append("delta" + separator);
			content.append("atividade" + separator);
			content.append("descricao" + separator);
			content.append("classificacao" + separator);
			content.append("sistema\n");

			for (Task t : tasks) {

				content.append(String.valueOf(t.getBeginDateAsString()) + separator);
				content.append(String.valueOf(Utils.date2String(t.getBegin(), Utils.HH_mm_ss)) + separator);
				content.append(String.valueOf(Utils.date2String(t.getEnd(), Utils.HH_mm_ss)) + separator);
				content.append(String.valueOf(t.getDelta()) + separator);
				content.append(String.valueOf(t.getActivity()) + separator);
				content.append(String.valueOf(t.getDescription()) + separator);
				content.append(String.valueOf(t.getTaskClass()) + separator);
				content.append(String.valueOf(t.getSystem()));

				content.append("\n");
			}

			try {
				ok = FilesUtils.writeTxtFile(target, new String(content.toString().getBytes(), encoding));
			} catch (UnsupportedEncodingException e) {
				log.error("exportDB2Txt() " + e.getMessage());
				e.printStackTrace();
			}
		}

		return ok;
	}

	public boolean restoreDB(File fileToRestore) {
		boolean ok = false;
		String fn = String.valueOf(FilesUtils.DATABASE_FILE_V1) + "_backup_before_restore" + ".dbkp";
		FilesUtils.backupDataBase(fn);

		try {
			FileUtils.copyFile(fileToRestore, new File(FilesUtils.DATABASE_FILE_V1));
			ok = true;
		} catch (IOException e) {
			log.error("restoreDB() " + e.getMessage());
			e.printStackTrace();
		}

		return ok;
	}

	public String getTotalSpentOn(Task task) {
		String total = dao.countTasksByActivity(task.getActivity());
		return total;
	}

	public List<String> getAllTypeClassDesc() {
		Set<TypeClassification> allTypeClass = this.dao.getAllCachedTypeClassification();
		List<String> allTypeClassDescriptions = new ArrayList<String>();
		if (!Utils.isEmpty(allTypeClass)) {
			for (TypeClassification type : allTypeClass) {
				allTypeClassDescriptions.add(type.getDescription());
			}
		}
		return allTypeClassDescriptions;
	}

	public List<String> getAllSystemsNames() {
		Set<br.com.jpsp.model.System> allSystems = this.dao.getAllCachedSystems();
		List<String> allSystemsNames = new ArrayList<String>();
		if (!Utils.isEmpty(allSystems)) {
			for (br.com.jpsp.model.System sys : allSystems) {
				allSystemsNames.add(sys.getName());
			}
		}
		return allSystemsNames;
	}

	public Set<br.com.jpsp.model.System> getAllSystems() {
		Set<br.com.jpsp.model.System> allSystems = this.dao.getAllCachedSystems();
		return allSystems;
	}

	public void migrateDB(Refreshable r) {
		dao.migrateData(r);
	}
}
