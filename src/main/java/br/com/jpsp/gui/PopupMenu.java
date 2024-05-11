package br.com.jpsp.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskListTableModel;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskSetServices;
import br.com.jpsp.utils.Gui;

public class PopupMenu extends JPopupMenu {
	private static final long serialVersionUID = 6879620017103017314L;
	private final JTable tableSource;
	private final TaskSetServices services = TaskSetServices.instance;
	private final Refreshable refreshable;

	public PopupMenu(JTable table, Refreshable refreshable) {
		this.tableSource = table;
		this.refreshable = refreshable;
		create();
	}

	private void create() {
		final int selected = Gui.getSelectedQty(this.tableSource);

		JMenuItem item = new JMenuItem("Incluir");
		item.setIcon(Images.ADD);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showIncludeTask(PopupMenu.this.refreshable);
			}
		});
		add(item);

		item = new JMenuItem("Editar");
		item.setIcon(Images.EDIT);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					Task task = Gui.getSelectedTask(PopupMenu.this.tableSource);
					if (task != null) {
						GuiSingleton.showEditTask(task, PopupMenu.this.refreshable);
					}
				}
			}
		});

		if (selected != 1) {
			item.setEnabled(false);
		}
		add(item);

		item = new JMenuItem(Strings.jPSP.BATCH_EDIT_TASK_AND_DESC);
		item.setIcon(Images.EDIT_ACTIVITY_AND_DESC);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					List<Task> toBatchEdit = Gui.getSelectedTasks(PopupMenu.this.tableSource);
					GuiSingleton.showBatchUpdateActivityDescription(toBatchEdit, PopupMenu.this.refreshable);
				}
			}
		});

		if (selected <= 1) {
			item.setEnabled(false);
		}
		add(item);

		item = new JMenuItem(Strings.jPSP.BATCH_EDIT_CLASSIFICATION);
		item.setIcon(Images.EDIT_CLASS);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					List<Task> toBatchEdit = Gui.getSelectedTasks(PopupMenu.this.tableSource);
					GuiSingleton.showBatchUpdateClass(toBatchEdit, PopupMenu.this.refreshable);
				}
			}
		});

		if (selected <= 1) {
			item.setEnabled(false);
		}
		add(item);


		item = new JMenuItem(Strings.jPSP.BATCH_EDIT_SYSTEM);
		item.setIcon(Images.EDIT_SYSTEM);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					List<Task> toBatchEdit = Gui.getSelectedTasks(PopupMenu.this.tableSource);
					GuiSingleton.showBatchUpdateSystem(toBatchEdit, PopupMenu.this.refreshable);
				}
			}
		});
		if (selected <= 1) {
			item.setEnabled(false);
		}
		add(item);


		item = new JMenuItem(Strings.jPSP.TASK_SPENT_TIME);
		item.setIcon(Images.CHRONOMETER);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					Task task = Gui.getSelectedTask(PopupMenu.this.tableSource);
					if (task != null) {
						GuiSingleton.showTotalSpent(task);
					}
				}
			}
		});
		if (selected <= 1) {
			item.setEnabled(true);
		} else {
			item.setEnabled(false);
		}
		add(item);


		item = new JMenuItem(Strings.jPSP.CONTINUE_THIS_TASK);
		item.setIcon(Images.START);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					Task task = Gui.getSelectedTask(PopupMenu.this.tableSource);
					if (task != null) {
						PopupMenu.this.refreshable.doContinue(task);
					}
				}
			}
		});

		if (selected <= 1) {
			item.setEnabled(true);
		} else {
			item.setEnabled(false);
		}
		add(item);

		addSeparator();

		item = new JMenuItem(Strings.jPSP.SPLIT);
		item.setIcon(Images.SPLIT);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					Task task = Gui.getSelectedTask(PopupMenu.this.tableSource);
					GuiSingleton.showBranch(task, PopupMenu.this.refreshable);
				}
			}
		});

		if (selected > 1) {
			item.setEnabled(false);
		}
		add(item);

		item = new JMenuItem(Strings.jPSP.MERGE);
		item.setIcon(Images.MERGE);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (PopupMenu.this.tableSource != null) {
					List<Task> tasks = Gui.getSelectedTasks(PopupMenu.this.tableSource);
					GuiSingleton.showMerge(tasks, PopupMenu.this.refreshable);
				}
			}
		});

		if (selected <= 1) {
			item.setEnabled(false);
		}
		add(item);

		addSeparator();

		item = new JMenuItem(Strings.jPSP.EXCLUDE);
		item.setIcon(Images.DEL);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (selected == 1) {
					Task toRemove = Gui.getSelectedTask(PopupMenu.this.tableSource);
					if (toRemove != null) {
						String message = Strings.jPSP.CONFIRM_TASK_EXCLUSION.replaceAll("&1", toRemove.getActivity());
						int answer = JOptionPane.showConfirmDialog(null,
								message, Strings.GUI.CONFIRM_ACTION,
								JOptionPane.OK_CANCEL_OPTION);
						if (answer == JOptionPane.OK_OPTION) {
							PopupMenu.this.services.removeTask(toRemove);
						}
					}
				} else if (selected > 1) {
					int answer = JOptionPane.showConfirmDialog(null, Strings.jPSP.CONFIRM_TASK_LIST_EXCLUSION,
							Strings.GUI.CONFIRM_ACTION, JOptionPane.OK_CANCEL_OPTION);
					if (answer == JOptionPane.OK_OPTION) {

						int rowModel = 0;

						TaskListTableModel model = (TaskListTableModel) PopupMenu.this.tableSource.getModel();
						byte b;
						int i, arrayOfInt[];
						for (i = (arrayOfInt = PopupMenu.this.tableSource.getSelectedRows()).length, b = 0; b < i;) {
							int r = arrayOfInt[b];
							rowModel = PopupMenu.this.tableSource.convertRowIndexToModel(r);
							Task toRemove = model.get(rowModel);
							PopupMenu.this.services.removeTask(toRemove);
							b++;
						}

					}
				}
				if (PopupMenu.this.refreshable != null)
					PopupMenu.this.refreshable.refresh();
			}
		});
		if (selected == 0) {
			item.setEnabled(false);
		}
		add(item);
	}
}
