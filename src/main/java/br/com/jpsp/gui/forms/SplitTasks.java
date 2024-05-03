package br.com.jpsp.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import br.com.jpsp.gui.DateSpinner;
import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.Refreshable;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Activity;
import br.com.jpsp.model.Description;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskValidation;
import br.com.jpsp.services.ActivityServices;
import br.com.jpsp.services.DescriptionServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskSetServices;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

public class SplitTasks extends JFrame implements Refreshable, WindowListener {
	private static final long serialVersionUID = -103651380043899625L;
	private JSpinner beginTask1;
	private JSpinner endTask1;
	private JTextField deltaTask1;
	private JComboBox<String> activityTask1;
	private JComboBox<String> descriptionTask1;
	private JComboBox<String> taskClassTask1;
	private JComboBox<String> systemTask1;
	private JSpinner beginTask2;
	private JSpinner endTask2;
	private JTextField deltaTask2;
	private JComboBox<String> activityTask2;
	private JComboBox<String> descriptionTask2;
	private JComboBox<String> taskClassTask2;
	private JComboBox<String> systemTask2;
	private final Refreshable refreshable;
	
	private final TaskSetServices services = TaskSetServices.instance;
	private final ActivityServices activityServices = ActivityServices.instance;
	private final DescriptionServices descriptionServices = DescriptionServices.instance;

	private Task originalTask;
	private Task task1 = new Task();
	private Task task2 = new Task();

	public SplitTasks(Task originalTask, Refreshable refreshable) {
		super(Strings.SplitTasks.TITLE);
		this.setIconImage(Images.SPLIT_IMG);
		this.originalTask = originalTask;
		this.refreshable = refreshable;
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(mountMain(), "Center");

//		setUndecorated(true);
		this.setIconImage(Images.SPLIT_IMG);
		setAlwaysOnTop(true);

		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(Gui.getLinedBorder(Strings.SplitTasks.TITLE, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));
		main.setBackground(GuiSingleton.DEFAULT_BG_COLOR);

		JPanel fields = new JPanel(new SpringLayout());

		JPanel task2Panel = new JPanel(new SpringLayout());
		task2Panel.setBorder(Gui.getTitledBorder(Strings.SplitTasks.TASK_2, Gui.getFont(1, Integer.valueOf(13)), Color.BLUE));

		this.endTask2 = DateSpinner.createSpinner(null, null, null, Gui.getFont(0, Integer.valueOf(12)), this);
		this.endTask2.setEnabled(false);

		this.beginTask2 = DateSpinner.createSpinner(null, null, null, Gui.getFont(0, Integer.valueOf(12)), this);
		this.beginTask2.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				SplitTasks.this.calculateDelta2();
			}

			public void focusLost(FocusEvent e) {
				SplitTasks.this.calculateDelta2();
			}
		});

		List<String> tasks = this.activityServices.getAllActivitiesDescriptions();
		Set<String> descs = this.services.getAllDescriptions();
		
		this.deltaTask2 = new JTextField("");
		this.deltaTask2.setEditable(false);

		this.activityTask2 = new JComboBox<String>(tasks.toArray(new String[tasks.size()]));
		this.activityTask2.setEditable(true);

		this.descriptionTask2 = new JComboBox<String>(descs.toArray(new String[descs.size()]));
		this.descriptionTask2.setEditable(true);

		this.taskClassTask2 = Gui.createTypeClassCombo();
		
		this.systemTask2 = Gui.createSystemsCombo();
		
		task2Panel.add(new JLabel(Strings.Form.START));
		task2Panel.add(this.beginTask2);
		
		task2Panel.add(new JLabel(Strings.Form.END));
		task2Panel.add(this.endTask2);
		
		task2Panel.add(new JLabel(Strings.Form.DELTA));
		task2Panel.add(this.deltaTask2);
		
		task2Panel.add(new JLabel(Strings.Form.TASK));
		task2Panel.add(this.activityTask2);
		
		task2Panel.add(new JLabel(Strings.Form.DESCRIPTION));
		task2Panel.add(this.descriptionTask2);
		
		task2Panel.add(new JLabel(Strings.Form.CLASSIFICATION));
		task2Panel.add(this.taskClassTask2);
		
		task2Panel.add(new JLabel(Strings.Form.SYSTEM));
		task2Panel.add(this.systemTask2);

		Gui.makeCompactGrid(task2Panel, 7, 2, 5, 5, 5, 5);

		JPanel task1Panel = new JPanel(new SpringLayout());
		task1Panel.setBorder(Gui.getTitledBorder(Strings.SplitTasks.TASK_1, Gui.getFont(1, Integer.valueOf(13)), Color.BLUE));
		this.endTask1 = DateSpinner.createSpinner(null, null, null, Gui.getFont(0, Integer.valueOf(12)), this);
		this.endTask1.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				SplitTasks.this.calculateDelta1();
			}

			public void focusLost(FocusEvent e) {
				SplitTasks.this.calculateDelta1();
			}
		});

		this.beginTask1 = DateSpinner.createSpinner(this.beginTask2.getModel(), null, null,
				Gui.getFont(0, Integer.valueOf(12)), this);
		this.beginTask1.setEnabled(false);

		this.deltaTask1 = new JTextField("");
		this.deltaTask1.setEditable(false);

		this.activityTask1 = new JComboBox<String>(tasks.toArray(new String[tasks.size()]));
		this.activityTask1.setEditable(true);

		this.descriptionTask1 = new JComboBox<String>(descs.toArray(new String[descs.size()]));
		this.descriptionTask1.setEditable(true);

		this.taskClassTask1 = Gui.createTypeClassCombo();
		
		this.systemTask1 = Gui.createSystemsCombo();

		task1Panel.add(new JLabel(Strings.Form.START));
		task1Panel.add(this.beginTask1);
		
		task1Panel.add(new JLabel(Strings.Form.END));
		task1Panel.add(this.endTask1);
		
		task1Panel.add(new JLabel(Strings.Form.DELTA));
		task1Panel.add(this.deltaTask1);
		
		task1Panel.add(new JLabel(Strings.Form.TASK));
		task1Panel.add(this.activityTask1);
		
		task1Panel.add(new JLabel(Strings.Form.DESCRIPTION));
		task1Panel.add(this.descriptionTask1);
		
		task1Panel.add(new JLabel(Strings.Form.CLASSIFICATION));
		task1Panel.add(this.taskClassTask1);		
		
		task1Panel.add(new JLabel(Strings.Form.SYSTEM));
		task1Panel.add(this.systemTask1);

		Gui.makeCompactGrid(task1Panel, 7, 2, 5, 5, 5, 5);

		fields.add(task1Panel);
		fields.add(task2Panel);

		Gui.makeCompactGrid(fields, 1, 2, 5, 5, 5, 5);

		loadTask();

		JPanel buttons = new JPanel(new BorderLayout());

		JButton button = new JButton(Strings.GUI.CANCEL);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SplitTasks.this.dispose();
			}
		});

		buttons.add(button, "West");

		button = new JButton(Strings.GUI.CONFIRM);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SplitTasks.this.save();
			}
		});

		buttons.add(button, "East");

		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.setBorder(Gui.getEmptyBorder(5));
		mainPanel.add(fields);
		mainPanel.add(buttons);

		Gui.makeCompactGrid(mainPanel, 2, 1, 5, 5, 5, 5);

		main.add(mainPanel, "Center");

		return main;
	}

	private void loadTask() {
		long diffDelta = (this.originalTask.getEnd().getTime() - this.originalTask.getBegin().getTime()) / 2L;
		diffDelta = this.originalTask.getEnd().getTime() - diffDelta;

		this.beginTask1.setValue(this.originalTask.getBegin());
		this.endTask1.setValue(new Date(diffDelta - 1000L));
		this.activityTask1.setSelectedItem(this.originalTask.getActivity());
		this.descriptionTask1.setSelectedItem(this.originalTask.getDescription());
		this.taskClassTask1.setSelectedItem(this.originalTask.getTaskClass());
		this.systemTask1.setSelectedItem(this.originalTask.getSystem());

		this.beginTask2.setValue(new Date(diffDelta + 1000L));
		this.endTask2.setValue(this.originalTask.getEnd());
		this.activityTask2.setSelectedItem(this.originalTask.getActivity());
		this.descriptionTask2.setSelectedItem(this.originalTask.getDescription());
		this.taskClassTask2.setSelectedItem(this.originalTask.getTaskClass());
		this.systemTask2.setSelectedItem(this.originalTask.getSystem());

		refresh();
	}

	private void save() {
		fillTask();
		List<String> errors = new ArrayList<String>();
		errors.addAll(TaskValidation.validate(this.task1, " (" + Strings.SplitTasks.TASK_1 + ")"));
		errors.addAll(TaskValidation.validate(this.task2, " (" + Strings.SplitTasks.TASK_2 + ")"));

		if (errors.isEmpty()) {
			this.services.removeTask(this.originalTask);
			this.services.addTask(this.task1);
			this.services.addTask(this.task2);

			this.activityServices.add(new Activity(this.task1.getActivity()));
			this.activityServices.add(new Activity(this.task2.getActivity()));

			this.descriptionServices.add(new Description(this.task1.getDescription()));
			this.descriptionServices.add(new Description(this.task2.getDescription()));
			if (this.refreshable != null) {
				this.refreshable.refresh();
			}
			dispose();
		} else {
			StringBuffer sb = new StringBuffer(Strings.Form.ERRORS + ":\n");

			for (String e : errors) {
				sb.append("\t - " + e + "\n");
			}

			JOptionPane.showMessageDialog(this, sb.toString(), Strings.Form.MANDATORY_FIELDS, 0);
		}
	}

	private void fillTask() {
		if (this.task1 == null) {
			this.task1 = new Task();
		}
		if (this.beginTask1.getValue() != null) {
			this.task1.setBegin((Date) this.beginTask1.getValue());
		}
		if (this.endTask1.getValue() != null) {
			this.task1.setEnd((Date) this.endTask1.getValue());
		}

		if (this.activityTask1.getSelectedItem() != null && !Utils.isEmpty(this.activityTask1.getSelectedItem().toString())) {
			this.task1.setActivity(this.activityTask1.getSelectedItem().toString());
		} else {
			this.task1.setActivity("");
		}

		if (this.descriptionTask1.getSelectedItem() != null && !Utils.isEmpty(this.descriptionTask1.getSelectedItem().toString())) {
			this.task1.setDescription(this.descriptionTask1.getSelectedItem().toString());
		} else {
			this.task1.setDescription("");
		}
		this.task1.setTaskClass(this.taskClassTask1.getSelectedItem().toString());
		this.task1.setSystem(this.systemTask1.getSelectedItem().toString());

		if (this.task2 == null) {
			this.task2 = new Task();
		}
		if (this.beginTask2.getValue() != null) {
			this.task2.setBegin((Date) this.beginTask2.getValue());
		}
		if (this.endTask2.getValue() != null) {
			this.task2.setEnd((Date) this.endTask2.getValue());
		}

		if (this.activityTask2.getSelectedItem() != null && !Utils.isEmpty(this.activityTask2.getSelectedItem().toString())) {
			this.task2.setActivity(this.activityTask2.getSelectedItem().toString());
		} else {
			this.task2.setActivity("");
		}

		if (this.descriptionTask2.getSelectedItem() != null && !Utils.isEmpty(this.descriptionTask2.getSelectedItem().toString())) {
			this.task2.setDescription(this.descriptionTask2.getSelectedItem().toString());
		} else {
			this.task2.setDescription("");
		}
		this.task2.setTaskClass(this.taskClassTask2.getSelectedItem().toString());
		this.task2.setSystem(this.systemTask2.getSelectedItem().toString());
	}

	public void refresh() {
		calculateDelta1();
		calculateDelta2();
	}

	private void calculateDelta1() {
		Date b = (Date) this.beginTask1.getValue();
		Date e = (Date) this.endTask1.getValue();

		if (b != null && e != null && b.before(e)) {
			long delta = e.getTime() - b.getTime();
			this.deltaTask1.setText(Utils.getTimeByDelta(delta));
		}
	}

	private void calculateDelta2() {
		Date b = (Date) this.beginTask2.getValue();
		Date e = (Date) this.endTask2.getValue();

		if (b != null && e != null && b.before(e)) {
			long delta = e.getTime() - b.getTime();
			this.deltaTask2.setText(Utils.getTimeByDelta(delta));
		}
	}

	public void doContinue(Task task) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

}