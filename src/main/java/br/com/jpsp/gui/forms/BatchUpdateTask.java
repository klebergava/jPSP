package br.com.jpsp.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.Refreshable;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Task;
import br.com.jpsp.services.ActivityServices;
import br.com.jpsp.services.DescriptionServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskServices;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class BatchUpdateTask extends JDialog implements WindowListener {
	private static final long serialVersionUID = -103651380043899625L;
	private final static Logger log = LogManager.getLogger(BatchUpdateTask.class);

	private final Collection<Task> tasks;
	private final Refreshable refreshable;

	private final TaskServices taskServices = TaskServices.instance;
	private final ActivityServices activityServices = ActivityServices.instance;
	private final DescriptionServices descriptionServices = DescriptionServices.instance;

	private final FieldToEdit toEdit;
	private JComboBox<String> task;
	private JComboBox<String> description;
	private JComboBox<String> taskClass;
	private JComboBox<String> system;
	private String title;

	public BatchUpdateTask(Collection<Task> tasks, String title, FieldToEdit toEdit, Refreshable refreshable) {
		super();
		this.setTitle(title);
		this.setModal(true);
		this.toEdit = toEdit;
		this.tasks = tasks;
		this.refreshable = refreshable;
		Gui.setConfiguredLookAndFeel(this);
		this.title = title;

		switch (this.toEdit) {
			case ACTIVITY_AND_DESC:
				this.setIconImage(Images.EDIT_ACTIVITY_AND_DESC_IMG);
				break;
			case SYSTEM:
				this.setIconImage(Images.EDIT_SYSTEM_IMG);
				break;
			case TYPE_CLASS:
				this.setIconImage(Images.EDIT_CLASS_IMG);

				break;
			default:
				break;

		}

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
		fields.setBackground(Color.WHITE);

		long delta = 0L;
		for (Task t : this.tasks) {
			JLabel label = new JLabel(Utils.date2String(t.getBegin(), "dd/MM/yyyy"));
			fields.add(label);

			label = new JLabel(
					(new SimpleDateFormat("EEEE", new Locale("pt"))).format(Long.valueOf(t.getBegin().getTime())));
			fields.add(label);

			label = new JLabel(Utils.date2String(t.getBegin(), "HH:mm:ss"));
			fields.add(label);

			label = new JLabel(Utils.date2String(t.getEnd(), "HH:mm:ss"));
			fields.add(label);

			delta = t.getDelta();
			label = new JLabel(Utils.getTimeByDelta(delta));
			fields.add(label);

			label = new JLabel(t.getDescription());
			if (this.toEdit.equals(FieldToEdit.ACTIVITY_AND_DESC)) {
				label.setForeground(Color.BLUE);
			}
			fields.add(label);

			label = new JLabel(t.getTaskClass());
			if (this.toEdit.equals(FieldToEdit.TYPE_CLASS)) {
				label.setForeground(Color.BLUE);
			}
			fields.add(label);

			label = new JLabel(t.getSystem());
			if (this.toEdit.equals(FieldToEdit.SYSTEM)) {
				label.setForeground(Color.BLUE);
			}
			fields.add(label);
		}

		Gui.makeCompactGrid(fields, this.tasks.size(), 8, 5, 5, 5, 5);

		List<String> persistedTasks = this.activityServices.getAllActivitiesDescriptions();
		this.task = new JComboBox<String>(persistedTasks.toArray(new String[persistedTasks.size()]));
		this.task.setEditable(true);

		Set<String> descs = this.descriptionServices.getAllDescriptions();
		this.description = new JComboBox<String>(descs.toArray(new String[descs.size()]));
		this.description.setEditable(true);

		this.taskClass = Gui.createTypeClassCombo();
		this.system = Gui.createSystemsCombo();

		JPanel editFields = new JPanel(new BorderLayout());
		JPanel descAct = new JPanel(new SpringLayout());

			switch (this.toEdit) {
			case ACTIVITY_AND_DESC:
					descAct.add(new JLabel(Strings.BatchUpdateTask.ACTIVITY + ": "));
					descAct.add(this.task);

					descAct.add(new JLabel(Strings.BatchUpdateTask.DESCRIPTION + ": "));
					descAct.add(this.description);

					Gui.makeCompactGrid(descAct, 2, 2, 5, 5, 5, 5);
				break;
			case SYSTEM:
				descAct.add(new JLabel(Strings.BatchUpdateTask.SYSTEM + ": "));
				descAct.add(this.system);

				Gui.makeCompactGrid(descAct, 1, 2, 5, 5, 5, 5);
				break;
			case TYPE_CLASS:
				descAct.add(new JLabel(Strings.BatchUpdateTask.CLASSIFICATION + ": "));
				descAct.add(this.taskClass);

				Gui.makeCompactGrid(descAct, 1, 2, 5, 5, 5, 5);
				break;
			default:
				break;

		}
		editFields.add(descAct);

		JPanel buttons = new JPanel(new BorderLayout());

		JButton button = new JButton(Strings.GUI.CANCEL);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BatchUpdateTask.this.dispose();
			}
		});

		buttons.add(button, "West");

		button = new JButton("Confirmar");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BatchUpdateTask.this.save();
			}
		});

		buttons.add(button, "East");
		fields.add(buttons);


		JScrollPane fieldsScroll = Gui.getDefaultScroll(fields);
		fieldsScroll.setPreferredSize(new Dimension(600, 100));

		JPanel innerMain = new JPanel(new BorderLayout());
		innerMain.setBorder(Gui.getEmptyBorder(5));
		innerMain.add(fieldsScroll, "North");
		innerMain.add(editFields, "Center");
		innerMain.add(buttons, "South");

		main.add(innerMain, "Center");

		return main;
	}

	private void save() {
		String errors = null;

		if (this.toEdit.equals(FieldToEdit.ACTIVITY_AND_DESC) && (this.task.getSelectedItem() == null || Utils.isEmpty(this.task.getSelectedItem().toString()))) {
			errors = Strings.BatchUpdateTask.PLEASE_ENTER_TASK;
		} else if (this.toEdit.equals(FieldToEdit.SYSTEM) && (this.system.getSelectedItem() == null || Utils.isEmpty(this.system.getSelectedItem().toString()))) {
			errors = Strings.BatchUpdateTask.PLEASE_ENTER_SYSTEM;
		}

		if (errors == null) {
			Task updatedTask = null;
			for (Task t : this.tasks) {
				updatedTask = t.clone();
				updatedTask.setId(t.getId());
				if (this.toEdit.equals(FieldToEdit.ACTIVITY_AND_DESC)) {
					updatedTask.setActivity(this.task.getSelectedItem().toString());
					updatedTask.setDescription(this.description.getSelectedItem().toString());
					this.descriptionServices.add(new br.com.jpsp.model.Description(updatedTask.getDescription()));
				} else if (this.toEdit.equals(FieldToEdit.TYPE_CLASS)) {
					updatedTask.setTaskClass(this.taskClass.getSelectedItem().toString());
				} else if (this.toEdit.equals(FieldToEdit.SYSTEM)) {
					updatedTask.setSystem(this.system.getSelectedItem().toString());
				}
				try {
					this.taskServices.update(updatedTask);
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
			}
			if (this.refreshable != null) {
				this.refreshable.refresh();
			}
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, errors, Strings.Form.MANDATORY_FIELDS, 0);
			log.trace(Strings.Form.MANDATORY_FIELDS);
		}
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
