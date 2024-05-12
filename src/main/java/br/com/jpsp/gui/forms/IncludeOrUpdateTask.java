package br.com.jpsp.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import br.com.jpsp.services.TaskServices;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

public class IncludeOrUpdateTask extends JDialog implements Refreshable, WindowListener {
	private static final long serialVersionUID = -103651380043899625L;
	private final static Logger log = LogManager.getLogger(IncludeOrUpdateTask.class);

	private Task updatedTask;
	private JSpinner begin;
	private JSpinner end;
	private JTextField delta;

	private JComboBox<String> task;
	private JComboBox<String>  description;

	private JComboBox<String> taskClass;

	private JComboBox<String> system;

	private final Refreshable refreshable;

	private final TaskServices services = TaskServices.instance;
	private final ActivityServices activityServices = ActivityServices.instance;
	private final DescriptionServices descriptionServices = DescriptionServices.instance;

	private final boolean isInclusion;
	private String title;

	public IncludeOrUpdateTask(Task task, Refreshable refreshable) {
		super();
		this.setTitle((task == null) ? Strings.IncludeOrUpdateTask.TITLE_INCLUDE : Strings.IncludeOrUpdateTask.TITLE_EDIT);
		this.setModal(true);
		this.isInclusion = (task == null);
		this.refreshable = refreshable;
		Gui.setConfiguredLookAndFeel(this);
		this.updatedTask = null;
		if (task != null) {
			this.updatedTask = task.clone();
			this.updatedTask.setId(task.getId());
		}

		this.title = (task == null) ? Strings.IncludeOrUpdateTask.TITLE_INCLUDE : Strings.IncludeOrUpdateTask.TITLE_EDIT;

		if (task == null)
			this.setIconImage(Images.ADD_IMG);
		else this.setIconImage(Images.EDIT_IMG);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(mountMain(), "Center");

		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(Gui.getLinedBorder(this.title, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));
		main.setBackground(GuiSingleton.DARK_BG_COLOR);

		JPanel fields = new JPanel(new SpringLayout());

		this.end = DateSpinner.createSpinner(null, null, null, Gui.getFont(0, Integer.valueOf(12)), this);
		this.end.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				IncludeOrUpdateTask.this.calculateDelta();
			}

			public void focusLost(FocusEvent e) {
				IncludeOrUpdateTask.this.calculateDelta();
			}
		});

		this.begin = DateSpinner.createSpinner(this.end.getModel(), null, null, Gui.getFont(0, Integer.valueOf(12)),
				this);
		this.begin.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				IncludeOrUpdateTask.this.calculateDelta();
			}

			public void focusLost(FocusEvent e) {
				IncludeOrUpdateTask.this.calculateDelta();
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
		fields.add(this.begin);

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

		loadTask();

		JPanel buttons = new JPanel(new BorderLayout());

		JButton button = new JButton(Strings.GUI.CANCEL);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IncludeOrUpdateTask.this.dispose();
			}
		});

		buttons.add(button, "West");

		button = new JButton(Strings.GUI.CONFIRM);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				IncludeOrUpdateTask.this.save();
			}
		});

		buttons.add(button, "East");

		Gui.makeCompactGrid(fields, 7, 2, 5, 5, 5, 5);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(Gui.getEmptyBorder(5));
		panel.add(fields, "Center");
		panel.add(buttons, "South");

		main.add(panel, "Center");

		return main;
	}

	private void loadTask() {
		if (this.updatedTask != null) {
			Date begin = this.updatedTask.getBegin();
			Date end = this.updatedTask.getEnd();

			this.begin.setValue(begin);
			this.end.setValue(end);

			this.delta.setText(Utils.getTimeByDelta(this.updatedTask.getDelta()));
			this.task.setSelectedItem(this.updatedTask.getActivity());
			this.description.setSelectedItem(this.updatedTask.getDescription());
			this.taskClass.setSelectedItem(this.updatedTask.getTaskClass());
			this.system.setSelectedItem(this.updatedTask.getSystem());
		} else {
			Calendar c = Calendar.getInstance();
			c.add(12, -1);
			Date d = c.getTime();
			this.begin.setValue(d);

			c.add(12, 2);
			d = c.getTime();
			this.end.setValue(d);

			calculateDelta();
		}
	}

	private void save() {
		fillTask();
		List<String> errors = TaskValidation.validate(this.updatedTask);
		if (errors.isEmpty()) {
			if (this.isInclusion) {
				this.services.add(this.updatedTask);
			} else {
				try {
					this.services.update(this.updatedTask);
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}

			Activity activity = new Activity(this.updatedTask.getActivity());
			this.activityServices.add(activity);
			this.descriptionServices.add(new Description(this.updatedTask.getDescription()));

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
		if (this.updatedTask == null) {
			this.updatedTask = new Task();
		}

		if (this.begin.getValue() != null) {
			this.updatedTask.setBegin((Date) this.begin.getValue());
		}

		if (this.end.getValue() != null) {
			this.updatedTask.setEnd((Date) this.end.getValue());
		}

		if (this.task.getSelectedItem() != null && !Utils.isEmpty(this.task.getSelectedItem().toString())) {
			this.updatedTask.setActivity(this.task.getSelectedItem().toString());
		} else {
			this.updatedTask.setActivity("");
		}

		if (this.description.getSelectedItem() != null && !Utils.isEmpty(this.description.getSelectedItem().toString())) {
			this.updatedTask.setDescription(this.description.getSelectedItem().toString());
		} else {
			this.updatedTask.setDescription("");
		}
		this.updatedTask.setTaskClass(this.taskClass.getSelectedItem().toString());
		this.updatedTask.setSystem(this.system.getSelectedItem().toString());
	}

	public void refresh() {
		calculateDelta();
	}

	private void calculateDelta() {
		Date b = (Date) this.begin.getValue();
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
