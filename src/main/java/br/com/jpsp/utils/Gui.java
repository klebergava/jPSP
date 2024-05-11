package br.com.jpsp.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.model.Configuration;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskListTableModel;
import br.com.jpsp.services.ConfigServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskSetServices;

/**
 *
 */
public class Gui {

	private final static Logger log = LogManager.getLogger(Gui.class);

	private static final ConfigServices configServices = ConfigServices.instance;
	private static final TaskSetServices services = TaskSetServices.instance;

	public static final Font COURIER_12 = new Font("Courier", 0, 12);

	public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	public static final int WIDTH = (int) screenSize.getWidth();
	public static final int HEIGHT = (int) screenSize.getHeight();

	public static void makeCompactGrid(Container parent, int rows, int cols, int initialX, int initialY, int xPad,
			int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout) parent.getLayout();
		} catch (ClassCastException exc) {
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		Spring x = Spring.constant(initialX);
		for (int c = 0; c < cols; c++) {
			Spring width = Spring.constant(0);
			int i;
			for (i = 0; i < rows; i++) {
				width = Spring.max(width, getConstraintsForCell(i, c, parent, cols).getWidth());
			}
			for (i = 0; i < rows; i++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(i, c, parent, cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x, Spring.sum(width, Spring.constant(xPad)));
		}

		Spring y = Spring.constant(initialY);
		for (int r = 0; r < rows; r++) {
			Spring height = Spring.constant(0);
			int i;
			for (i = 0; i < cols; i++) {
				height = Spring.max(height, getConstraintsForCell(r, i, parent, cols).getHeight());
			}
			for (i = 0; i < cols; i++) {
				SpringLayout.Constraints constraints = getConstraintsForCell(r, i, parent, cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y, Spring.sum(height, Spring.constant(yPad)));
		}

		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint("South", y);
		pCons.setConstraint("East", x);
	}

	private static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
		SpringLayout layout = (SpringLayout) parent.getLayout();
		Component c = parent.getComponent(row * cols + col);
		return layout.getConstraints(c);
	}

	public static Font getFont(int fontDecoration, Integer fontSize) {
		String fontName = "Arial";
		Font configuredFont = new Font(fontName, fontDecoration, fontSize.intValue());
		return configuredFont;
	}

	public static Font getFont(String fontName, int fontDecoration, Integer fontSize) {
		Font configuredFont = new Font(fontName, fontDecoration, fontSize.intValue());
		return configuredFont;
	}

	public static Border getEmptyBorder(int pad) {
		return new EmptyBorder(pad, pad, pad, pad);
	}

	public static Border getTitledBorder(String title, Font fonte) {
		Border emptyBorder = null;//BorderFactory.createEmptyBorder(0, 0, 0, 0);
		return BorderFactory.createTitledBorder(emptyBorder, title, 0, 0, fonte);
	}

	public static Border getLinedBorder(String title, Font fonte, Color titleColor) {
		Border line = BorderFactory.createLineBorder(Color.DARK_GRAY, 3, true);
		TitledBorder titled = BorderFactory.createTitledBorder(line, title, 2, 0, fonte);
		if (titleColor != null) {
			titled.setTitleColor(titleColor);
		}
		return titled;
	}

	public static Border getTitledBorder(String title, Font fonte, Color color) {
		Border emptyBorder = null;//BorderFactory.createEmptyBorder(0, 0, 0, 0);
		return BorderFactory.createTitledBorder(emptyBorder, title, 0, 0, fonte, color);
	}

	public static JScrollPane getDefaultScroll(Component component) {
		JScrollPane scroll = new JScrollPane(component);
		scroll.setAutoscrolls(true);
		scroll.setHorizontalScrollBarPolicy(30);
		scroll.setVerticalScrollBarPolicy(20);
		return scroll;
	}

	public static KeyListener getKeyListenerNumbers() {
		return new KeyListener() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if ((c < '0' || c > '9') && c != '\b' && c != '' && c != ',' && c != '.') {
					e.consume();
				}
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}
		};
	}

	public static KeyListener getKeyListenerDigitsOnly() {
		return new KeyListener() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if ((c < '0' || c > '9') && c != '\b' && c != '') {
					e.consume();
				}
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}
		};
	}

	public static void setLookAndFeel() {
		try {
			String lookAndFeel = null;
			byte b;
			int i;
			UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo;
			for (i = (arrayOfLookAndFeelInfo = UIManager.getInstalledLookAndFeels()).length, b = 0; b < i;) {
				UIManager.LookAndFeelInfo info = arrayOfLookAndFeelInfo[b];

				if ("Nimbus".equals(info.getName())) {
					lookAndFeel = info.getClassName();
					break;
				}
				b++;
			}

			if (lookAndFeel == null) {
				lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			}

			UIManager.setLookAndFeel(lookAndFeel);
		} catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
			log.info("setLookAndFeel() " + unsupportedLookAndFeelException.getMessage());
		} catch (ClassNotFoundException classNotFoundException) {
			log.info("setLookAndFeel() " + classNotFoundException.getMessage());
		} catch (InstantiationException instantiationException) {
			log.info("setLookAndFeel() " + instantiationException.getMessage());
		} catch (IllegalAccessException illegalAccessException) {
			log.info("setLookAndFeel() " + illegalAccessException.getMessage());
		}
	}

	public static List<String> getAvailableLookAndFeel() {
		List<String> laf = new ArrayList<String>();
		byte b;
		int i;
		UIManager.LookAndFeelInfo[] arrayOfLookAndFeelInfo;
		for (i = (arrayOfLookAndFeelInfo = UIManager.getInstalledLookAndFeels()).length, b = 0; b < i;) {
			UIManager.LookAndFeelInfo info = arrayOfLookAndFeelInfo[b];
			laf.add(info.getClassName());
			b++;
		}

		return laf;
	}

	public static String getSelectedLookAndFeel() {
		return UIManager.getLookAndFeel().getClass().getName();
	}

	public static JScrollPane getScroll(Component c) {
		JScrollPane scroll = new JScrollPane(c);

		scroll.setHorizontalScrollBarPolicy(30);
		scroll.setVerticalScrollBarPolicy(20);
		return scroll;
	}

	public static KeyListener getKeyListenerOnlyDigits() {
		return new KeyListener() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if ((c < '0' || c > '9') && c != '\b' && c != '') {

					e.consume();
				}
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}
		};
	}

	public static JSeparator getSeparator(int orientation) {
		JSeparator s = new JSeparator(orientation);
		s.setForeground(Color.BLACK);

		return s;
	}

	public static void setSpace(JPanel panel) {
		panel.add(new JLabel("          "));
		panel.add(getSeparator(1));
		panel.add(new JLabel("          "));
	}

	public static void loadYearsComboUntilCurrent(int from, JComboBox<Integer> years) {
		int currentYear = Utils.getCurrentYear();
		for (int i = from; i <= currentYear; i++) {
			years.addItem(new Integer(i));
		}
		years.setSelectedItem(new Integer(Utils.getCurrentYear()));
	}

	public static void setLookAndFeel(String lookAndFeel, Component component) {
		try {
			UIManager.setLookAndFeel(lookAndFeel);
			if (component != null) {
				SwingUtilities.updateComponentTreeUI(component);
			}
		} catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException) {
			log.info("setLookAndFeel(String lookAndFeel, Component component) " + unsupportedLookAndFeelException.getMessage());
		} catch (ClassNotFoundException classNotFoundException) {
			log.info("setLookAndFeel(String lookAndFeel, Component component) " + classNotFoundException.getMessage());
		} catch (InstantiationException instantiationException) {
			log.info("setLookAndFeel(String lookAndFeel, Component component) " + instantiationException.getMessage());
		} catch (IllegalAccessException illegalAccessException) {
			log.info("setLookAndFeel(String lookAndFeel, Component component) " + illegalAccessException.getMessage());
		}
	}

	public static Dimension getScreenResolution() {
		Toolkit tk = Toolkit.getDefaultToolkit();
		return tk.getScreenSize();
	}

	public static int getTaskBarHeight() {
		int height = 0;
		try {
			Dimension screenSize = getScreenResolution();
			Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			height = screenSize.height - winSize.height;
		} catch (Exception ex) {
			int fullScreenSizeY = (int) getScreenResolution().getHeight();
			int maximizedScreenSizeY = (int) getMaximizedScreenResolution().getHeight();
			height = fullScreenSizeY - maximizedScreenSizeY;
		}
		return height;
	}

	public static Dimension getMaximizedScreenResolution() {
		JFrame windowFrame = new JFrame("Full screen test...");

		windowFrame.setExtendedState(6);
		windowFrame.setVisible(true);
		Dimension maximizedScreenResolution = windowFrame.getSize();
		windowFrame.setVisible(false);
		windowFrame.dispose();
		return maximizedScreenResolution;
	}

	public static JComboBox<String> createTypeClassCombo() {
		List<String> types =	services.getAllTypeClassDesc();
		JComboBox<String> taskClass = new JComboBox<String>(types.toArray(new String[types.size()]));
		return taskClass;
	}

	public static JComboBox<String> createSystemsCombo() {
		List<String> systems = services.getAllSystemsNames();
		JComboBox<String> systemsCombo = new JComboBox<String>(systems.toArray(new String[systems.size()]));
		return systemsCombo;
	}


	public static Task getSelectedTask(JTable tableSource) {
		int row = tableSource.convertRowIndexToModel(tableSource.getSelectedRow());
		TaskListTableModel model = (TaskListTableModel) tableSource.getModel();
		Task task = model.get(row);
		return task;
	}

	public static List<Task> getSelectedTasks(JTable tableSource) {
		int rowModel = 0;

		List<Task> toBatchEdit = new ArrayList<Task>();
		TaskListTableModel model = (TaskListTableModel) tableSource.getModel();
		byte b;
		int i, arrayOfInt[];
		for (i = (arrayOfInt = tableSource.getSelectedRows()).length, b = 0; b < i;) {
			int r = arrayOfInt[b];
			rowModel = tableSource.convertRowIndexToModel(r);
			Task toRemove = model.get(rowModel);
			toBatchEdit.add(toRemove);
			b++;
		}

		return toBatchEdit;
	}

	public static int getSelectedQty(JTable tableSource) {
		return (tableSource.getSelectedRows()).length;
	}

	public static void showErrorMessage(Container c, String mesg) {
		JOptionPane.showMessageDialog(c, mesg, Strings.GUI.ERROR, JOptionPane.ERROR_MESSAGE);
	}

	public static void setConfiguredLookAndFeel(Container container) {
		Configuration config = configServices.getConfiguration();
		setLookAndFeel(config.getLookAndFeel(), container);
	}

	public static void showMessage(Container c, String mesg) {
		JOptionPane.showMessageDialog(c, mesg);
	}

	public static int showConfirmMessage(Container c, String mesg) {
		return JOptionPane.showConfirmDialog(c, mesg, Strings.GUI.CONFIRM_ACTION, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	public static int showConfirmMessage(Container c, String mesg, int type) {
		return JOptionPane.showConfirmDialog(c, mesg, Strings.GUI.CONFIRM_ACTION, type, JOptionPane.QUESTION_MESSAGE);
	}

	public static Font getTitledBorderFont() {
		return getFont(Font.BOLD, 16);
	}

	public static JComboBox<String> createMonthsCombo() {
		JComboBox<String> months = new JComboBox<String>();
		months.addItem(Strings.JAN);
		months.addItem(Strings.FEB);
		months.addItem(Strings.MAR);
		months.addItem(Strings.APR);
		months.addItem(Strings.MAY);
		months.addItem(Strings.JUN);
		months.addItem(Strings.JUL);
		months.addItem(Strings.AUG);
		months.addItem(Strings.SEP);
		months.addItem(Strings.OCT);
		months.addItem(Strings.NOV);
		months.addItem(Strings.DEZ);

		return months;
	}
}
