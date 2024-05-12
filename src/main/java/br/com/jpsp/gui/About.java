package br.com.jpsp.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;

public class About extends JFrame {
	private static final long serialVersionUID = -2283777512879950127L;
	private final JEditorPane appInfo = SimpleBrowser.buildEmpty();
	private final JEditorPane license = SimpleBrowser.buildEmpty();
	private final JEditorPane readMe = SimpleBrowser.buildEmpty();
	private final JEditorPane logs = SimpleBrowser.buildEmpty();

    // Criando o painel principal
    private final JTabbedPane tabbedPane = new JTabbedPane();
	private final Font textAreaFont = new Font("Arial Unicode MS", Font.PLAIN, 14);
	private final JLabel splashImageLabel = new JLabel(Images.SPLASH_ICON);
	public About() {
		super(Strings.ABOUT);
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setIconImage(Images.ABOUT_IMG);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mount(), "Center");

		setSize((int)(Gui.WIDTH * 0.5), (int)(Gui.HEIGHT * 0.85));

//		pack();

		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setVisible(true);

		toFront();

		new Thread(() -> {
			appInfo.setText(Strings.LOADING);
			try {
				About.this.fillAppInfoContent();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			license.setText(Strings.LOADING);
			About.this.fillLicenseContent();
		}).start();
		new Thread(() -> {
			readMe.setText(Strings.LOADING);
			About.this.fillReadmeContent();
		}).start();

		new Thread(() -> {
			logs.setText(Strings.LOADING);
			About.this.fillLogContent();
		}).start();


		resizeToFitImage();

	}

	/**
	 *
	 */
	private void resizeToFitImage() {
		Dimension imageSize = splashImageLabel.getPreferredSize();
		Dimension frameSize = this.getSize();
		frameSize.setSize(imageSize.getWidth(), frameSize.getHeight());
		this.setSize(frameSize);
		setResizable(false);
	}

	/**
	 * @throws IOException
	 *
	 */
	private void fillAppInfoContent() throws IOException {
		appInfo.setEditable(false);
		StringBuilder content = new StringBuilder();

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(FilesUtils.ABOUT_FILE_NAME);
		List<String> fileLines = FilesUtils.readTxtFile(is);
		fileLines.forEach(line -> {
			content.append(line).append("\n");
		});

		String html = content.toString();
		html = html.replaceAll("[$][{]appVersion[}]", Strings.VERSION + " - " + Strings.VERSION_DATE);

		File path = new File(".");
		html = html.replaceAll("[$][{]appFolder[}]", path.getCanonicalPath().replaceAll("[\\\\]", "/"));

		path = new File(FilesUtils.DATABASE_FILE_PATH);
		html = html.replaceAll("[$][{]dbFile[}]", path.getCanonicalPath().replaceAll("[\\\\]", "/"));

		path = new File(FilesUtils.USER_CONFIG_DATA_FILE);
		html = html.replaceAll("[$][{]datFile[}]", path.getCanonicalPath().replaceAll("[\\\\]", "/"));

		html = html.replaceAll("[$][{]javaVersion[}]", System.getProperty("java.version"));

		List<String> jars = FilesUtils.readAppJARS();
		StringBuilder jarFiles = new StringBuilder();
		for (String jar : jars) {
			jarFiles.append("<li>").append(jar).append("</li>\n");
		}
		html = html.replaceAll("[$][{]jarFiles[}]", jarFiles.toString());

		appInfo.setText(html);
		appInfo.setCaretPosition(0);
	}

	/**
	 *
	 */
	private void fillLicenseContent() {
		license.setEditable(false);

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(FilesUtils.GPL3_LICENCE_FILE_NAME);
		List<String> fileLines = FilesUtils.readTxtFile(is);

		StringBuilder sb = new StringBuilder();
		fileLines.forEach(line -> {
			sb.append(line).append("\n");
		});

		license.setText(sb.toString());
		license.setCaretPosition(0);
	}

	/**
	 *
	 */
	private void fillReadmeContent() {
		readMe.setEditable(false);
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(FilesUtils.README_FILE_NAME);
		List<String> fileLines = FilesUtils.readTxtFile(is);
		StringBuilder sb = new StringBuilder();
		fileLines.forEach(line -> {
			sb.append(line).append("\n");
		});

		readMe.setText(sb.toString());
		readMe.setCaretPosition(0);
	}

	/**
	 *
	 */
	private void fillLogContent() {
		logs.setEditable(false);
		logs.setAlignmentX(JTextArea.LEFT_ALIGNMENT);
		List<String> fileLines = FilesUtils.readLogFile();
		logs.setFont(textAreaFont);

		StringBuilder sb = new StringBuilder();
		fileLines.forEach(line -> {
			sb.append(line).append("\n");
		});

		logs.setText(sb.toString());
		logs.setCaretPosition(0);
	}

	private JPanel mount() {
		JPanel main = new JPanel(new BorderLayout());
//		main.setBackground(GuiSingleton.DARK_BG_COLOR);

//		JLabel splashImageLabel = new JLabel(Images.SPLASH_ICON);
		splashImageLabel.setAlignmentX(0.5F);
		main.add(splashImageLabel, "North");

		JScrollPane appInfoScroll = Gui.getDefaultScroll(appInfo);
		appInfoScroll.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab(Strings.ABOUT, appInfoScroll);

		JScrollPane licenseScroll = Gui.getDefaultScroll(license);
		licenseScroll.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab(Strings.GPL3, licenseScroll);

		JScrollPane readmeScroll = Gui.getDefaultScroll(readMe);
		readmeScroll.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab(Strings.README, readmeScroll);

		JScrollPane logsScroll = Gui.getDefaultScroll(logs);
		logsScroll.setPreferredSize(new Dimension(600, 400));
		tabbedPane.addTab(Strings.LOGS, logsScroll);

		JButton close = new JButton(Strings.GUI.OK);
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				About.this.dispose();
			}
		});

		JPanel buttonsPanel = new JPanel(new BorderLayout());
		buttonsPanel.add(close, "East");

		main.add(tabbedPane, "Center");
		main.add(buttonsPanel, "South");

		return main;
	}

}
