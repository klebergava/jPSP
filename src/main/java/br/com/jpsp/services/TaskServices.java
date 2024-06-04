package br.com.jpsp.services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.LoadingScreen;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskActivityWrapper;
import br.com.jpsp.model.TaskDateWrapper;
import br.com.jpsp.model.TaskTypeWrapper;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class TaskServices extends RepositoryAccessServices implements CRUDServices<Task>  {
	private final static Logger log = LogManager.getLogger(TaskServices.class);
	public static final TaskServices instance = new TaskServices();

	private final ActivityServices activityServices = ActivityServices.instance;
	private final DescriptionServices descriptionServices = DescriptionServices.instance;
	private final TypeClassificationServices typeClassificationServices = TypeClassificationServices.instance;
	private final SystemServices systemServices = SystemServices.instance;

	private TaskServices() {
		super();
	}

	@Override
	public Set<Task> getAll() {
		return taskDAO.getAll();
	}

	@Override
	public void add(Task task) {
		synchronized (TaskServices.this) {
			taskDAO.add(task);
		}
	}

	@Override
	public void remove(Task task) throws Exception {
		synchronized (TaskServices.this) {
			taskDAO.removeTask(task);
		}
	}

	@Override
	public void update(Task task) throws Exception {
		synchronized (TaskServices.this) {
			taskDAO.updateTask(task);
		}
	}

	/**
	 *
	 * @param month
	 * @param year
	 * @return
	 */
	public List<Task> getTasksOfPeriod(int month, int year) {
		List<Task> tasksOfPeriod = new ArrayList<Task>();

		Set<Task> tasks = getAll();
		if (!Utils.isEmpty(tasks)) {
			tasks.forEach(task -> {
				if (task.getMonth() == month && task.getYear() == year) {
					tasksOfPeriod.add(task);
				}
			});
		}

		return tasksOfPeriod;
	}

	/**
	 *
	 * @param tasks
	 * @return
	 */
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

	/**
	 *
	 * @param tasks
	 * @return
	 */
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

	/**
	 *
	 * @param tasks
	 * @return
	 */
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

	public Set<Task> filterTasksByDescription(String desc) {
		Set<Task> taskSet = this.taskDAO.filterTasksByDesc(desc);
		return taskSet;
	}

	public void removeTasks(List<Task> tasks) {
		if (!Utils.isEmpty(tasks)) {
			synchronized (TaskServices.this) {
				for (Task t : tasks) {
					this.taskDAO.removeTask(t);
				}
			}
		}
	}

	public List<Task> filterTasksByActivity(String text) {
		return this.taskDAO.filterTasksByActivity(text);
	}

	public List<Task> getAllTasks() {
		return this.taskDAO.getAllTasks();
	}

	public List<Task> filterTasksByMonthAndYear(int month, int year) {
		List<Task> allTasks = getAllTasks();
		List<Task> filteredTasks = new ArrayList<Task>();

		for (Task t : allTasks) {
			if (t.getMonth() == month && t.getYear() == year) {
				filteredTasks.add(t);
			}
		}

		Collections.sort(filteredTasks, new Comparator<Task>() {

			@Override
			public int compare(Task o1, Task o2) {
				return o1.getBegin().compareTo(o2.getBegin());
			}

		});

		return filteredTasks;
	}

	public Task getLastTask() {
		return this.taskDAO.getMostRecent();
	}

	/**
	 *
	 * @param target
	 * @param separator
	 * @param encoding
	 * @param includeHeaders
	 * @return
	 */
	public boolean exportTasksDB2Txt(File target, String separator, String encoding, boolean includeHeaders) {
		boolean ok = false;

		synchronized (TaskServices.this) {

			List<Task> tasks = getAllTasks();

			if (Utils.isEmpty(separator)) {
				separator = Utils.DEFAULT_SEPARATOR;
			}

			StringBuilder content = new StringBuilder("");

			if (!Utils.isEmpty(tasks)) {

				if (includeHeaders) {
					content.append("data_inicio" + separator);
					content.append("hora_inicio" + separator);
					content.append("hora_fim" + separator);
					content.append("delta" + separator);
					content.append("atividade" + separator);
					content.append("descricao" + separator);
					content.append("classificacao" + separator);
					content.append("sistema\n");
				}

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
					log.error("exportTasksDB2Txt() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return ok;
	}

	/**
	 *
	 * @param target
	 * @param separator
	 * @param encoding
	 * @param includeHeaders
	 * @return
	 */
	public boolean exportTasksDB2Json(File target, String encoding) {
		boolean ok = false;

		synchronized (TaskServices.this) {

			List<Task> tasks = getAllTasks();

			StringBuilder content = new StringBuilder("");

			if (!Utils.isEmpty(tasks)) {

				content.append("[\n");
				for (Task t : tasks) {
					content.append(t.getJson() + ",\n");
				}
				content.append("]");

				try {
					ok = FilesUtils.writeTxtFile(target, new String(content.toString().getBytes(), encoding));
				} catch (UnsupportedEncodingException e) {
					log.error("exportTasksDB2Json() " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		return ok;
	}

	/**
	 *
	 * @param task
	 * @return
	 */
	public String getTotalSpentOn(Task task) {
		String total = taskDAO.countTasksByActivity(task.getActivity());
		return total;
	}

	/**
	 *
	 * @param sourceFile
	 * @param s
	 * @param enc
	 * @param hasHeaders
	 * @return
	 */
	public boolean importTasksFromTxt(File sourceFile, final String fieldSeparator, String encoding, boolean hasHeaders,
			boolean deleteAllData) {

		File dbBackupFile = FilesUtils.backupDataBase();

		boolean importOK = false;

		synchronized (TaskServices.this) {
			try {

				if (deleteAllData) {
					purgeDatabase();
				}

				List<String> lines = FilesUtils.readTxtFile(sourceFile, encoding);

				List<Task> tasksToInsert = new ArrayList<Task>();
				if (!Utils.isEmpty(lines)) {
					if (hasHeaders) {
						lines.remove(0);
					}

					final int[] total = {0};
					final LoadingScreen readingFile = GuiSingleton.showLoadingScreen(Strings.DBOptions.READING_FILE, false, 0, lines.size());

					final Set<String> activities = new TreeSet<String>();
					final Set<String> descriptions = new TreeSet<String>();
					final Set<String> systems = new TreeSet<String>();
					final Set<String> typeClass = new TreeSet<String>();

					lines.forEach(line -> {
						Task taskReadFromFile = readLine(line, fieldSeparator);
						if (taskReadFromFile != null) {
							tasksToInsert.add(taskReadFromFile);

							activities.add(taskReadFromFile.getActivity());
							descriptions.add(taskReadFromFile.getDescription());
							systems.add(taskReadFromFile.getDescription());
							typeClass.add(taskReadFromFile.getDescription());
						}

						total[0] = total[0] + 1;

						SwingUtilities.invokeLater(() -> {
							readingFile.updateProgressBar(total[0]);
						});

					});

					GuiSingleton.disposeLoadingScreen();

					if (!Utils.isEmpty(tasksToInsert)) {
						LoadingScreen importingData = GuiSingleton.showLoadingScreen(Strings.DBOptions.IMPORTING_DATA, false, 0, tasksToInsert.size());
						total[0] = 0;
						// ordena pela data
						Collections.sort(tasksToInsert);

						tasksToInsert.forEach(task -> {
							add(task);

							total[0] = total[0] + 1;

							SwingUtilities.invokeLater(() -> {
								importingData.updateProgressBar(total[0]);
							});
						});

						importOK = true;

						if (!Utils.isEmpty(activities)) {
							activityServices.addActivities(activities);
						}

						if (!Utils.isEmpty(descriptions)) {
							descriptionServices.addDescriptions(descriptions);
						}

						if (!Utils.isEmpty(systems)) {
							systemServices.addSystems(systems);
						}

						if (!Utils.isEmpty(typeClass)) {
							typeClassificationServices.addTypeClasses(typeClass);
						}

					}
				}
			} catch (Exception ex) {
				restoreDB(dbBackupFile);
				log.error("importDBFromTxt() " + ex.getMessage());
				ex.printStackTrace();
			} finally {
				GuiSingleton.disposeLoadingScreen();
			}
		}

		return importOK;
	}

	/**
	 *
	 * @param line
	 * @return
	 */
	private Task readLine(String line, String separator) {
		Task task = null;
		try {
			String[] fields = line.split(separator);
			if (!Utils.isEmpty(fields)) {
				task = new Task();
				Date date = Utils.string2Date(fields[0], Utils.DD_MM_YYYY);
				Date beginDate = getDateFromHour(date, fields[1]);
				Date endDate = getDateFromHour(date, fields[2]);
				task.setBegin(beginDate);
				task.setEnd(endDate);
				task.setDelta(Long.parseLong(fields[3]));
				task.setActivity(String.valueOf(fields[4]));
				task.setDescription(String.valueOf(fields[5]));
				task.setTaskClass(String.valueOf(fields[6]));
				if (fields.length == 8) {
					task.setSystem(fields[7]);
				}
			}
		} catch (Exception e) {
			log.error("readLine() " + e.getMessage());
			e.printStackTrace();
		}

		return task;
	}

	/**
	 *
	 * @param date
	 * @param hours
	 * @return
	 */
	private Date getDateFromHour(Date date, String time) {
		int[] timeSplit = getTimeSplit(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, timeSplit[0]); // Definindo as horas
		calendar.set(Calendar.MINUTE, timeSplit[1]); // Definindo os minutos (opcional)
		calendar.set(Calendar.SECOND, timeSplit[2]);

		return calendar.getTime();
	}

	private int[] getTimeSplit(String time) {
		String[] split = time.split("[:]");
		int[] timeSplit = {Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])};

		return timeSplit;
	}

}
