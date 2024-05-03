package br.com.jpsp.model;

import br.com.jpsp.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class TaskValidation {
	public static List<String> validate(Task task) {
		List<String> errors = new ArrayList<String>();

		if (task != null) {
			if (task.getBegin() == null) {
				errors.add("Data de início está em branco");
			}

			if (task.getEnd() == null) {
				errors.add("Data de fim está em branco");
			}

			if (task.getDelta() <= 0L) {
				errors.add("Delta possui um valor inválido");
			}

			if (Utils.isEmpty(task.getActivity())) {
				errors.add("A atividade está em branco");
			}

			if (task.getTaskClass() == null) {
				errors.add("A classificação da tarefa está em branco");
			}

			if (task.getBegin() != null && task.getEnd() != null && task.getBegin().after(task.getEnd())) {
				errors.add("Data de início é posterior à data de fim");
			}
		}

		return errors;
	}

	public static List<String> validate(Task task, String extraInfo) {
		List<String> errors = new ArrayList<String>();

		if (task != null) {
			if (task.getBegin() == null) {
				errors.add("Data de início está em branco" + extraInfo);
			}

			if (task.getEnd() == null) {
				errors.add("Data de fim está em branco" + extraInfo);
			}

			if (task.getDelta() <= 0L) {
				errors.add("Delta possui um valor inválido" + extraInfo);
			}

			if (Utils.isEmpty(task.getActivity())) {
				errors.add("A atividade está em branco" + extraInfo);
			}

			if (task.getTaskClass() == null) {
				errors.add("A classificação da tarefa está em branco" + extraInfo);
			}

			if (task.getBegin() != null && task.getEnd() != null && task.getBegin().after(task.getEnd())) {
				errors.add("Data de início é posterior à data de fim" + extraInfo);
			}
		}

		return errors;
	}

	public static boolean isValid(Task task) {
		return validate(task).isEmpty();
	}
}
