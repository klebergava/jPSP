package br.com.jpsp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Task;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskServices;
import br.com.jpsp.utils.Gui;

public class TotalSpentOnTask extends JDialog {
	private static final long serialVersionUID = 6068987447068216025L;
	private final Task task;
	private final TaskServices services = TaskServices.instance;

	public TotalSpentOnTask(Task task) {
		super();
		this.setTitle(Strings.jPSP.TASK_SPENT_TIME);
		this.setModal(true);
		this.task = task;
		setIconImage(Images.CHRONOMETER_IMG);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mount(), "Center");

		setSize(800, 600);
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
		toFront();
		setVisible(true);
	}

	private JPanel mount() {
		String total = services.getTotalSpentOn(task);

		String txt = "'" + task.getActivity() + "'";//Strings.jPSP.TASK_TOTAL_SPENT_TIME.replaceAll("&1", task.getActivity());

		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DARK_BG_COLOR);
		main.setBorder(
				Gui.getLinedBorder(Strings.jPSP.TASK_SPENT_TIME, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));

		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(Gui.getEmptyBorder(15));

		JLabel txtLabel = new JLabel(txt + ":");
		txtLabel.setFont(Gui.getFont(Font.BOLD, 18));
		contentPanel.add(txtLabel, "North");

		JLabel label = new JLabel(total);
		label.setForeground(Color.BLUE);
		label.setFont(Gui.getFont(Font.BOLD, 16));
		JPanel timeSpentPanel = new JPanel(new BorderLayout());
		timeSpentPanel.setBorder(Gui.getEmptyBorder(10));
		timeSpentPanel.add(label, "Center");
		contentPanel.add(timeSpentPanel, "Center");

		JButton cancel = new JButton(Strings.GUI.OK);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JPanel buttons = new JPanel(new BorderLayout());
		buttons.setBorder(Gui.getEmptyBorder(5));
		buttons.add(cancel, "East");

		contentPanel.add(buttons, "South");

		main.add(contentPanel, "Center");

		return main;
	}
}

