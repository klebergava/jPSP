package br.com.jpsp.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

public class MergeTasks extends JFrame implements Refreshable, WindowListener {
	private static final long serialVersionUID = -103651380043899625L;
	private JSpinner start;
	private JSpinner end;
	private JTextField delta;
	private JComboBox<String> task;
	private JComboBox<String>  description;
	private JComboBox<String> taskClass;
	private JComboBox<String> system;
	private final Refreshable refreshable;
	
	private final TaskSetServices services = TaskSetServices.instance;
	private final ActivityServices activityServices = ActivityServices.instance;
	private final DescriptionServices descriptionServices = DescriptionServices.instance;

	private Task mergedTask;

	private List<Task> tasks;

	public MergeTasks(List<Task> tasks, Refreshable refreshable) {
		super(Strings.MergeTasks.TITLE);
		this.tasks = tasks;
		this.refreshable = refreshable;
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.setIconImage(Images.MERGE_IMG);

		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(mountMain(), "Center");

//		setUndecorated(true);
		setIconImage(Images.MERGE_IMG);
		setAlwaysOnTop(true);

		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		this.setPreferredSize(new Dimension(800, 600));
		setVisible(true);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(Gui.getLinedBorder(Strings.MergeTasks.TITLE, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));
		main.setBackground(GuiSingleton.DEFAULT_BG_COLOR);

		JPanel tasksList = new JPanel(new SpringLayout());
		tasksList.setBackground(Color.WHITE);

		long delta = 0L;
		for (Task t : this.tasks) {
			JLabel label = new JLabel(Utils.date2String(t.getBegin(), "dd/MM/yyyy"));
			tasksList.add(label);

			label = new JLabel(
					(new SimpleDateFormat("EEEE", new Locale("pt"))).format(Long.valueOf(t.getBegin().getTime())));
			tasksList.add(label);

			label = new JLabel(Utils.date2String(t.getBegin(), "HH:mm:ss"));
			tasksList.add(label);

			label = new JLabel(Utils.date2String(t.getEnd(), "HH:mm:ss"));
			tasksList.add(label);

			delta = t.getDelta();
			label = new JLabel(Utils.getTimeByDelta(delta));
			tasksList.add(label);

			label = new JLabel(t.getDescription());
			tasksList.add(label);

			label = new JLabel(t.getTaskClass());
			tasksList.add(label);
			
			label = new JLabel(t.getSystem());
			tasksList.add(label);
		}

		Gui.makeCompactGrid(tasksList, this.tasks.size(), 8, 5, 5, 5, 5);

		JPanel fields = new JPanel(new SpringLayout());

		this.end = DateSpinner.createSpinner(null, null, null, Gui.getFont(0, Integer.valueOf(12)), this);
		this.end.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				MergeTasks.this.calculateDelta();
			}

			public void focusLost(FocusEvent e) {
				MergeTasks.this.calculateDelta();
			}
		});

		this.start = DateSpinner.createSpinner(this.end.getModel(), null, null, Gui.getFont(0, Integer.valueOf(12)),
				this);
		this.start.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				MergeTasks.this.calculateDelta();
			}

			public void focusLost(FocusEvent e) {
				MergeTasks.this.calculateDelta();
			}
		});

		this.delta = new JTextField("");
		this.delta.setEditable(false);

		List<String> tasks = this.activityServices.getAllActivitiesDescriptions();
		this.task = new JComboBox<String>(tasks.toArray(new String[tasks.size()]));
		this.task.setEditable(true);

		Set<String> descs = this.services.getAllDescriptions();
		this.description = new JComboBox<String>(descs.toArray(new String[descs.size()]));
		this.description.setEditable(true);

		this.taskClass = Gui.createTypeClassCombo();
		
		this.system = Gui.createSystemsCombo();

		fields.add(new JLabel(Strings.Form.START));
		fields.add(this.start);
		
		fields.add(new JLabel(Strings.Form.END));
		fields.add(this.end);
		
		fields.add(new JLabel(Strings.Form.DELTA));
		fields.add(this.delta);
		
		fields.add(new JLabel(Strings.Form.TASK));
		fields.add(this.task);
		
		fields.add(new JLabel(Strings.Form.DESCRIPTION));
		fields.add(this.description);
		
		fields.add(new JLabel(Strings.Form.CLASSIFICATION));
		fields.add(this.taskClass);
		
		fields.add(new JLabel(Strings.Form.SYSTEM));
		fields.add(this.system);

		Gui.makeCompactGrid(fields, 7, 2, 5, 5, 5, 5);

		loadTask();

		JPanel buttons = new JPanel(new BorderLayout());

		JButton button = new JButton("Cancelar");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MergeTasks.this.dispose();
			}
		});

		buttons.add(button, "West");

		button = new JButton(Strings.GUI.CONFIRM);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MergeTasks.this.save();
			}
		});

		buttons.add(button, "East");
		
		JScrollPane tasksListScroll = Gui.getDefaultScroll(tasksList);
		tasksListScroll.setPreferredSize(new Dimension(400, 100));
		
		JPanel mainPanel = new JPanel(new SpringLayout());
		mainPanel.setBorder(Gui.getEmptyBorder(5));
		
		mainPanel.add(tasksListScroll);
		mainPanel.add(fields);
		mainPanel.add(buttons);

		Gui.makeCompactGrid(mainPanel, 3, 1, 5, 5, 5, 5);

		main.add(mainPanel, "Center");

		calculateDelta();

		return main;
	}

	private void loadTask() {
		Task first = this.tasks.get(0);
		Task last = this.tasks.get(this.tasks.size() - 1);

		this.start.setValue(first.getBegin());
		this.end.setValue(last.getEnd());
	}

	private void save() {
		fillTask();
		List<String> errors = TaskValidation.validate(this.mergedTask);
		if (errors.isEmpty()) {
			this.services.removeTasks(this.tasks);
			this.services.addTask(this.mergedTask);
			this.activityServices.add(new Activity(this.mergedTask.getActivity()));
			this.descriptionServices.add(new Description(this.mergedTask.getDescription()));
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
		if (this.mergedTask == null) {
			this.mergedTask = new Task();
		}

		if (this.start.getValue() != null) {
			this.mergedTask.setBegin((Date) this.start.getValue());
		}

		if (this.end.getValue() != null) {
			this.mergedTask.setEnd((Date) this.end.getValue());
		}

		if (this.task.getSelectedItem() != null && !Utils.isEmpty(this.task.getSelectedItem().toString())) {
			this.mergedTask.setActivity(this.task.getSelectedItem().toString());
		} else {
			this.mergedTask.setActivity("");
		}

		if (this.description.getSelectedItem() != null && !Utils.isEmpty(this.description.getSelectedItem().toString())) {
			this.mergedTask.setDescription(this.description.getSelectedItem().toString());
		} else {
			this.mergedTask.setDescription("");
		}
		
		
		if (this.system.getSelectedItem() != null && !Utils.isEmpty(this.system.getSelectedItem().toString())) {
			this.mergedTask.setSystem(this.system.getSelectedItem().toString());
		} else {
			this.mergedTask.setSystem("");
		}
		
		this.mergedTask.setTaskClass(this.taskClass.getSelectedItem().toString());
	}

	public void refresh() {
		calculateDelta();
	}

	private void calculateDelta() {
		Date b = (Date) this.start.getValue();
		Date e = (Date) this.end.getValue();

		if (b != null && e != null && b.before(e)) {
			long delta = e.getTime() - b.getTime();
			this.delta.setText(Utils.getTimeByDelta(delta));
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
