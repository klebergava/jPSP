package br.com.jpsp.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;

public class SimpleBrowser extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5369059681416197183L;

	private final String content;
	
	public SimpleBrowser(String title, String content) {
		super(title);
		this.content = content;
	}
	
	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());

		final JEditorPane editor = new JEditorPane("text/html", this.content);
		editor.setEditable(false);
		JScrollPane scroll = Gui.getScroll(editor);
		add(scroll, "Center");
		setSize(1024, 768);

		editor.setCaretPosition(0);

		JButton selectAll = new JButton(Strings.Report.SELECT_ALL);
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editor.selectAll();
			}
		});

		pack();
		setVisible(true);
	}
	
	public static JEditorPane buildEmpty() {
		final JEditorPane editor = new JEditorPane("text/html", "");
		editor.setEditable(false);
		return editor;
	}
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public static JEditorPane buildWithContent(String content) {
		final JEditorPane editor = new JEditorPane("text/html", content);
		editor.setEditable(false);
		return editor;
	}
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public static JEditorPane buildFromFile(File htmlFile) {
		List<String> lines = FilesUtils.readTxtFile(htmlFile);
		
		StringBuilder content = new StringBuilder("");
		lines.forEach(line -> {
			content.append(line);
		});
		
		final JEditorPane editor = new JEditorPane("text/html", content.toString());
		editor.setEditable(false);
		return editor;
	}
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	public static JEditorPane buildFromFile(InputStream htmlFile) {
		List<String> lines = FilesUtils.readTxtFile(htmlFile);
		
		StringBuilder content = new StringBuilder("");
		lines.forEach(line -> {
			content.append(line);
		});
		
		final JEditorPane editor = new JEditorPane("text/html", content.toString());
		editor.setEditable(false);
		return editor;
	}
}
