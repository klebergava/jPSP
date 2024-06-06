package br.com.jpsp.gui.database;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.Refreshable;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.services.RepositoryAccessServicesAdapter;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;

/**
 *
 */
public class DBOptions extends JDialog implements WindowListener {
	private static final long serialVersionUID = 7811181541648032335L;
	private JButton cancel;
	private final static Logger log = LogManager.getLogger(DBOptions.class);
	private final Refreshable refreshable;

	public DBOptions(Refreshable refreshable) {
		super();
		this.setTitle(Strings.DBOptions.TITLE);
		setModal(true);
		Gui.setConfiguredLookAndFeel(this);
		this.refreshable = refreshable;
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.setIconImage(Images.DATABASE_IMG);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mountMain(), "Center");

		setSize(800, 600);
		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
//		setAlwaysOnTop(true);

		addWindowListener(this);

		toFront();
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DARK_BG_COLOR);
		main.setBorder(
				Gui.getLinedBorder(Strings.DBOptions.OPTIONS, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));

		JPanel fields = new JPanel(new SpringLayout());

		JButton backup = new JButton(Strings.DBOptions.BACKUP_DB);
		backup.setIcon(Images.DATABASE_BACKUP);
		backup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBOptions.this.doBackup();
			}
		});
		fields.add(backup);

		JButton restore = new JButton(Strings.DBOptions.RESTORE_DB);
		restore.setIcon(Images.DATABASE_RESTORE);
		restore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showRestoreDB();
			}
		});
		fields.add(restore);

		////////// EXPORT
		JPanel exportDBOptions = new JPanel(new BorderLayout());
		exportDBOptions.setBorder(Gui.getTitledBorder(Strings.DBOptions.EXPORT_DB, Gui.COURIER_12));
		JPanel exports = new JPanel(new SpringLayout());

		JButton export = new JButton(Strings.DBOptions.EXPORT_CSV);
		export.setIcon(Images.DATABASE_EXPORT);
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showExportDB2Txt();
			}
		});

		JButton exportJson = new JButton(Strings.DBOptions.EXPORT_JSON);
		exportJson.setIcon(Images.JSON);
		exportJson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showExportDB2Json();
			}
		});

		exports.add(export);
		exports.add(exportJson);
		Gui.makeCompactGrid(exports, 2, 1, 10, 10, 10, 10);
		exportDBOptions.add(exports, "Center");
		fields.add(exportDBOptions);

		////////// IMPORT
		JPanel importDBOptions = new JPanel(new BorderLayout());
		importDBOptions.setBorder(Gui.getTitledBorder(Strings.DBOptions.IMPORT_DB, Gui.COURIER_12));
		JPanel imports = new JPanel(new SpringLayout());

		JButton importDB = new JButton(Strings.DBOptions.IMPORT_CSV);
		importDB.setIcon(Images.DATABASE_IMPORT);
		importDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showImportDBFromTxt();
			}
		});

		JButton importJson = new JButton(Strings.DBOptions.IMPORT_JSON);
		importJson.setIcon(Images.JSON);
		importJson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GuiSingleton.showImportDBFromJson();
			}
		});

		imports.add(importDB);
		imports.add(importJson);
		Gui.makeCompactGrid(imports, 2, 1, 10, 10, 10, 10);

		importDBOptions.add(imports, "Center");
		fields.add(importDBOptions);
		//////////////////////

		JPanel buttons = new JPanel(new BorderLayout());
		this.cancel = new JButton(Strings.GUI.CANCEL);
		this.cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBOptions.this.closeWindow();
			}
		});

		buttons.add(this.cancel, "West");

		fields.add(buttons);

		Gui.makeCompactGrid(fields, 5, 1, 10, 10, 10, 10);
		main.add(fields, "Center");

		return main;
	}

	private void closeWindow() {
		DBOptions.this.refreshable.refresh();
		dispose();
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

	/**
	 *
	 */
	private void doBackup() {
		File backupFile = RepositoryAccessServicesAdapter.instance.backupDatabase();
		if (backupFile != null) {
			File dir = new File(FilesUtils.DATA_FOLDER);
			try {
				String path = dir.getCanonicalPath();
				path = path.replaceAll("[\\\\]", "/");
				String message = Strings.DBOptions.SUCCESS_BACKUP.replaceAll("&1", path);
				Gui.showMessage(this, message);
			} catch (IOException e) {
				log.error("doBackup() " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			Gui.showErrorMessage(this, Strings.DBOptions.ERROR_BACKUP);
			log.info("doBackup() " + Strings.DBOptions.ERROR_BACKUP);
		}
	}

}
