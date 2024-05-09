package br.com.jpsp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.jna.Callback;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.Wtsapi32;

import br.com.jpsp.gui.database.DBOptions;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Activity;
import br.com.jpsp.model.Configuration;
import br.com.jpsp.model.Description;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskListTableModel;
import br.com.jpsp.services.ActivityServices;
import br.com.jpsp.services.ConfigServices;
import br.com.jpsp.services.DescriptionServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskSetServices;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class jPSP extends JFrame implements WindowListener, Refreshable, MouseListener, WinUser.WindowProc, Callback {
	private static final long serialVersionUID = 5345992610620020749L;

	private final static Logger log = LogManager.getLogger(jPSP.class);

	private static final Dimension BUTTON_DIMENSION = new Dimension(120, 50);
	private static final Dimension SQUARE_BUTTON_DIMENSION = new Dimension(50, 50);
	private static final Dimension COMBOBOX_SIZE = new Dimension(100, 50);
	private static final Dimension COMBOBOX_MIN_SIZE = new Dimension(80, 50);
	private JButton startPauseButton;
	private JButton todayButton;
	private JLabel begin;
	private JLabel chronometer;
	private JLabel sumHours;
	private JLabel intervals;
	private JTextField sumTotalHours;
	private JTextField start;
	private JTextField end;
	private JTextField allIntervals;
	private JComboBox<String> taskDescription;
	private JComboBox<String> taskActivity;
	private JComboBox<String> taskClass;
	private JComboBox<String> system;
	private Task task;
	private final TaskSetServices services = TaskSetServices.instance;
	private final ActivityServices activityServices = ActivityServices.instance;
	private final ConfigServices configServices = ConfigServices.instance;
	private final DescriptionServices descriptionServices = DescriptionServices.instance;

	private List<Task> tasks;

	private JPanel centerTable;

	private JLabel dayOfWeekLabel;
	private JLabel todayDateLabel;

	private JComboBox<String> days;

	private JComboBox<String> months;
	private JComboBox<Integer> years;
	private JButton dateFilterButton;
	private JButton activityFilterButton;
	private JComboBox<String> activity;
	private final ButtonGroup group = new ButtonGroup();

	private Thread clockThread;
	private boolean isClockRunning;
	private TaskSetServices.Order orderType;
	private JTable table = new JTable();

	private boolean autoPause = true;
	private boolean autoStart = false;
	private boolean clockWasRunning = false;

	private Alert alert;

	private Date taskEnd;
	private Thread thread;
	private transient boolean updateDateThreadRunning;
	private JButton generateReportButton;

	public jPSP() {
		super(Strings.jPSP.TITLE + " (" + Utils.date2String(new Date(), Utils.DD_MM_YYYY) + ")");
		log.trace("Starting jPSP app");
		Gui.setLookAndFeel();
		this.orderType = TaskSetServices.Order.ASC;

		setJNA();
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.setIconImage(Images.SPLASH_IMAGE);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mountMain(), "Center");

		addWindowListener(this);

		createMenuBar();

		loadConfigurationFromFile();

		if (this.autoStart) {
			reStartLastTask();
		}

		setSize((int) (Utils.SCREEN_WIDTH - (Utils.SCREEN_WIDTH*.25)), (int) (Utils.SCREEN_HEIGHT - Utils.SCREEN_HEIGHT*.25));

		setLocationRelativeTo(this);

		setVisible(true);
		setMaximized();

		GuiSingleton.closeSplash();

		startUpdateDateThread();

		this.services.migrateDB(this);

		log.trace("jPSP app started");
	}

	private void setMaximized() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		this.setMaximizedBounds(env.getMaximumWindowBounds());
		this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);

	}

	private void loadConfigurationFromFile() {
		Configuration config = this.configServices.getConfiguration();
		Gui.setLookAndFeel(config.getLookAndFeel(), this);
		updateMenu(config.getLookAndFeel());
		this.autoPause = config.isAutoPause();
		this.autoStart = config.isAutoStart();

		if (!Utils.isEmpty(config.getAlertTime())) {
			String alertTime = config.getAlertTime().replaceAll(":", "");
			if (Utils.isNumber(alertTime)) {

				if (this.alert != null) {
					this.alert.stop();
					this.alert = null;
				}

				this.alert = new Alert(config.getAlertTime());
				this.alert.start();
			}
		}

		if (config.getCombosValues() != null && config.getCombosValues().length == 4) {
			Object[] persistedValues = config.getCombosValues();

			if (persistedValues[0] != null)
				taskActivity.setSelectedItem(persistedValues[0]);

			if (persistedValues[1] != null)
				taskDescription.setSelectedItem(persistedValues[1]);

			if (persistedValues[2] != null)
				taskClass.setSelectedItem(persistedValues[2]);

			if (persistedValues[3] != null)
				system.setSelectedItem(persistedValues[3]);
		}


	}

	private void updateMenu(String selectedLAF) {
		Enumeration<AbstractButton> buttons = this.group.getElements();
		while (buttons.hasMoreElements()) {
			AbstractButton b = buttons.nextElement();
			if (selectedLAF.equals(b.getText())) {
				b.setSelected(true);
				break;
			}
		}
	}

	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu editMenu = new JMenu(Strings.jPSP.EDIT);

		JMenu editItem = new JMenu(Strings.jPSP.CRUD);
		editItem.setIcon(Images.CRUD);

		JMenuItem editActivities = new JMenuItem(Strings.jPSP.EDIT_TASKS);
		editActivities.setIcon(Images.TASK);
		editActivities.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showEditActivities(jPSP.this);
			}
		});
		editItem.add(editActivities);

		JMenuItem editDesc = new JMenuItem(Strings.jPSP.EDIT_DESCS);
		editDesc.setIcon(Images.DESCRIPTION);
		editDesc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showEditDescription(jPSP.this);
			}
		});
		editItem.add(editDesc);

		JMenuItem editTaskClass = new JMenuItem(Strings.jPSP.EDIT_TASK_CLASS);
		editTaskClass.setIcon(Images.EDIT_CLASS);
		editTaskClass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showEditTypeClass(jPSP.this);
			}
		});
		editItem.add(editTaskClass);

		JMenuItem editSystem = new JMenuItem(Strings.jPSP.EDIT_SYSTEMS);
		editSystem.setIcon(Images.EDIT_SYSTEM);
		editSystem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showEditSystem(jPSP.this);
			}
		});
		editItem.add(editSystem);

		editMenu.add(editItem);
		editMenu.addSeparator();

		JMenuItem edit = new JMenuItem(Strings.jPSP.INCLUDE);
		edit.setIcon(Images.ADD);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showIncludeTask(jPSP.this);
			}
		});
		editMenu.add(edit);

		edit = new JMenuItem(Strings.jPSP.EDIT);
		edit.setIcon(Images.EDIT);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jPSP.this.table != null && Gui.getSelectedQty(jPSP.this.table) == 1) {
					Task task = Gui.getSelectedTask(jPSP.this.table);
					if (task != null) {
						GuiSingleton.showEditTask(task, jPSP.this);
					}
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_1_TASK);
				}
			}
		});
		editMenu.add(edit);

		edit = new JMenuItem(Strings.jPSP.BATCH_EDIT_TASK_AND_DESC);
		edit.setIcon(Images.EDIT_ACTIVITY_AND_DESC);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jPSP.this.table != null && Gui.getSelectedQty(jPSP.this.table) > 1) {
					List<Task> toBatchEdit = Gui.getSelectedTasks(jPSP.this.table);
					GuiSingleton.showBatchUpdateActivityDescription(toBatchEdit, jPSP.this);
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_2_OR_MORE_TASKS);
				}
			}
		});
		editMenu.add(edit);

		edit = new JMenuItem(Strings.jPSP.BATCH_EDIT_CLASSIFICATION);
		edit.setIcon(Images.EDIT_CLASS);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jPSP.this.table != null && Gui.getSelectedQty(jPSP.this.table) > 1) {
					List<Task> toBatchEdit = Gui.getSelectedTasks(jPSP.this.table);
					GuiSingleton.showBatchUpdateSystem(toBatchEdit, jPSP.this);
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_2_OR_MORE_TASKS);
				}
			}
		});
		editMenu.add(edit);

		edit = new JMenuItem(Strings.jPSP.BATCH_EDIT_SYSTEM);
		edit.setIcon(Images.EDIT_SYSTEM);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jPSP.this.table != null && Gui.getSelectedQty(jPSP.this.table) > 1) {
					List<Task> toBatchEdit = Gui.getSelectedTasks(jPSP.this.table);
					GuiSingleton.showBatchUpdateClass(toBatchEdit, jPSP.this);
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_2_OR_MORE_TASKS);
				}
			}
		});
		editMenu.add(edit);

		edit = new JMenuItem(Strings.jPSP.TASK_SPENT_TIME);
		edit.setIcon(Images.CHRONOMETER);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Gui.getSelectedQty(jPSP.this.table) > 1) {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_1_TASK);
				} else if (Gui.getSelectedQty(jPSP.this.table) <= 0) {
					Gui.showErrorMessage(jPSP.this, Strings.Form.ERROR_SELECT_ITEM);
				} else if (Gui.getSelectedQty(jPSP.this.table) == 1) {
					Task toCalculate = Gui.getSelectedTask(jPSP.this.table);
					if (toCalculate != null) {
						GuiSingleton.showTotalSpent(toCalculate);
					}
				}
			}
		});
		editMenu.add(edit);

		edit = new JMenuItem(Strings.jPSP.CONTINUE_THIS_TASK);
		edit.setIcon(Images.START);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jPSP.this.table != null && Gui.getSelectedQty(jPSP.this.table) == 1) {
					Task task = Gui.getSelectedTask(jPSP.this.table);
					if (task != null) {
						jPSP.this.doContinue(task);
					}
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_1_TASK);
				}
			}
		});
		editMenu.add(edit);

		editMenu.addSeparator();

		edit = new JMenuItem(Strings.jPSP.SPLIT);
		edit.setIcon(Images.SPLIT);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jPSP.this.table != null && Gui.getSelectedQty(jPSP.this.table) == 1) {
					Task task = Gui.getSelectedTask(jPSP.this.table);
					GuiSingleton.showBranch(task, jPSP.this);
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_1_TASK);
				}
			}
		});
		editMenu.add(edit);

		edit = new JMenuItem(Strings.jPSP.MERGE);
		edit.setIcon(Images.MERGE);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jPSP.this.table != null && Gui.getSelectedQty(jPSP.this.table) > 1) {
					List<Task> tasks = Gui.getSelectedTasks(jPSP.this.table);
					GuiSingleton.showMerge(tasks, jPSP.this);
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_2_OR_MORE_TASKS);
				}
			}
		});
		editMenu.add(edit);

		editMenu.addSeparator();

		edit = new JMenuItem(Strings.jPSP.EXCLUDE);
		edit.setIcon(Images.DEL);
		edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Gui.getSelectedQty(jPSP.this.table) == 1) {
					Task toRemove = Gui.getSelectedTask(jPSP.this.table);
					if (toRemove != null) {
						String message = Strings.jPSP.CONFIRM_TASK_EXCLUSION.replaceAll("&1", toRemove.getActivity());
						int answer = JOptionPane.showConfirmDialog(null, message, Strings.GUI.CONFIRM_ACTION, 0);
						if (answer == 0) {
							jPSP.this.services.removeTask(toRemove);
						}
					}
				} else if (Gui.getSelectedQty(jPSP.this.table) > 1) {
					int answer = JOptionPane.showConfirmDialog(null, Strings.jPSP.CONFIRM_TASK_LIST_EXCLUSION,
							Strings.GUI.CONFIRM_ACTION, 0);
					if (answer == 0) {

						int rowModel = 0;

						TaskListTableModel model = (TaskListTableModel) jPSP.this.table.getModel();
						byte b;
						int i, arrayOfInt[];
						for (i = (arrayOfInt = jPSP.this.table.getSelectedRows()).length, b = 0; b < i;) {
							int r = arrayOfInt[b];
							rowModel = jPSP.this.table.convertRowIndexToModel(r);
							Task toRemove = model.get(rowModel);
							jPSP.this.services.removeTask(toRemove);
							b++;
						}

					}
				} else {
					Gui.showErrorMessage(jPSP.this, Strings.jPSP.SELECT_1_OR_MORE_TASKS);
				}

				jPSP.this.refresh();
			}
		});
		editMenu.add(edit);

		menuBar.add(editMenu);

		JMenu configMenu = new JMenu(Strings.jPSP.OPTIONS);
		configMenu.getAccessibleContext().setAccessibleDescription(Strings.jPSP.OPTIONS);
		menuBar.add(configMenu);

		JMenuItem config = new JMenuItem(Strings.jPSP.CONFIGURATIONS);
		config.setIcon(Images.CONFIG);
		config.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showConfiguration(jPSP.this);
			}
		});

		configMenu.add(config);

		JMenuItem dbOptions = new JMenuItem(Strings.jPSP.DATA_BASE);
		dbOptions.setIcon(Images.DATABASE);
		dbOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBOptions dbOptions = new DBOptions(jPSP.this);
				dbOptions.createAndShow();
			}
		});

		configMenu.add(dbOptions);

		configMenu.addSeparator();
		JMenuItem generateReport = new JMenuItem(Strings.jPSP.REPORT);
		generateReport.setIcon(Images.REPORT_MINI);
		generateReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showReport();
			}
		});

		configMenu.add(generateReport);

		configMenu.addSeparator();

		List<String> laf = Gui.getAvailableLookAndFeel();

		String selectedLaf = Gui.getSelectedLookAndFeel();

		for (String name : laf) {
			JRadioButtonMenuItem rbMenuItem = new JRadioButtonMenuItem(name);

			if (selectedLaf.equals(name)) {
				rbMenuItem.setSelected(true);
			}

			rbMenuItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					Enumeration<AbstractButton> buttons = jPSP.this.group.getElements();
					while (buttons.hasMoreElements()) {
						AbstractButton b = buttons.nextElement();
						if (b.isSelected()) {
							Gui.setLookAndFeel(b.getText(), jPSP.this);
							Configuration c = jPSP.this.configServices.getConfiguration();
							c.setLookAndFeel(b.getText());
							jPSP.this.configServices.updateConfiguration(c);
						}
					}
				}
			});

			this.group.add(rbMenuItem);
			configMenu.add(rbMenuItem);
		}

		JMenu about = new JMenu(Strings.ABOUT);

		JMenuItem aboutjPSP = new JMenuItem(Strings.jPSP.ABOUT, Images.ABOUT);
		aboutjPSP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showAbout();
			}
		});

		about.add(aboutjPSP);

		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(about);

		setJMenuBar(menuBar);
	}

	private void updateTableByDate(int day, int month, int year) {
		this.tasks = this.services.getAllTasks();
		if (this.tasks != null) {
			this.centerTable.removeAll();

			if (month >= 0) {
				this.tasks = filterTasksByDayAndMonth(day, month, year);
			}
			updateTable();
		}
	}

	private void updateTable() {
		TaskListTableModel model = new TaskListTableModel(this.tasks, this.orderType);
		this.table = new JTable();
		this.table.setSelectionMode(2);
		this.table.setAutoCreateRowSorter(true);
		ListSelectionModel selectionModel = this.table.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				jPSP.this.handleSelectionEvent(e);
			}
		});

		this.table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3 && e.getSource() instanceof JTable) {
					JTable table = (JTable) e.getSource();
					PopupMenu menu = new PopupMenu(table, jPSP.this);
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		this.table.setModel((TableModel) model);

		this.table.setAutoCreateRowSorter(true);
		this.table.setRowHeight(50);
		this.table.setRowSelectionAllowed(true);
		this.table.setUpdateSelectionOnSort(true);
		Enumeration<TableColumn> headers = this.table.getColumnModel().getColumns();
		TableColumn col = null;
		while (headers.hasMoreElements()) {
			col = headers.nextElement();
			col.setPreferredWidth(150);
		}

		this.table.setDefaultRenderer(Object.class, new TaskRenderer());

		JScrollPane scroll = Gui.getDefaultScroll(this.table);
		this.centerTable.add(scroll, "Center");

		this.centerTable.validate();

		if (this.sumHours != null) {
			this.sumHours.setText("   00:00:00   ");
		}

		if (this.intervals != null) {
			this.intervals.setText("");
		}

		updateTotals();
	}

	protected void handleSelectionEvent(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			return;
		}
		if (this.table != null) {
			int modelRow = 0;
			TaskListTableModel model = (TaskListTableModel) this.table.getModel();
			Task currentTask = null, previousTask = null;
			long delta = 0L, intervalBetweenTasks = 0L;
			StringBuffer interval = new StringBuffer("");
			byte b;
			int j, arrayOfInt[];
			for (j = (arrayOfInt = this.table.getSelectedRows()).length, b = 0; b < j;) {
				int k = arrayOfInt[b];

				modelRow = this.table.convertRowIndexToModel(k);
				currentTask = model.get(modelRow);
				delta += currentTask.getDelta();

				if (previousTask != null) {
					intervalBetweenTasks = Utils.getInterval(previousTask, currentTask);
					interval.append(";" + Utils.getMinutesByDelta(intervalBetweenTasks));
				}

				previousTask = currentTask;
				b++;
			}

			this.sumHours.setText(Utils.getTimeByDelta(delta));
			String i = (interval.length() < 2) ? "" : interval.toString().substring(1);
			this.intervals.setText(i);
		}
	}

	private List<Task> filterTasksByDayAndMonth(int day, int month, int year) {
		return this.services.filterTasksByDayMonthAndYear(day, month, year);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());

		main.setBorder(Gui.getEmptyBorder(5));

		this.todayButton = new JButton(Strings.TODAY);
		this.todayButton.setPreferredSize(COMBOBOX_MIN_SIZE);
		this.todayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jPSP.this.setTodayDate();
			}
		});

		this.taskClass = Gui.createTypeClassCombo();
		this.taskClass.addMouseListener(this);
		this.taskClass.setSelectedIndex(0);
		this.taskClass.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jPSP.this.taskClass.setBorder(BorderFactory.createEmptyBorder());
			}
		});

		this.system = Gui.createSystemsCombo();
		this.system.addMouseListener(this);
		this.system.setSelectedIndex(0);
		this.system.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jPSP.this.system.setBorder(BorderFactory.createEmptyBorder());
			}
		});

		this.chronometer = new JLabel("   00:00:00   ", 10);

		List<String> tasks = this.activityServices.getAllActivitiesDescriptions();
		Set<String> descs = this.services.getAllDescriptions();

		this.taskActivity = new JComboBox<String>(tasks.toArray(new String[tasks.size()]));
		this.taskActivity.setFont(Gui.getFont(0, Integer.valueOf(14)));
		this.taskActivity.setEditable(true);
		this.taskActivity.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jPSP.this.taskActivity.setBorder(BorderFactory.createEmptyBorder());
			}
		});

		this.taskDescription = new JComboBox<String>(descs.toArray(new String[descs.size()]));
		this.taskDescription.setFont(Gui.getFont(0, Integer.valueOf(14)));
		this.taskDescription.setEditable(true);
		this.taskDescription.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				jPSP.this.taskDescription.setBorder(BorderFactory.createEmptyBorder());
			}
		});

		this.startPauseButton = new JButton(Strings.START, Images.START);
		this.startPauseButton.setPreferredSize(BUTTON_DIMENSION);

		this.startPauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jPSP.this.startPause();
			}
		});

		JPanel activityFields = new JPanel(new SpringLayout());
		activityFields.setBorder(Gui.getEmptyBorder(5));

		activityFields.add(new JLabel(Strings.jPSP.TASK_TYPE));
		activityFields.add(new JLabel(Strings.jPSP.TASK_DESC));
		activityFields.add(new JLabel(Strings.jPSP.TASK_CLASSIFICATION));
		activityFields.add(new JLabel(Strings.jPSP.TASK_SYSTEM));
		activityFields.add(new JLabel(Strings.jPSP.TASK_INIT_PAUSE));

		activityFields.add(this.taskActivity);
		activityFields.add(this.taskDescription);
		activityFields.add(this.taskClass);
		activityFields.add(this.system);
		activityFields.add(this.startPauseButton);
		activityFields.setBorder(Gui.getEmptyBorder(15));
		Gui.makeCompactGrid(activityFields, 2, 5, 0, 0, 2, 2);

		this.months = Gui.createMonthsCombo();
		this.months.setPreferredSize(COMBOBOX_SIZE);
		this.months.setSelectedIndex(Utils.getCurrentMonth());
		this.months.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jPSP.this.reloadDaysCombo();
			}
		});

		this.days = new JComboBox<String>();
		reloadDaysCombo();
		this.days.setPreferredSize(COMBOBOX_MIN_SIZE);

		this.dateFilterButton = new JButton(Images.SEARCH);
		this.dateFilterButton.setPreferredSize(SQUARE_BUTTON_DIMENSION);
		this.dateFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jPSP.this.updateTableByDate(jPSP.this.getSelectedDay().intValue(), jPSP.this.months.getSelectedIndex(),
						jPSP.this.getYear());
			}
		});

		this.years = new JComboBox<Integer>();
		Gui.loadYearsComboUntilCurrent(2010, this.years);
		this.years.setPreferredSize(COMBOBOX_MIN_SIZE);
		this.years.addKeyListener(Gui.getKeyListenerDigitsOnly());

		this.activity = new JComboBox<String>(tasks.toArray(new String[tasks.size()]));
		this.activity.setPreferredSize(new Dimension(350, 50));
		this.activity.setEditable(true);
		this.activity.setFont(Gui.getFont(0, Integer.valueOf(14)));
		this.activity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jPSP.this.updateTableByActivity();
			}
		});

		this.activityFilterButton = new JButton(Images.SEARCH);
		this.activityFilterButton.setPreferredSize(SQUARE_BUTTON_DIMENSION);
		this.activityFilterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jPSP.this.updateTableByActivity();
			}
		});

		JPanel filtersPanel = new JPanel(new SpringLayout());
		filtersPanel.setBorder(Gui.getTitledBorder(Strings.jPSP.SEARCH_LABEL, Gui.getTitledBorderFont(), Color.BLUE));

		JPanel dateFilterOptions = new JPanel(new FlowLayout()); // new SpringLayout());

		JPanel dateParamsPanel = new JPanel(new SpringLayout());

		dateParamsPanel.add(this.days);
		dateParamsPanel.add(this.months);
		dateParamsPanel.add(this.years);

		Gui.makeCompactGrid(dateParamsPanel, 1, 3, 1, 1, 5, 5);

		dateFilterOptions.add(dateParamsPanel);

		dateFilterOptions.add(this.dateFilterButton);
		dateFilterOptions.add(this.todayButton);

		dateFilterOptions.setBorder(Gui.getTitledBorder(Strings.jPSP.BY_DATE, null, null));

		filtersPanel.add(dateFilterOptions);

		JPanel textFilterOptions = new JPanel(new FlowLayout()); // new SpringLayout());
		textFilterOptions.setBorder(Gui.getTitledBorder(Strings.jPSP.BY_TASK, null, null));
		textFilterOptions.add(this.activity);
		textFilterOptions.add(this.activityFilterButton);
		filtersPanel.add(textFilterOptions);

		Gui.makeCompactGrid(filtersPanel, 1, 2, 1, 1, 5, 5);

		JPanel info = new JPanel(new SpringLayout());

		Date today = new Date();
		JPanel todayInfo = new JPanel(new GridLayout(2, 1));
		todayInfo.setBorder(Gui.getTitledBorder(Strings.TODAY, Gui.getTitledBorderFont(), Color.BLUE));

		dayOfWeekLabel = new JLabel(Utils.dayOfWeek(today));

		dayOfWeekLabel.setFont(Gui.getFont(1, Integer.valueOf(16)));
		dayOfWeekLabel.setForeground(Color.DARK_GRAY);
		todayInfo.add(dayOfWeekLabel);

		todayDateLabel = new JLabel(Utils.date2String(today, "dd/MM/yyyy"));

		todayDateLabel.setFont(Gui.getFont(1, Integer.valueOf(16)));
		todayDateLabel.setForeground(Color.DARK_GRAY);
		todayInfo.add(todayDateLabel);

		info.add(todayInfo);

		JPanel summary = new JPanel(new SpringLayout());
		summary.setPreferredSize(new Dimension(200, 200));
		summary.setBorder(Gui.getTitledBorder(Strings.jPSP.DAY_SUMMARY, Gui.getTitledBorderFont(), Color.BLUE));
		summary.add(new JLabel(Strings.jPSP.START + ": "));
		this.start = new JTextField("   00:00   ");
		this.start.setEditable(false);
		this.start.setFont(Gui.getFont(1, Integer.valueOf(12)));
		summary.add(this.start);

		summary.add(new JLabel(Strings.jPSP.END + ": "));
		this.end = new JTextField("   00:00   ");
		this.end.setEditable(false);
		this.end.setFont(Gui.getFont(1, Integer.valueOf(12)));
		summary.add(this.end);

		summary.add(new JLabel(Strings.jPSP.HOURS_TOTAL + ": "));
		this.sumTotalHours = new JTextField("   00:00   ");
		this.sumTotalHours.setEditable(false);
		this.sumTotalHours.setFont(Gui.getFont(1, Integer.valueOf(12)));
		summary.add(this.sumTotalHours);

		summary.add(new JLabel(Strings.jPSP.INTERVALS + ": "));
		this.allIntervals = new JTextField("");
		this.allIntervals.setEditable(false);
		this.allIntervals.setFont(Gui.getFont(1, Integer.valueOf(12)));
		summary.add(this.allIntervals);

		Gui.makeCompactGrid(summary, 4, 2, 5, 5, 5, 5);

		JPanel summaryInfo = new JPanel(new BorderLayout());
		summaryInfo.add(summary, "Center");

		info.add(summaryInfo);

		Gui.makeCompactGrid(info, 2, 1, 5, 5, 5, 5);

		this.begin = new JLabel(Strings.jPSP.NO_TASK_IN_PROGRESS);
		this.begin.setPreferredSize(new Dimension(600, 55));
		this.begin.setSize(new Dimension(600, 55));
		this.begin.setFont(Gui.getFont(0, Integer.valueOf(14)));
		this.begin.setBorder(Gui.getTitledBorder(Strings.jPSP.IN_PROGRESS, Gui.getTitledBorderFont(), Color.BLUE));

		this.chronometer.addMouseListener(this);
		this.chronometer.setPreferredSize(new Dimension(150, 55));
		this.chronometer.setSize(150, 55);
		this.chronometer.setFont(Gui.getFont(1, Integer.valueOf(16)));
		this.chronometer.setBackground(Color.WHITE);
		this.chronometer
				.setBorder(Gui.getTitledBorder(Strings.jPSP.CHRONOMETER, Gui.getTitledBorderFont(), Color.BLUE));

		generateReportButton = new JButton(Strings.jPSP.REPORT);
		generateReportButton.setPreferredSize(BUTTON_DIMENSION);
		generateReportButton.setIcon(Images.REPORT_MINI);
		generateReportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showReport();
			}
		});

		JPanel inExecution = new JPanel(new BorderLayout());
		inExecution.add(this.chronometer, "West");
		inExecution.add(this.begin, "Center");
		inExecution.add(generateReportButton, "East");

		JPanel north = new JPanel(new BorderLayout());
		north.add(info, "East");

		JPanel infoPlusFilters = new JPanel(new SpringLayout());
		infoPlusFilters.add(inExecution, "North");
		infoPlusFilters.add(activityFields, "Center");
		infoPlusFilters.add(filtersPanel, "South");
		Gui.makeCompactGrid(infoPlusFilters, 3, 1, 5, 5, 5, 5);

		north.add(infoPlusFilters, "Center");

		JPanel bottom = new JPanel(new SpringLayout());
		bottom.setBorder(Gui.getTitledBorder(Strings.jPSP.SELECTION, null, null));
		this.sumHours = new JLabel("   00:00   ");
		this.sumHours.setFont(Gui.getFont(1, Integer.valueOf(12)));
		bottom.add(new JLabel(Strings.jPSP.HOURS_TOTAL + ": "));
		bottom.add(this.sumHours);

		bottom.add(new JLabel("      |      "));

		this.intervals = new JLabel("");
		this.intervals.setFont(Gui.getFont(1, Integer.valueOf(12)));
		bottom.add(new JLabel(Strings.jPSP.INTERVALS + ": "));
		bottom.add(this.intervals);

		Gui.makeCompactGrid(bottom, 1, 5, 5, 5, 5, 5);

		JPanel south = new JPanel(new BorderLayout());
		south.add(bottom, "South");

		this.centerTable = new JPanel(new BorderLayout());
		updateTableByDate(Utils.getCurrentDay(), Utils.getCurrentMonth(), Utils.getCurrentYear());

		main.add(north, "North");
		main.add(this.centerTable, "Center");
		main.add(south, "South");

		return main;
	}

	private void updateTotals() {
		if (this.table != null) {
			int modelRow = 0;
			TaskListTableModel model = (TaskListTableModel) this.table.getModel();

			if (!model.isEmpty()) {

				Task currentTask = null, previousTask = null;
				long delta = 0L, intervalBetweenTasks = 0L;
				StringBuffer interval = new StringBuffer("");
				for (int j = 0; j < this.table.getRowCount(); j++) {

					modelRow = this.table.convertRowIndexToModel(j);
					currentTask = model.get(modelRow);
					delta += currentTask.getDelta();

					if (previousTask != null) {
						intervalBetweenTasks = Utils.getInterval(previousTask, currentTask);
						interval.append(";" + Utils.getMinutesByDelta(intervalBetweenTasks));
					}

					previousTask = currentTask;
				}
				this.sumTotalHours.setText(Utils.getTimeByDelta(delta, false));
				String i = (interval.length() < 2) ? "" : interval.toString().substring(1);
				this.allIntervals.setText(i);

				modelRow = this.table.convertRowIndexToModel(0);
				currentTask = model.get(modelRow);
				this.start.setText(Utils.date2String(currentTask.getBegin(), "HH:mm"));
				this.start.repaint();

				modelRow = this.table.convertRowIndexToModel(this.table.getRowCount() - 1);
				currentTask = model.get(modelRow);
				this.end.setText(Utils.date2String(currentTask.getEnd(), "HH:mm"));
				this.end.repaint();
			}
		}
	}

	private int getYear() {
		int year = (new Integer(this.years.getSelectedItem().toString())).intValue();
		return year;
	}

	protected void updateTableByActivity() {
		this.tasks = this.services.getAllTasks();
		if (this.tasks != null) {
			if (this.activity.getSelectedItem() != null && !Utils.isEmpty(this.activity.getSelectedItem().toString())) {
				this.tasks = filterTasksByActivity(this.activity.getSelectedItem().toString());
				this.centerTable.removeAll();
				updateTable();
			}
		}
	}

	private List<Task> filterTasksByActivity(String text) {
		return this.services.filterTasksByActivity(text);
	}

	private void startPause() {
		if (this.startPauseButton.getText().equals(Strings.START)) {
			if (validateFields()) {
				startTask();
			}
		} else if (this.startPauseButton.getText().equals(Strings.PAUSE)) {
			pauseTask();
		}
	}

	private void pauseTask() {
		if (this.task != null) {
			this.task.setEnd(new Date());
			this.services.addTask(this.task);
			this.taskEnd = this.task.getEnd();
			this.task = null;
			updateTableByDate(getSelectedDay().intValue(), this.months.getSelectedIndex(), getYear());
			enableButtons(true);
		}
		resetCounterClock();
	}

	private void startTask() {
		if (this.task != null) {
			pauseTask();
		}

		this.task = new Task();
		this.task.setBegin(new Date());

		String activity = this.taskActivity.getSelectedItem().toString();
		this.task.setActivity(activity);

		if (this.taskDescription.getSelectedItem() != null) {
			String desc = this.taskDescription.getSelectedItem().toString();
			this.task.setDescription(desc);
			this.descriptionServices.add(new Description(desc));
		}

		String taskClassification = this.taskClass.getSelectedItem().toString();
		this.task.setTaskClass(taskClassification);

		String sys = this.system.getSelectedItem().toString();
		this.task.setSystem(sys);

		enableButtons(false);

		Activity newActivity = new Activity(activity, Activity.UNBLOCKED);
		this.activityServices.add(newActivity);

		startCounterClock();

		persistCombosValues();
	}

	private void enableButtons(boolean enable) {
		this.taskDescription.setEnabled(enable);
		this.taskActivity.setEnabled(enable);
		this.taskClass.setEnabled(enable);
		this.system.setEnabled(enable);

		if (enable) {

			if (this.taskActivity.getSelectedItem() != null) {
				this.begin.setText(Strings.jPSP.NO_TASK_IN_PROGRESS + " (" + Strings.jPSP.LAST_TASK + ": "
						+ Utils.date2String(this.taskEnd, Utils.DD_MM_YYYY_HH_mm_ss) + " - "
						+ this.taskActivity.getSelectedItem().toString() + ")");
				this.begin.setForeground(Color.RED);
			}

			this.startPauseButton.setText(Strings.START);
			this.startPauseButton.setIcon(Images.START);
		} else {

			String message = Strings.jPSP.TASK_STARTED_AT.replaceAll("&1",
					Utils.date2String(this.task.getBegin(), Utils.DD_MM_YYYY_HH_mm_ss));
			message = message.replaceAll("&2", this.taskActivity.getSelectedItem().toString());

			this.begin.setText(message);
			this.begin.setForeground(Color.BLUE);
			this.startPauseButton.setText(Strings.PAUSE);
			this.startPauseButton.setIcon(Images.PAUSE);
		}
	}

	private void startCounterClock() {
		if (this.clockThread != null) {
			resetCounterClock();
		}

		this.clockThread = new Thread() {
			public void run() {
				int countGC = 0;
				jPSP.this.isClockRunning = true;
				long initClockMillis = System.currentTimeMillis();
				long delta = Utils.getDeltaByTime(jPSP.this.chronometer.getText());
				jPSP.this.chronometer.setForeground(Color.BLUE);
				while (jPSP.this.isClockRunning) {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException interruptedException) {
						log.info("startCounterClock() " + interruptedException.getMessage());
					}

					delta = System.currentTimeMillis() - initClockMillis;
					jPSP.this.chronometer.setText("   " + Utils.getTimeByDelta(delta) + "   ");
					countGC++;

					if (countGC > 300) {
						countGC = 0;
						System.gc();
					}
				}
				jPSP.this.chronometer.setForeground(Color.RED);
			}
		};
		this.clockThread.start();
	}

	private void resetCounterClock() {
		if (this.clockThread != null) {
			this.isClockRunning = false;
			try {
				this.clockThread.interrupt();
			} catch (Exception exception) {
				log.info("resetCounterClock() " + exception.getMessage());
			}
			this.clockThread = null;
			this.chronometer.setText("   00:00:00   ");
			this.chronometer.setForeground(Color.RED);

			this.clockWasRunning = true;
		} else {
			this.clockWasRunning = false;
		}
	}

	private boolean validateFields() {

		List<String> mandatoryFields = new ArrayList<String>();

		if (this.taskActivity.getSelectedItem() == null
				|| Utils.isEmpty(this.taskActivity.getSelectedItem().toString())) {
			mandatoryFields.add(Strings.jPSP.TASK_TYPE);
			this.taskActivity.setBorder(BorderFactory.createLineBorder(Color.RED));
		}

		if (this.taskDescription.getSelectedItem() == null
				|| Utils.isEmpty(this.taskDescription.getSelectedItem().toString())) {
			mandatoryFields.add(Strings.jPSP.TASK_DESC);
			this.taskDescription.setBorder(BorderFactory.createLineBorder(Color.RED));
		}

		if (this.taskClass.getSelectedItem() == null
				|| Utils.isEmpty(this.taskClass.getSelectedItem().toString())) {
			mandatoryFields.add(Strings.jPSP.TASK_CLASS);
			this.taskClass.setBorder(BorderFactory.createLineBorder(Color.RED));
		}

		if (this.system.getSelectedItem() == null
				|| Utils.isEmpty(this.system.getSelectedItem().toString())) {
			mandatoryFields.add(Strings.jPSP.TASK_SYSTEM);
			this.system.setBorder(BorderFactory.createLineBorder(Color.RED));
		}

		if (!Utils.isEmpty(mandatoryFields)) {
			StringBuilder sb = new StringBuilder(Strings.Form.FILL_MANDATORY_FIELDS);
			for (String m : mandatoryFields) {
				sb.append("\n* " + m);
			}
			JOptionPane.showMessageDialog(this, sb.toString(), Strings.Form.MANDATORY_FIELDS, 0);
		}

		this.taskActivity.setBorder(BorderFactory.createEmptyBorder());
		this.taskDescription.setBorder(BorderFactory.createEmptyBorder());
		this.taskClass.setBorder(BorderFactory.createEmptyBorder());
		this.system.setBorder(BorderFactory.createEmptyBorder());

		return Utils.isEmpty(mandatoryFields);
	}

	private void reloadDaysCombo() {
		int maxDay = 30;

		int month = this.months.getSelectedIndex();
		switch (month) {
		case 1:
			maxDay = 29;
			break;
		case 0:
		case 2:
		case 4:
		case 6:
		case 7:
		case 9:
		case 11:
			maxDay = 31;
			break;
		}
		this.days.removeAllItems();
		this.days.addItem("");
		for (int i = 1; i <= maxDay; i++) {
			this.days.addItem(Integer.toString(i));
		}
		this.days.setSelectedItem(Integer.toString(Utils.getCurrentDay()));
	}

	private Object[] getComboxValues() {
		Object[] values = new Object[4];
		values[0] = taskActivity.getSelectedItem();
		values[1] = taskDescription.getSelectedItem();
		values[2] = taskClass.getSelectedItem();
		values[3] = system.getSelectedItem();
		return values;
	}

	public void refresh() {
		updateTableByDate(getSelectedDay().intValue(), this.months.getSelectedIndex(), getYear());
		loadConfigurationFromFile();

		// salar info antes do refresh
		Object[] valuesBefore = this.getComboxValues();

		List<String> tasks = this.activityServices.getAllActivitiesDescriptions();
		Set<String> descs = this.services.getAllDescriptions();

		taskDescription.removeAllItems();
		for (String desc : descs) {
			taskDescription.addItem(desc);
		}

		taskActivity.removeAllItems();
		for (String t : tasks) {
			taskActivity.addItem(t);
		}

		List<String> tcdesc = this.services.getAllTypeClassDesc();
		taskClass.removeAllItems();
		for (String t : tcdesc) {
			taskClass.addItem(t);
		}
		taskClass.setSelectedIndex(0);

		List<String> sys = this.services.getAllSystemsNames();
		system.removeAllItems();
		for (String s : sys) {
			system.addItem(s);
		}
		system.setSelectedIndex(0);

		activity.removeAllItems();
		for (String t : tasks) {
			activity.addItem(t);
		}

		taskActivity.setSelectedItem(valuesBefore[0]);
		taskDescription.setSelectedItem(valuesBefore[1]);
		taskClass.setSelectedItem(valuesBefore[2]);
		system.setSelectedItem(valuesBefore[3]);
	}

	private void setTodayDate() {
		this.months.setSelectedIndex(Utils.getCurrentMonth());
		this.days.setSelectedItem(Integer.toString(Utils.getCurrentDay()));
		this.years.setSelectedItem(new Integer(Utils.getCurrentYear()));
		updateTableByDate(getSelectedDay().intValue(), this.months.getSelectedIndex(), getYear());
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {

		persistCombosValues();

		if (this.task != null) {
			int answer = JOptionPane.showConfirmDialog(null, Strings.jPSP.TASK_IN_PROGRESS_CONFIRM,
					Strings.GUI.CONFIRM_ACTION, 0, 3);
			if (answer == 0) {
				pauseTask();
				dispose();
			}
		} else {
			dispose();
		}
	}

	/**
	 *
	 */
	private void persistCombosValues() {
		Configuration config = this.configServices.getConfiguration();
		config.setCombosValues(this.getComboxValues());
		this.configServices.updateConfiguration(config);
	}

	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	private Integer getSelectedDay() {
		Integer day = new Integer(0);
		String selected = this.days.getSelectedItem().toString();
		if (!Utils.isEmpty(selected)) {
			day = new Integer(selected);
		}
		return day;
	}

	class TaskRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public TaskRenderer() {
			setOpaque(false);
		}

		public Component getTableCellRendererComponent(JTable tableSource, Object value, boolean isSelected,
				boolean hasFocus, int row, int col) {
			int modelRow = tableSource.convertRowIndexToModel(row);

			TaskRenderer c = (TaskRenderer) super.getTableCellRendererComponent(tableSource, value, isSelected,
					hasFocus, modelRow, col);
			c.setOpaque(true);

			TaskListTableModel model = (TaskListTableModel) tableSource.getModel();
			Task task = model.get(modelRow);

			setVisual(task, c);

			return c;
		}

		private void setVisual(Task task, TaskRenderer c) {
			Color color = Color.BLACK;
			Font font = Gui.getFont(0, Integer.valueOf(13));

			int dayOfWeek = task.getDayOfWeek();
			switch (dayOfWeek) {
			case 1:
			case 7:
				color = Color.BLUE;
				font = Gui.getFont(1, Integer.valueOf(13));
				break;
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				color = Color.BLACK;
				break;
			}
			c.setFont(font);
			c.setForeground(color);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public WinDef.LRESULT callback(WinDef.HWND hwnd, int uMsg, WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
		switch (uMsg) {
		case 2:
			User32.INSTANCE.PostQuitMessage(0);
			return new WinDef.LRESULT(0L);

		case 689:
			onSessionChange(wParam, lParam);
			return new WinDef.LRESULT(0L);
		}

		return User32.INSTANCE.DefWindowProc(hwnd, uMsg, wParam, lParam);
	}

	private void setJNA() {
		log.trace("Starting JNA thread");
		Runnable r = new Runnable() {

			public void run() {
				try {
					String windowClass = new String("MyWindowClass");
					WinDef.HMODULE hInst = Kernel32.INSTANCE.GetModuleHandle("");

					WinUser.WNDCLASSEX wClass = new WinUser.WNDCLASSEX();
					wClass.hInstance = (WinDef.HINSTANCE) hInst;
					wClass.lpfnWndProc = (Callback) jPSP.this;
					wClass.lpszClassName = windowClass;

					User32.INSTANCE.RegisterClassEx(wClass);
					jPSP.this.getLastError();

					WinDef.HWND hWnd = User32.INSTANCE.CreateWindowEx(8, windowClass,
							"'TimeTracker hidden helper window to catch Windows events", 0, 0, 0, 0, 0, null,

							null, (WinDef.HINSTANCE) hInst, null);

					jPSP.this.getLastError();

					Wtsapi32.INSTANCE.WTSRegisterSessionNotification(hWnd, 0);

					WinUser.MSG msg = new WinUser.MSG();
					while (User32.INSTANCE.GetMessage(msg, hWnd, 0, 0) != 0) {
						User32.INSTANCE.TranslateMessage(msg);
						User32.INSTANCE.DispatchMessage(msg);
					}

					Wtsapi32.INSTANCE.WTSUnRegisterSessionNotification(hWnd);
					User32.INSTANCE.UnregisterClass(windowClass, (WinDef.HINSTANCE) hInst);
					User32.INSTANCE.DestroyWindow(hWnd);
				} catch (Exception exception) {
				}
			}
		};

		Thread t = new Thread(r);
		t.start();
	}

	public int getLastError() {
		int rc = Kernel32.INSTANCE.GetLastError();

		return rc;
	}

	protected void onSessionChange(WinDef.WPARAM wParam, WinDef.LPARAM lParam) {
		switch (wParam.intValue()) {
		case 7:
			onMachineLocked(lParam.intValue());
			break;

		case 8:
			onMachineUnlocked(lParam.intValue());
			break;
		}
	}

	protected void onMachineLocked(int sessionId) {
		if (this.autoPause) {
			pauseTask();
		}
	}

	protected void onMachineUnlocked(int sessionId) {
		if (this.autoPause && this.clockWasRunning) {
			startPause();
		}
	}

	public void doContinue(Task task) {
		if (task != null) {
			if (this.isClockRunning) {
				String message = Strings.jPSP.TASK_ALREADY_IN_PROGRESS.replaceAll("&1", task.getActivity());
				message = message.replaceAll("&2", this.taskActivity.getSelectedItem().toString());

				JOptionPane.showMessageDialog(this, message, Strings.jPSP.TASK_IN_PROGRESS, 0);
			} else {
				this.taskActivity.setSelectedItem(task.getActivity());
				this.taskDescription.setSelectedItem(task.getDescription());
				this.taskClass.setSelectedItem(task.getTaskClass());
				this.system.setSelectedItem(task.getSystem());
				startPause();
			}
		}
	}

	private void reStartLastTask() {
		Task lastTask = this.services.getLastTask();
		doContinue(lastTask);
	}

	private void startUpdateDateThread() {
		log.trace("Starting update date thread");
		this.thread = new Thread() {
			public void run() {

				while (jPSP.this.updateDateThreadRunning) {
					try {
						Thread.sleep(1800000L); // 30 minutos
					} catch (InterruptedException interruptedException) {
						log.info("startUpdateDateThread() " + interruptedException.getMessage());
					}

					final Date now = new Date();
					jPSP.this.setTitle(Strings.jPSP.TITLE + " (" + Utils.date2String(now, "dd/MM/yyyy") + ")");
					dayOfWeekLabel.setText(Utils.dayOfWeek(now));
					todayDateLabel.setText(Utils.date2String(now, "dd/MM/yyyy"));
				}
			}
		};

		this.updateDateThreadRunning = true;
		this.thread.start();
	}

	public boolean isClockWasRunning() {
		return clockWasRunning;
	}

	public void setClockWasRunning(boolean clockWasRunning) {
		this.clockWasRunning = clockWasRunning;
	}

	public boolean isAutoPause() {
		return autoPause;
	}

	public void setAutoPause(boolean autoPause) {
		this.autoPause = autoPause;
	}

}
