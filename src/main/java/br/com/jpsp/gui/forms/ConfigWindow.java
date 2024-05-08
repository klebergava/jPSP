package br.com.jpsp.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.Refreshable;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Configuration;
import br.com.jpsp.services.ConfigServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.Gui;

/**
 * 
 */
public class ConfigWindow extends JFrame implements WindowListener {
	private static final long serialVersionUID = 7811181541648032335L;
	private final ConfigServices configServices = ConfigServices.instance;
	private JCheckBox autoPause;
	private JCheckBox autoStart;
	private JTextField alertTime;
	private JTextField name;
//	private JTextField outputFolder;
	private JButton save;
	private JButton cancel;
	private JComboBox<String> lookAndFeel;
	private Refreshable refreshable;

	public ConfigWindow(Refreshable refreshable) {
		super(Strings.ConfigWindow.TITLE);
		Gui.setConfiguredLookAndFeel(this);
		this.refreshable = refreshable;
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.setIconImage(Images.CONFIG_IMG);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mountMain(), "Center");

		setSize(800, 600);
		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
		setAlwaysOnTop(true);

		addWindowListener(this);

		fillFromFile();
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DEFAULT_BG_COLOR);
		main.setBorder(Gui.getLinedBorder(Strings.ConfigWindow.TITLE, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));

		JPanel fields = new JPanel(new SpringLayout());

		this.autoPause = new JCheckBox(Strings.ConfigWindow.AUTOMATICALLY_START_PAUSE);
		fields.add(this.autoPause);

		this.autoStart = new JCheckBox(Strings.ConfigWindow.RESTART_FROM_LAST_TASK);
		fields.add(this.autoStart);

		JPanel textFields = new JPanel(new SpringLayout());
		textFields.add(new JLabel(Strings.ConfigWindow.ALERT_WHEN_TIME_REACHED + ":"));
		this.alertTime = new JTextField("", 6);
		textFields.add(this.alertTime);

		textFields.add(new JLabel(Strings.ConfigWindow.YOUR_NAME + ":"));
		this.name = new JTextField("", 20);
		textFields.add(this.name);
		
		Gui.makeCompactGrid(textFields, 2, 2, 5, 5, 5, 5);

		fields.add(textFields);

		JPanel appearance = new JPanel(new BorderLayout());
		this.lookAndFeel = new JComboBox<String>();
		List<String> laf = Gui.getAvailableLookAndFeel();
		for (String l : laf) {
			this.lookAndFeel.addItem(l);
		}
		
		appearance.add(new JLabel(Strings.ConfigWindow.APPEARANCE + ": "), "West");
		appearance.add(this.lookAndFeel, "Center");
		fields.add(appearance);

		JPanel buttons = new JPanel(new BorderLayout());
		this.cancel = new JButton(Strings.GUI.CANCEL);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigWindow.this.closeWindow();
			}
		});

		this.save = new JButton(Strings.GUI.CONFIRM);
		this.save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigWindow.this.saveToFile();
			}
		});

		buttons.add(this.cancel, "West");
		buttons.add(this.save, "East");

		fields.add(buttons);

		Gui.makeCompactGrid(fields, 5, 1, 10, 10, 10, 10);
		main.add(fields, "Center");

		return main;
	}

	private void fillFromFile() {
		Configuration c = this.configServices.getConfiguration();
		this.autoPause.setSelected(c.isAutoPause());
		this.alertTime.setText(c.getAlertTime());
		this.lookAndFeel.setSelectedItem(c.getLookAndFeel());
		this.autoStart.setSelected(c.isAutoStart());
		this.name.setText(c.getName());
	}

	private void saveToFile() {
		
		Configuration config = new Configuration();
		config.setAutoPause(this.autoPause.isSelected() ? 1 : 0);
		config.setAlertTime(this.alertTime.getText());
		config.setLookAndFeel(this.lookAndFeel.getSelectedItem().toString());
		config.setAutoStart(this.autoStart.isSelected() ? 1 : 0);
		config.setName(this.name.getText());
		
		this.configServices.updateConfiguration(config);
		closeWindow();
	}

	private void closeWindow() {
		dispose();
		if (this.refreshable != null)
			this.refreshable.refresh();
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
