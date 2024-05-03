package br.com.jpsp.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.Gui;

public class Splash extends JFrame {
	private static final long serialVersionUID = 6068987447068216025L;

	public Splash() {
		super("");
		setDefaultCloseOperation(0);
		setUndecorated(true);
	}

	public void createAndShow() {
		setDefaultCloseOperation(2);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mount(), "Center");

		setUndecorated(true);
		setSize(800, 600);
		pack();
		setVisible(true);
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setResizable(false);
		toFront();
	}

	private JPanel mount() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBorder(Gui.getEmptyBorder(10));
		
		main.setOpaque(true);
		main.setBackground(Color.WHITE);

		Icon icon = Images.SPLASH_ICON;
		JLabel label = new JLabel();
		label.setIcon(icon);
		main.add(label, "Center");
		
		label = new JLabel(Strings.VERSION + " - " + Strings.VERSION_DATE);
		label.setFont(Gui.getFont(Font.BOLD, 18));
				
		main.add(label, "South");

		return main;
	}
}

