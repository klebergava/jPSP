package br.com.jpsp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.Gui;

public class LoadingScreen extends JFrame {
	private static final long serialVersionUID = 6068987447068216025L;

	private String txt;
	private final JProgressBar progressBar;
	private int start = 0, end = 0;
	private final boolean indeterminate;

	public LoadingScreen(String txt, boolean indeterminate, int start, int end) {
		super(Strings.LOADING);
		this.indeterminate = indeterminate;
		this.txt = txt;
		this.start = start;
		this.end = end;
		progressBar = new JProgressBar(this.start, this.end);

		setIconImage(Images.LOADING_IMG);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setUndecorated(true);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mount(), "Center");

		setSize(400, 300);
		pack();
		setVisible(true);
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setResizable(false);
		toFront();
	}

	private JPanel mount() {

		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(GuiSingleton.DEFAULT_BG_COLOR);
		main.setBorder(
				Gui.getLinedBorder(Strings.LOADING, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));


		JPanel contentPanel = new JPanel(new BorderLayout());
		contentPanel.setBorder(Gui.getEmptyBorder(10));

		progressBar.setIndeterminate(this.indeterminate);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(250, 30));
		progressBar.setForeground(Color.BLUE);

		if (this.indeterminate) {
			progressBar.setValue(0);
			progressBar.setString("");
		}

		contentPanel.add(new JLabel(this.txt), "North");
		contentPanel.add(progressBar, "Center");

		main.add(contentPanel, "Center");

		return main;
	}

	public void updateProgressBar(int total) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(total);
        });
	}
}

