package br.com.jpsp.gui.database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskSetServices;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;

/**
 *
 */
public class RestoreDB extends JDialog {
	private static final long serialVersionUID = -3218307819517596211L;
	private final TaskSetServices services = TaskSetServices.instance;

	private JTextField sourceFile;
	private final JFileChooser fc = new JFileChooser();
	private JButton cancel;
	private JButton restore;

	private final static Logger log = LogManager.getLogger(RestoreDB.class);

	public RestoreDB() {
		super();
		this.setTitle(Strings.DBOptions.TITLE);
		setModal(true);
		Gui.setConfiguredLookAndFeel(this);
	}

	private File fileToRestore = null;
	private File actualDBFile;

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setIconImage(Images.DATABASE_IMG);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mountMain(), "Center");

		setSize(800, 600);
		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
//		setAlwaysOnTop(true);

		toFront();
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DARK_BG_COLOR);
		main.setBorder(Gui.getLinedBorder(Strings.RestoreDB.TITLE, Gui.getFont(1, Integer.valueOf(16)),
				Color.WHITE));

		this.fc.setCurrentDirectory(new File("data"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("DATA BASE BACKUP FILES",
				new String[] { "dbkp", "database backup" });
		this.fc.setFileFilter(filter);

		JPanel fields = new JPanel(new SpringLayout());

		JPanel inputs = new JPanel(new SpringLayout());

		this.actualDBFile = new File(FilesUtils.DATABASE_FILE_V1);

		inputs.add(new JLabel(Strings.RestoreDB.CURRENT_FILE + ": "));
		try {
			inputs.add(new JLabel(this.actualDBFile.getCanonicalPath()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JButton chooseFile = new JButton(Strings.RestoreDB.CHOOSE_FILE);
		chooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RestoreDB.this.chooseFile();
			}
		});

		inputs.add(chooseFile);
		this.sourceFile = new JTextField("", 30);
		this.sourceFile.setEditable(false);
		inputs.add(this.sourceFile);

		Gui.makeCompactGrid(inputs, 2, 2, 5, 5, 5, 5);

		fields.add(inputs);

		JPanel buttons = new JPanel(new BorderLayout());
		this.cancel = new JButton(Strings.GUI.CANCEL);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RestoreDB.this.closeWindow();
			}
		});

		this.restore = new JButton(Strings.RestoreDB.RESTORE);
		this.restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkFileAndRestore();
			}
		});

		buttons.add(this.cancel, "West");
		buttons.add(this.restore, "East");

		fields.add(buttons);

		Gui.makeCompactGrid(fields, 2, 1, 10, 10, 10, 10);
		main.add(fields, "Center");

		JLabel warning = new JLabel(Strings.RestoreDB.WARNING);
		warning.setFont(Gui.getFont(1, Integer.valueOf(16)));
		warning.setForeground(Color.RED);
		main.add(warning, "North");

		return main;
	}

	protected void checkFileAndRestore() {
		if (this.fileToRestore == null) {
			Gui.showErrorMessage(this, Strings.RestoreDB.NO_FILE_SELECTED);
			log.info("checkFileAndRestore() " + Strings.RestoreDB.NO_FILE_SELECTED);
		} else
			RestoreDB.this.restoreDB();
	}

	private void closeWindow() {
		dispose();
	}

	private void restoreDB() {
		int choice = Gui.showConfirmMessage(this, Strings.RestoreDB.CONFIRM_RESTORE);

		if (choice == JOptionPane.OK_OPTION) {
			if (this.services.restoreDB(this.fileToRestore)) {
				Gui.showMessage(this, Strings.RestoreDB.SUCESS);
				closeWindow();
			} else {
				Gui.showErrorMessage(this, Strings.RestoreDB.ERROR);
				log.info("restoreDB() " + Strings.RestoreDB.ERROR);
			}
		}
	}

	private void chooseFile() {
		int returnVal = this.fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.fileToRestore = this.fc.getSelectedFile();
			try {
				this.sourceFile.setText(this.fileToRestore.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
