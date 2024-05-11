package br.com.jpsp.gui.database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import br.com.jpsp.services.TaskSetServices;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class ExportDB2Txt extends JDialog {
	private static final long serialVersionUID = -3218307819517596211L;
	private JCheckBox includeHeaders;
	private JTextField separator;
	private JTextField targetDir;
	private final TaskSetServices services = TaskSetServices.instance;
	private JTextField fileName;
	private JTextField encoding;
	private final JFileChooser fc = new JFileChooser();
	private JButton cancel;
	private JButton exportDB;
	private File directoryToExport;
	private final static Logger log = LogManager.getLogger(ExportDB2Txt.class);

	public ExportDB2Txt() {
		super();
		this.setTitle(Strings.DBOptions.IMPORT_TITLE);
		setModal(true);
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setIconImage(Images.DATABASE_IMPORT_IMG);

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

		this.includeHeaders = new JCheckBox();
		this.includeHeaders.setSelected(true);
		inputs.add(new JLabel(Strings.DBOptions.INCLUDE_HEADERS));
		inputs.add(this.includeHeaders);

		this.separator = new JTextField(Utils.DEFAULT_SEPARATOR, 5);
		this.separator.setSize(5, this.separator.getHeight());
		inputs.add(new JLabel(Strings.DBOptions.SEPARATOR + ": "));
		inputs.add(this.separator);

		this.encoding = new JTextField(FilesUtils.DEFAULT_ENCODING, 5);
		this.encoding.setSize(5, this.separator.getHeight());
		inputs.add(new JLabel(Strings.DBOptions.ENCODING + ": "));
		inputs.add(this.encoding);

		this.fileName = new JTextField(Strings.DBOptions.DEFAULT_EXPORT_FILE_NAME, 30);
		inputs.add(new JLabel(Strings.DBOptions.FILE_NAME + ": "));
		inputs.add(this.fileName);

		this.directoryToExport = new File(FilesUtils.DATA_FOLDER_NAME);

		JButton chooseFile = new JButton(Strings.DBOptions.CHOOSE_DIR);
		chooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportDB2Txt.this.chooseFile();
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

		Gui.makeCompactGrid(inputs, 5, 2, 5, 5, 5, 5);

		fields.add(inputs);

		JPanel buttons = new JPanel(new BorderLayout());
		this.cancel = new JButton(Strings.GUI.CANCEL);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportDB2Txt.this.closeWindow();
			}
		});

		this.exportDB = new JButton(Strings.DBOptions.EXPORT);
		this.exportDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExportDB2Txt.this.exportToTxt();
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

	private void exportToTxt() {
		String s = this.separator.getText();
		if (Utils.isEmpty(s)) {
			s = Utils.DEFAULT_SEPARATOR;
			this.separator.setText(s);
		}

		String fn = this.fileName.getText();
		if (Utils.isEmpty(fn)) {
			fn = Strings.DBOptions.DEFAULT_EXPORT_FILE_NAME;
			this.fileName.setText(fn);
		}

		String enc = this.encoding.getText();
		if (Utils.isEmpty(enc)) {
			enc = "ISO8859_1";
			this.encoding.setText(enc);
		}

		try {
			File txtFile = new File(
					String.valueOf(this.directoryToExport.getCanonicalPath()) + FilesUtils.FILE_SEPARATOR + fn);

			if (this.services.exportDB2Txt(txtFile, s, enc, this.includeHeaders.isSelected())) {
				try {
					String path = txtFile.getCanonicalPath();
					path = path.replaceAll("[\\\\]", "/");

					String message = Strings.DBOptions.EXPORT_SUCCESS.replaceAll("&1", path);
					Gui.showMessage(this, message);
					closeWindow();
				} catch (IOException e) {
					log.error("export() " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				try {
					String message = Strings.DBOptions.EXPORT_ERROR.replaceAll("&1", txtFile.getCanonicalPath());
					Gui.showErrorMessage(this, message);
					log.info("export() " + message);
				} catch (IOException e) {
					log.error("export() " + e.getMessage());
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			log.error("export() " + e.getMessage());
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
