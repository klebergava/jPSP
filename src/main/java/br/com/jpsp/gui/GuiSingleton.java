package br.com.jpsp.gui;

import java.awt.Color;
import java.util.Collection;
import java.util.List;

import javax.swing.SwingUtilities;

import br.com.jpsp.gui.database.DBOptions;
import br.com.jpsp.gui.database.ExportDB2Txt;
import br.com.jpsp.gui.database.RestoreDB;
import br.com.jpsp.gui.forms.BatchUpdateTask;
import br.com.jpsp.gui.forms.CRUDWindow;
import br.com.jpsp.gui.forms.ConfigWindow;
import br.com.jpsp.gui.forms.FieldToEdit;
import br.com.jpsp.gui.forms.IncludeOrUpdateTask;
import br.com.jpsp.gui.forms.MergeTasks;
import br.com.jpsp.gui.forms.ReportWindow;
import br.com.jpsp.gui.forms.SplitTasks;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Task;
import br.com.jpsp.services.ActivityServices;
import br.com.jpsp.services.DescriptionServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.SystemServices;
import br.com.jpsp.services.TypeClassificationServices;

public final class GuiSingleton {
	static About about;
	static ConfigWindow config;
	static ReportWindow report;
	static IncludeOrUpdateTask include;
	static IncludeOrUpdateTask edit;
	static BatchUpdateTask batchUpdateActivityDescription;
	static BatchUpdateTask batchUpdateClass;
	static BatchUpdateTask batchUpdateSystem;
	static SplitTasks branch;
	static MergeTasks merge;
	static ExportDB2Txt exportDB2Txt;
	static RestoreDB restoreDB;
	static DBOptions DBOptions;
	static Splash splash;
	static TotalSpentOnTask totalSpent;
	static LoadingScreen loading;
	
	public static Color DEFAULT_BG_COLOR = Color.DARK_GRAY; //new Color(47, 103, 253);

	public static void showAbout() {
		if (about == null || !about.isVisible()) {
			System.gc();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					GuiSingleton.about = new About();
					GuiSingleton.about.createAndShow();
				}
			});
		} else {
			about.toFront();
		}
	}

	public static void showReport() {
		if (report == null || !report.isVisible()) {
			System.gc();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					GuiSingleton.report = new ReportWindow();
					GuiSingleton.report.createAndShow();
				}
			});
		} else {
			report.toFront();
		}
	}

	public static void showConfiguration(jPSP jpsp) {
		if (config == null || !config.isVisible()) {
			config = new ConfigWindow(jpsp);
			config.createAndShow();
		} else {
			config.toFront();
		}
	}

	public static void showIncludeTask(Refreshable jpsp) {
		if (include == null || !include.isVisible()) {
			include = new IncludeOrUpdateTask(null, jpsp);
			include.createAndShow();
		} else {
			include.toFront();
		}
	}

	public static void showEditTask(Task task, Refreshable jpsp) {
		if (edit == null || !edit.isVisible()) {
			edit = new IncludeOrUpdateTask(task, jpsp);
			edit.createAndShow();
		} else {
			edit.toFront();
		}
	}

	public static void showBatchUpdateActivityDescription(Collection<Task> tasks, Refreshable refreshable) {
		if (batchUpdateActivityDescription == null || !batchUpdateActivityDescription.isVisible()) {
			batchUpdateActivityDescription = new BatchUpdateTask(tasks, Strings.BatchUpdateTask.TITLE_DESC, FieldToEdit.ACTIVITY_AND_DESC, refreshable);
			batchUpdateActivityDescription.createAndShow();
		} else {
			batchUpdateActivityDescription.toFront();
		}
	}

	public static void showBatchUpdateClass(Collection<Task> tasks, Refreshable refreshable) {
		if (batchUpdateClass == null || !batchUpdateClass.isVisible()) {
			batchUpdateClass = new BatchUpdateTask(tasks, Strings.BatchUpdateTask.TITLE_DESC, FieldToEdit.TYPE_CLASS, refreshable);
			batchUpdateClass.createAndShow();
		} else {
			batchUpdateClass.toFront();
		}
	}
	
	public static void showBatchUpdateSystem(Collection<Task> tasks, Refreshable refreshable) {
		if (batchUpdateSystem == null || !batchUpdateSystem.isVisible()) {
			batchUpdateSystem = new BatchUpdateTask(tasks, Strings.BatchUpdateTask.TITLE_SYSTEM, FieldToEdit.SYSTEM, refreshable);
			batchUpdateSystem.createAndShow();
		} else {
			batchUpdateSystem.toFront();
		}
	}

	public static void showBranch(Task task, Refreshable jpsp) {
		if (branch == null || !branch.isVisible()) {
			branch = new SplitTasks(task, jpsp);
			branch.createAndShow();
		} else {
			branch.toFront();
		}
	}

	public static void showMerge(List<Task> tasks, Refreshable refreshable) {
		if (merge == null || !merge.isVisible()) {
			merge = new MergeTasks(tasks, refreshable);
			merge.createAndShow();
		} else {
			merge.toFront();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void showEditActivities(Refreshable refreshable) {
		/*
		if (editActivities == null || !editActivities.isVisible()) {
			editActivities = new EditTasksNames(refreshable);
			editActivities.createAndShow();
		} else {
			editActivities.toFront();
		}
		*/
		CRUDWindow crud = new CRUDWindow(refreshable, Strings.jPSP.TASK_ACTIVITY, Images.TASK_IMG, ActivityServices.instance, new br.com.jpsp.model.Activity());
		crud.createAndShow();
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void showEditSystem(Refreshable refreshable) {
		CRUDWindow crud = new CRUDWindow(refreshable, Strings.jPSP.TASK_SYSTEM, Images.EDIT_SYSTEM_IMG, SystemServices.instance, new br.com.jpsp.model.System());
		crud.createAndShow();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void showEditTypeClass(Refreshable refreshable) {
		CRUDWindow crud = new CRUDWindow(refreshable, Strings.jPSP.TASK_CLASS, Images.EDIT_CLASS_IMG, TypeClassificationServices.instance, new br.com.jpsp.model.TypeClassification());
		crud.createAndShow();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void showEditDescription(Refreshable refreshable) {
		CRUDWindow crud = new CRUDWindow(refreshable, Strings.jPSP.TASK_DESCRIPTION, Images.DESCRIPTION_IMG, DescriptionServices.instance, new br.com.jpsp.model.Description());
		crud.createAndShow();
	}
	
	public static void showExportDB2Txt() {
		if (exportDB2Txt == null || !exportDB2Txt.isVisible()) {
			exportDB2Txt = new ExportDB2Txt();
			exportDB2Txt.createAndShow();
		} else {
			exportDB2Txt.toFront();
		}
	}

	public static void showRestoreDB() {
		if (restoreDB == null || !restoreDB.isVisible()) {
			restoreDB = new RestoreDB();
			restoreDB.createAndShow();
		} else {
			restoreDB.toFront();
		}
	}

	public static void showDBOptions() {
		if (DBOptions == null || !DBOptions.isVisible()) {
			DBOptions = new DBOptions();
			DBOptions.createAndShow();
		} else {
			DBOptions.toFront();
		}
	}

	public static void showSplash() {
		splash = new Splash();
		splash.createAndShow();
	}

	public static void closeSplash() {
		splash.setVisible(false);
		splash.dispose();
		splash = null;
	}
	
	
	public static void showTotalSpent(Task task) {
		if (totalSpent == null || !totalSpent.isVisible()) {
			totalSpent = new TotalSpentOnTask(task);
			totalSpent.createAndShow();
		} else {
			totalSpent.toFront();
		}
	}
	
	public static void showLoadingScreen(String txt) {
		if (loading == null || !loading.isVisible()) {
			SwingUtilities.invokeLater(() -> {
				loading = new LoadingScreen(txt);
				loading.createAndShow();
			});

		} else {
			loading.toFront();
		}
	}
	
	public static void disposeLoadingScreen() {
		if (loading != null && loading.isVisible()) {
			SwingUtilities.invokeLater(() -> {
				loading.dispose();
				loading = null;
			});

		}
	}
	
}
