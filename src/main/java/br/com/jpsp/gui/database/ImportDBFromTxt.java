package br.com.jpsp.gui.database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import br.com.jpsp.services.TaskSetServices;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class ImportDBFromTxt extends JDialog {
	private static final long serialVersionUID = -3218307819517596211L;
	private JCheckBox hasHeaders;
	private ButtonGroup buttonGroup = new ButtonGroup();

	private JRadioButton deleteAllData;
	private JRadioButton doNotDeleteAllData;

	private JTextField separator;
	private JTextField sourceFile;
	private final TaskSetServices services = TaskSetServices.instance;
	private JTextField encoding;
	private final JFileChooser fc = new JFileChooser();
	private JButton importDB;
	private File fileToImportFrom;

	private JButton cancel;
	private final static Logger log = LogManager.getLogger(ImportDBFromTxt.class);

	public ImportDBFromTxt() {
		super();
		this.setTitle(Strings.DBOptions.IMPORT_TITLE);
		setModal(true);
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setIconImage(Images.DATABASE_EXPORT_IMG);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mountMain(), "Center");

		setSize(800, 600);
		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
//		setAlwaysOnTop(true);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DEFAULT_BG_COLOR);
		main.setBorder(
				Gui.getLinedBorder(Strings.DBOptions.IMPORT_TITLE, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));

		this.fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		this.fc.setMultiSelectionEnabled(false);
		this.fc.setCurrentDirectory(new File(FilesUtils.DATA_FOLDER));
		this.fc.addChoosableFileFilter(new FileNameExtensionFilter("Text", "txt"));
		this.fc.addChoosableFileFilter(new FileNameExtensionFilter("CSV", "csv"));

		JPanel fields = new JPanel(new SpringLayout());

		JPanel inputs = new JPanel(new SpringLayout());

		this.hasHeaders = new JCheckBox();
		this.hasHeaders.setSelected(true);
		inputs.add(new JLabel(Strings.DBOptions.HAS_HEADERS));
		inputs.add(this.hasHeaders);

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

		this.separator = new JTextField(Utils.DEFAULT_SEPARATOR, 5);
		this.separator.setSize(5, this.separator.getHeight());
		inputs.add(new JLabel(Strings.DBOptions.SEPARATOR + ": "));
		inputs.add(this.separator);

		this.encoding = new JTextField(FilesUtils.DEFAULT_ENCODING, 5);
		this.encoding.setSize(5, this.separator.getHeight());
		inputs.add(new JLabel(Strings.DBOptions.ENCODING + ": "));
		inputs.add(this.encoding);

		this.fileToImportFrom = null;

		JButton chooseFile = new JButton(Strings.DBOptions.CHOOSE_FILE);
		chooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDBFromTxt.this.chooseFile();
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

		Gui.makeCompactGrid(inputs, 6, 2, 5, 5, 5, 5);

		fields.add(inputs);

		JPanel buttons = new JPanel(new BorderLayout());
		this.cancel = new JButton(Strings.GUI.CANCEL);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDBFromTxt.this.closeWindow();
			}
		});

		this.importDB = new JButton(Strings.DBOptions.IMPORT);
		this.importDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImportDBFromTxt.this.importFromTxt();
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

		if (Utils.isEmpty(this.separator.getText())) {
			sb.append("* " + Strings.DBOptions.ERROR_SEPARATOR_REQUIRED);
		}

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
				String fieldSeparator = this.separator.getText();
				if (Utils.isEmpty(fieldSeparator)) {
					fieldSeparator = Utils.DEFAULT_SEPARATOR;
					this.separator.setText(fieldSeparator);
				}

				String enc = this.encoding.getText();
				if (Utils.isEmpty(enc)) {
					enc = FilesUtils.DEFAULT_ENCODING;
					this.encoding.setText(enc);
				}

				try {

					if (this.services.importDBFromTxt(this.fileToImportFrom, fieldSeparator, enc,
							this.hasHeaders.isSelected(), deleteAllDataSelected[0])) {
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
