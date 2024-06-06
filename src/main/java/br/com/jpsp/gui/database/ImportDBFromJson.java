package br.com.jpsp.gui.database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskServices;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class ImportDBFromJson extends JDialog {
	private static final long serialVersionUID = -3218307819517596211L;
	private final static Logger log = LogManager.getLogger(ImportDBFromJson.class);

	private ButtonGroup buttonGroup = new ButtonGroup();

	private JRadioButton deleteAllData;
	private JRadioButton doNotDeleteAllData;

	private JTextField sourceFile;
	private final TaskServices services = TaskServices.instance;
	private JComboBox<Charset> encoding;
	private final JFileChooser fc = new JFileChooser();
	private JButton importDB;
	private File fileToImportFrom;

	private JButton cancel;

	public ImportDBFromJson() {
		super();
		this.setTitle(Strings.DBOptions.IMPORT_JSON_TITLE);
		setModal(true);
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setIconImage(Images.JSON_IMG);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mountMain(), "Center");

		setSize(800, 600);
		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DARK_BG_COLOR);
		main.setBorder(
				Gui.getLinedBorder(Strings.DBOptions.IMPORT_JSON_TITLE, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));

		this.fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.fc.setMultiSelectionEnabled(false);
		this.fc.setCurrentDirectory(new File(FilesUtils.DATA_FOLDER));
		this.fc.setFileFilter(new FileNameExtensionFilter("Arquivos JSon (*.json)", "json"));

		JPanel fields = new JPanel(new SpringLayout());

		JPanel inputs = new JPanel(new SpringLayout());

		this.doNotDeleteAllData = new JRadioButton(Strings.DBOptions.DO_NOT_DELETE_ALL_DATA);
		this.doNotDeleteAllData.setSelected(true);
		this.doNotDeleteAllData.setActionCommand(Boolean.FALSE.toString());
		inputs.add(new JLabel(Strings.DBOptions.CURRENT_DATABASE));
		inputs.add(this.doNotDeleteAllData);

		this.deleteAllData = new JRadioButton(Strings.DBOptions.DELETE_ALL_DATA);
		this.deleteAllData.setSelected(false);
		this.deleteAllData.setActionCommand(Boolean.TRUE.toString());
		inputs.add(new JLabel(""));
		inputs.add(this.deleteAllData);

		buttonGroup.add(deleteAllData);
		buttonGroup.add(doNotDeleteAllData);

		this.encoding = new JComboBox<Charset>(Utils.ENCODINGS);
		this.encoding.setSelectedIndex(0);
		inputs.add(new JLabel(Strings.DBOptions.ENCODING + ": "));
		inputs.add(this.encoding);

		this.fileToImportFrom = null;

		JButton chooseFile = new JButton(Strings.DBOptions.CHOOSE_FILE);
		chooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDBFromJson.this.chooseFile();
			}
		});

		inputs.add(chooseFile);
		try {
			this.sourceFile = new JTextField("", 30);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		this.sourceFile.setEditable(false);
		inputs.add(this.sourceFile);

		Gui.makeCompactGrid(inputs, 4, 2, 5, 5, 5, 5);

		fields.add(inputs);

		JPanel buttons = new JPanel(new BorderLayout());
		this.cancel = new JButton(Strings.GUI.CANCEL);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDBFromJson.this.closeWindow();
			}
		});

		this.importDB = new JButton(Strings.DBOptions.IMPORT);
		this.importDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDBFromJson.this.importFromTxt();
			}
		});

		buttons.add(this.cancel, "West");
		buttons.add(this.importDB, "East");

		fields.add(buttons);

		Gui.makeCompactGrid(fields, 2, 1, 10, 10, 10, 10);
		main.add(fields, "Center");

		JLabel warning = new JLabel(Strings.DBOptions.IMPORT_WARNING);
		warning.setFont(Gui.getFont(1, Integer.valueOf(16)));
		warning.setForeground(Color.RED);
		main.add(warning, "North");

		return main;
	}

	private void closeWindow() {
		dispose();
	}

	/**
	 *
	 * @return
	 */
	private String validateFields() {
		StringBuilder sb = new StringBuilder("");

		if (this.fileToImportFrom == null) {
			sb.append("\n* " + Strings.DBOptions.ERROR_IMPORT_FILE_REQUIRED);
		}

		return sb.toString();
	}

	/**
	 *
	 */
	private void importFromTxt() {

		String validationMessages = this.validateFields();
		if (!Utils.isEmpty(validationMessages)) {
			Gui.showErrorMessage(this, validationMessages);
			return;
		}

		int choice = -1;
		final boolean[] deleteAllDataSelected = { false };

		String actionCommand = this.buttonGroup.getSelection().getActionCommand();

		deleteAllDataSelected[0] = Boolean.parseBoolean(actionCommand);

		if (deleteAllDataSelected[0]) {
			choice = Gui.showConfirmMessage(this, Strings.DBOptions.DELETE_ALL_DATA_CONFIRM);
			if (choice != 0)
				return;
		}

		choice = Gui.showConfirmMessage(this, Strings.DBOptions.CONFIRM_IMPORT);

		if (choice == JOptionPane.OK_OPTION) {

			Thread thread = new Thread(() -> {

				try {
					Charset encoding = (Charset) this.encoding.getSelectedItem();
					if (this.services.importTasksFromJson(this.fileToImportFrom, encoding, deleteAllDataSelected[0])) {
						try {
							String path = this.fileToImportFrom.getCanonicalPath();
							path = path.replaceAll("[\\\\]", "/");

							String message = Strings.DBOptions.IMPORT_SUCCESS.replaceAll("&1", path);
							Gui.showMessage(this, message);
							closeWindow();
						} catch (IOException e) {
							GuiSingleton.disposeLoadingScreen();
							log.error("importFromTxt() " + e.getMessage());
							e.printStackTrace();
						}
					} else {
						GuiSingleton.disposeLoadingScreen();
						try {
							String message = Strings.DBOptions.IMPORT_ERROR.replaceAll("&1",
									this.fileToImportFrom.getAbsolutePath());
							message = message + " " + Strings.DBOptions.IMPORT_ERROR_MESSAGE;
							Gui.showErrorMessage(this, message);
							log.info("importFromTxt() " + message);
						} catch (Exception e) {
							GuiSingleton.disposeLoadingScreen();
							log.error("importFromTxt() " + e.getMessage());
							e.printStackTrace();
						}

					}
				} catch (Exception e) {
					GuiSingleton.disposeLoadingScreen();
					log.error("importFromTxt() " + e.getMessage());
					e.printStackTrace();
				} finally {
					GuiSingleton.disposeLoadingScreen();
				}
			});
			thread.start();
		}
	}

	/**
	 *
	 */
	private void chooseFile() {
		int returnVal = this.fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.fileToImportFrom = this.fc.getSelectedFile();

			try {
				this.sourceFile.setText(this.fileToImportFrom.getCanonicalPath());

				GuiSingleton.showLoadingScreen(Strings.DBOptions.IMPORTING_DATA, true, 0, 0);
				List<String> lines = FilesUtils.readTxtFile(fileToImportFrom);
				GuiSingleton.disposeLoadingScreen();
				Gui.showMessage(this, Strings.DBOptions.LINES_READ.replaceAll("&1", Integer.toString(lines.size())));

			} catch (IOException e) {
				GuiSingleton.disposeLoadingScreen();
				log.info("chooseFile() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
