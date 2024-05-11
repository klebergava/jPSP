package br.com.jpsp.model;

import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TaskValidation {
	public static List<String> validate(Task task) {
		List<String> errors = new ArrayList<String>();

		if (task != null) {
			if (task.getBegin() == null) {
				errors.add(Strings.SplitTasks.ERROR_START_DATE_REQUIRED);
			}

			if (task.getEnd() == null) {
				errors.add(Strings.SplitTasks.ERROR_END_DATE_REQUIRED);
			}

			if (task.getDelta() <= 0L) {
				errors.add(Strings.SplitTasks.ERROR_INVALID_DELTA);
			}

			if (Utils.isEmpty(task.getActivity())) {
				errors.add(Strings.SplitTasks.ERROR_ACTIVITY_REQUIRED);
			}

			if (task.getTaskClass() == null) {
				errors.add(Strings.SplitTasks.ERROR_TASKCLASS_REQUIRED);
			}

			if (task.getBegin() != null && task.getEnd() != null && task.getBegin().after(task.getEnd())) {
				errors.add(Strings.SplitTasks.ERROR_INVALID_DATES);
			}
		}

		return errors;
	}

	/**
	 *
	 * @param task
	 * @param extraInfo
	 * @return
	 */
	public static List<String> validate(Task task, String extraInfo) {
		List<String> errors = new ArrayList<String>();

		if (task != null) {
			if (task.getBegin() == null) {
				errors.add(Strings.SplitTasks.ERROR_START_DATE_REQUIRED + ": " + extraInfo);
			}

			if (task.getEnd() == null) {
				errors.add(Strings.SplitTasks.ERROR_END_DATE_REQUIRED + ": " + extraInfo);
			}

			if (task.getDelta() <= 0L) {
				errors.add(Strings.SplitTasks.ERROR_INVALID_DELTA + ": " + extraInfo);
			}

			if (Utils.isEmpty(task.getActivity())) {
				errors.add(Strings.SplitTasks.ERROR_ACTIVITY_REQUIRED + ": " + extraInfo);
			}

			if (task.getTaskClass() == null) {
				errors.add(Strings.SplitTasks.ERROR_TASKCLASS_REQUIRED + ": " + extraInfo);
			}

			if (task.getBegin() != null && task.getEnd() != null && task.getBegin().after(task.getEnd())) {
				errors.add(Strings.SplitTasks.ERROR_INVALID_DATES + ": " + extraInfo);
			}
		}

		return errors;
	}

	public static boolean isValid(Task task) {
		return validate(task).isEmpty();
	}
}
