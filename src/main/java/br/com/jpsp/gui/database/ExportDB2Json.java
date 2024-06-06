package br.com.jpsp.gui.database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

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
public class ExportDB2Json extends JDialog {
	private static final long serialVersionUID = -3218307819517596211L;
	private final static Logger log = LogManager.getLogger(ExportDB2Json.class);

	private JTextField targetDir;
	private final TaskServices services = TaskServices.instance;
	private JTextField fileName;
	private JComboBox<Charset> encoding;
	private final JFileChooser fc = new JFileChooser();
	private JButton cancel;
	private JButton exportDB;
	private File directoryToExport;

	public ExportDB2Json() {
		super();
		this.setTitle(Strings.DBOptions.EXPORT_JSON_TITLE);
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
//		setAlwaysOnTop(true);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DARK_BG_COLOR);
		main.setBorder(Gui.getLinedBorder(Strings.DBOptions.EXPORT_TITLE,
				Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));

		this.fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		this.fc.setMultiSelectionEnabled(false);

		JPanel fields = new JPanel(new SpringLayout());

		JPanel inputs = new JPanel(new SpringLayout());

		this.encoding = new JComboBox<Charset>(Utils.ENCODINGS);
		this.encoding.setSelectedIndex(0);
		inputs.add(new JLabel(Strings.DBOptions.ENCODING + ": "));
		inputs.add(this.encoding);

		this.fileName = new JTextField(Strings.DBOptions.DEFAULT_JSON_FILE_NAME, 30);
		inputs.add(new JLabel(Strings.DBOptions.FILE_NAME + ": "));
		inputs.add(this.fileName);

		this.directoryToExport = new File(FilesUtils.DATA_FOLDER_NAME);

		JButton chooseFile = new JButton(Strings.DBOptions.CHOOSE_DIR);
		chooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportDB2Json.this.chooseFile();
			}
		});

		inputs.add(chooseFile);
		try {
			this.targetDir = new JTextField(this.directoryToExport.getCanonicalPath(), 30);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		this.targetDir.setEditable(false);
		inputs.add(this.targetDir);

		Gui.makeCompactGrid(inputs, 3, 2, 5, 5, 5, 5);

		fields.add(inputs);

		JPanel buttons = new JPanel(new BorderLayout());
		this.cancel = new JButton(Strings.GUI.CANCEL);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportDB2Json.this.closeWindow();
			}
		});

		this.exportDB = new JButton(Strings.DBOptions.EXPORT);
		this.exportDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportDB2Json.this.exportToJson();
			}
		});

		buttons.add(this.cancel, "West");
		buttons.add(this.exportDB, "East");

		fields.add(buttons);

		Gui.makeCompactGrid(fields, 2, 1, 10, 10, 10, 10);
		main.add(fields, "Center");

		return main;
	}

	private void closeWindow() {
		dispose();
	}

	/**
	 *
	 */
	private void exportToJson() {

		String fn = this.fileName.getText();
		if (Utils.isEmpty(fn)) {
			fn = Strings.DBOptions.DEFAULT_EXPORT_FILE_NAME;
			this.fileName.setText(fn);
		}

		try {
			File txtFile = new File(
					String.valueOf(this.directoryToExport.getCanonicalPath()) + FilesUtils.FILE_SEPARATOR + fn);

			Charset encoding = (Charset) this.encoding.getSelectedItem();
			if (this.services.exportTasksDB2Json(txtFile, encoding)) {
				try {
					String path = txtFile.getCanonicalPath();
					path = path.replaceAll("[\\\\]", "/");

					String message = Strings.DBOptions.EXPORT_SUCCESS.replaceAll("&1", path);
					Gui.showMessage(this, message);
					closeWindow();
				} catch (IOException e) {
					log.error("exportToJson() " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				try {
					String message = Strings.DBOptions.EXPORT_ERROR.replaceAll("&1", txtFile.getCanonicalPath());
					Gui.showErrorMessage(this, message);
					log.info("exportToJson() " + message);
				} catch (IOException e) {
					log.error("exportToJson() " + e.getMessage());
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			log.error("exportToJson() " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void chooseFile() {
		int returnVal = this.fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.directoryToExport = this.fc.getSelectedFile();
			try {
				this.targetDir.setText(this.directoryToExport.getCanonicalPath());
			} catch (IOException e) {
				log.info("chooseFile() " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
