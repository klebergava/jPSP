package br.com.jpsp.model;

import br.com.jpsp.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class TaskValidation {
	public static List<String> validate(Task task) {
		List<String> errors = new ArrayList<String>();

		if (task != null) {
			if (task.getBegin() == null) {
				errors.add("Data de in�cio est� em branco");
			}

			if (task.getEnd() == null) {
				errors.add("Data de fim est� em branco");
			}

			if (task.getDelta() <= 0L) {
				errors.add("Delta possui um valor inv�lido");
			}

			if (Utils.isEmpty(task.getActivity())) {
				errors.add("A atividade est� em branco");
			}

			if (task.getTaskClass() == null) {
				errors.add("A classifica��o da tarefa est� em branco");
			}

			if (task.getBegin() != null && task.getEnd() != null && task.getBegin().after(task.getEnd())) {
				errors.add("Data de in�cio � posterior � data de fim");
			}
		}

		return errors;
	}

	public static List<String> validate(Task task, String extraInfo) {
		List<String> errors = new ArrayList<String>();

		if (task != null) {
			if (task.getBegin() == null) {
				errors.add("Data de in�cio est� em branco" + extraInfo);
			}

			if (task.getEnd() == null) {
				errors.add("Data de fim est� em branco" + extraInfo);
			}

			if (task.getDelta() <= 0L) {
				errors.add("Delta possui um valor inv�lido" + extraInfo);
			}

			if (Utils.isEmpty(task.getActivity())) {
				errors.add("A atividade est� em branco" + extraInfo);
			}

			if (task.getTaskClass() == null) {
				errors.add("A classifica��o da tarefa est� em branco" + extraInfo);
			}

			if (task.getBegin() != null && task.getEnd() != null && task.getBegin().after(task.getEnd())) {
				errors.add("Data de in�cio � posterior � data de fim" + extraInfo);
			}
		}

		return errors;
	}

	public static boolean isValid(Task task) {
		return validate(task).isEmpty();
	}
}
