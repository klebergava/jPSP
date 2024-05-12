package br.com.jpsp.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.table.AbstractTableModel;

import br.com.jpsp.services.OrderByDirection;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.Utils;

public class TaskListTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -8453557333049893234L;
	protected List<Task> taskList;
	private final String[] COLS = new String[] { Strings.jPSP.COL_DATE, Strings.jPSP.COL_WEEK_DAY, Strings.jPSP.START, Strings.jPSP.END, Strings.jPSP.COL_DELTA, Strings.jPSP.TASK_ACTIVITY,
			Strings.jPSP.TASK_DESCRIPTION, Strings.jPSP.TASK_CLASSIFICATION, Strings.jPSP.TASK_SYSTEM };

	public TaskListTableModel(List<Task> tasks, OrderByDirection orderType) {
		this.taskList = new ArrayList<Task>();
		this.taskList.addAll(tasks);
		if (orderType.isDESC()) {
			Collections.reverse(this.taskList);
		}
	}

	public String getColumnName(int num) {
		return this.COLS[num];
	}

	public int getRowCount() {
		return this.taskList.size();
	}

	public int getColumnCount() {
		return this.COLS.length;
	}

	public boolean isCellEditable(int linha, int coluna) {
		return false;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		long delta;
		Object obj = null;
		Task t = this.taskList.get(rowIndex);
		switch (columnIndex) {
			case 0:
				obj = Utils.date2String(t.getBegin(), "dd/MM/yyyy");
				break;
			case 1:
				obj = (new SimpleDateFormat("EEEE", new Locale("pt"))).format(Long.valueOf(t.getBegin().getTime()));
				break;
			case 2:
				obj = Utils.date2String(t.getBegin(), "HH:mm:ss");
				break;
			case 3:
				obj = Utils.date2String(t.getEnd(), "HH:mm:ss");
				break;
			case 4:
				delta = t.getDelta();
				obj = Utils.getTimeByDelta(delta);
				break;
			case 5:
				obj = t.getActivity();
				break;
			case 6:
				obj = (t.getDescription() == null) ? "" : t.getDescription();
				break;
			case 7:
				obj = t.getTaskClass();
				break;
			case 8:
				obj = t.getSystem();
		}
		return obj;
	}

	public Task get(int row) {
		return this.taskList.get(row);
	}

	public List<Task> getAllTasks() {
		return this.taskList;
	}

	public boolean isEmpty() {
		return !(this.taskList != null && !this.taskList.isEmpty());
	}
}
