package br.com.jpsp.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.Refreshable;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.CRUD;
import br.com.jpsp.services.CRUDServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

public class CRUDWindow<T extends CRUDServices<CRUD>> extends JDialog {
	private final static Logger log = LogManager.getLogger(CRUDWindow.class);
	private static final long serialVersionUID = -4084846662008987183L;
	private Refreshable refreshable;

	private final T services;
	private JList<CRUD> list;
	private JScrollPane scroll;
	private CRUD[] data;

	private final CRUD instance;

	public CRUDWindow(Refreshable refreshable, String title, Image img, T services, CRUD instance) {
		super();
		this.setTitle(title);
		this.setModal(true);
		this.refreshable = refreshable;
		this.setIconImage(img);
		this.services = services;
		this.instance = instance;
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mountMain(), "Center");
		setSize(640, 350);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private JPanel mountMain() {
		final JPanel main = new JPanel(new BorderLayout());
		main.setBorder(Gui.getLinedBorder(CRUDWindow.this.getTitle(), Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));
		main.setBackground(GuiSingleton.DARK_BG_COLOR);
		Set<? extends CRUD> items = this.services.getAll();
		this.data = new CRUD[items.size()];
		int i = 0;
		for (CRUD t : items) {
			this.data[i] = t;
			i++;
		}
		this.list = new JList<CRUD>();
		this.list.setCellRenderer(new MyCustomListRenderer());
		this.list.setModel(new MyListModel(this.data));
		this.list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					editItem();
				}
			}
		});

		this.scroll = new JScrollPane(this.list);
		JPanel buttons = new JPanel(new GridLayout(3, 1));
		JButton button = new JButton(Strings.Form.EDIT, Images.EDIT);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editItem();
			}

		});
		buttons.add(button);
		button = new JButton(Strings.Form.EXCLUDE, Images.DEL);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTxt();
			}
		});
		buttons.add(button);
		button = new JButton(Strings.Form.INCLUDE, Images.ADD);
		button.addActionListener(new ActionListener() {
			@SuppressWarnings("rawtypes")
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				CRUDWindow<T>.MiniEdit edit = new CRUDWindow.MiniEdit(null);
				edit.createAndShow();
			}
		});
		buttons.add(button);
		JPanel closePanel = new JPanel(new FlowLayout());
		button = new JButton(Strings.GUI.OK);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CRUDWindow.this.closeWindow();
			}
		});
		closePanel.add(button);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(Gui.getEmptyBorder(5));
		mainPanel.add(buttons, "East");
		mainPanel.add(this.scroll, "Center");
		mainPanel.add(closePanel, "South");
		main.add(mainPanel, "Center");
		return main;
	}

	private void closeWindow() {
		dispose();
		if (this.refreshable != null)
			this.refreshable.refresh();
	}

	class MyListModel extends AbstractListModel<CRUD> {
		private static final long serialVersionUID = 3201094740621433881L;
		private List<CRUD> data;

		MyListModel(CRUD[] data) {
			this.data = new ArrayList<CRUD>(Arrays.asList(data));
		}

		public int getSize() {
			return this.data.size();
		}

		public CRUD getElementAt(int index) {
			return this.data.get(index);
		}

		public void removeElement(int index) {
			this.data.remove(index);
			fireIntervalRemoved(this.data, index, index);
		}

		public CRUD[] getData() {
			return this.data.<CRUD>toArray(new CRUD[this.data.size()]);
		}

		public void add(CRUD newTxt) {
			this.data.add(newTxt);
			Collections.sort(this.data);
		}

		public void remove(CRUD newTxt) {
			this.data.remove(newTxt);
			Collections.sort(this.data);
		}
	}

	/**
	 *
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void editItem() {
		if (this.list.getSelectedIndex() > 0) {
			CRUDWindow<T>.MyListModel m = (MyListModel) CRUDWindow.this.list.getModel();
			CRUD toEdit = m.getElementAt(CRUDWindow.this.list.getSelectedIndex());
			CRUDWindow<T>.MiniEdit edit = new CRUDWindow.MiniEdit(toEdit);
			edit.createAndShow();
		} else {
			Gui.showErrorMessage(this, Strings.Form.ERROR_SELECT_ITEM);
			log.trace(Strings.Form.ERROR_SELECT_ITEM);
		}
	}

	/**
	 *
	 */
	private void deleteTxt() {
		if (this.list.getSelectedIndex() > 0) {

			int answer = Gui.showConfirmMessage(this, Strings.Form.CONFIRM_EXCLUSION.replaceAll("&1", this.list.getSelectedValue().toString()));

			if (answer == JOptionPane.OK_OPTION) {
				MyListModel m = (MyListModel) this.list.getModel();
				CRUD toRemove = m.getElementAt(this.list.getSelectedIndex());
				try {
					this.services.remove(toRemove);
					m.removeElement(this.list.getSelectedIndex());
				} catch (Exception e) {
					log.error("deleteTxt() " + e.getMessage());
					Gui.showErrorMessage(CRUDWindow.this, e.getMessage());
					e.printStackTrace();
				}

			}
		} else {
			log.trace(Strings.Form.ERROR_SELECT_ITEM);
			Gui.showErrorMessage(this, Strings.Form.ERROR_SELECT_ITEM);
		}
	}

	private void refreshlList(boolean include, CRUD newItem, CRUD oldItem) {
		MyListModel m = (MyListModel) this.list.getModel();

		if (include) {
			this.services.add(newItem);
			m.add(newItem);
			this.data = m.getData();
			this.list.setModel(new MyListModel(this.data));
			this.list.revalidate();
		} else {
			try {
				this.services.remove(oldItem);

				this.services.add(newItem);
				m.remove(oldItem);
				m.add(newItem);
				this.data = m.getData();
				this.list.setModel(new MyListModel(this.data));
				this.list.revalidate();
			} catch (Exception e) {
				log.error("refreshlList() " + e.getMessage());
				Gui.showErrorMessage(CRUDWindow.this, e.getMessage());
				e.printStackTrace();
			}

		}
	}


	class MiniEdit extends JFrame {
		private static final long serialVersionUID = -8993577068607053069L;
		private JTextField txt;
		private CRUD toEdit;
		private boolean include;

		public MiniEdit(CRUD toEdit) {
			super(CRUDWindow.this.getTitle());
			this.toEdit = toEdit;
			this.include = toEdit == null;
			Gui.setConfiguredLookAndFeel(this);
		}

		public void createAndShow() {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			this.setIconImage(Images.TASK_IMG);

			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(mountMain(), "Center");

			setSize(400, 300);
			pack();
			setLocationRelativeTo(CRUDWindow.this);
			setResizable(false);
			setVisible(true);
			setAlwaysOnTop(true);
		}

		private JPanel mountMain() {
			JPanel main = new JPanel(new BorderLayout());
			main.setBorder(Gui.getEmptyBorder(10));

			JButton button = new JButton(Strings.GUI.OK);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CRUDWindow.MiniEdit.this.closeMiniEdit();
				}
			});

			this.txt = new JTextField(this.toEdit == null ? "" : this.toEdit.toString(), 30);
			this.txt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					CRUDWindow.MiniEdit.this.closeMiniEdit();
				}
			});

			main.add(new JLabel(Strings.Form.ITEM_TEXT), "North");
			main.add(this.txt, "Center");
			main.add(button, "East");

			return main;
		}

		protected void closeMiniEdit() {

			if (Utils.isEmpty(this.txt.getText())) {
				Gui.showErrorMessage(this, Strings.Form.ERROR_MANDATORY_FIELD);
			} else {
				CRUDWindow.this.refreshlList(this.include, CRUDWindow.this.instance.from(this.txt.getText()), this.toEdit);
				dispose();
			}
		}
	}

	public class MyCustomListRenderer extends DefaultListCellRenderer {

	    /**
		 *
		 */
		private static final long serialVersionUID = -7254795982083286653L;

		@Override
	    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
	    		boolean cellHasFocus) {
	    	Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

	    	if (isSelected) {
	    		c.setBackground(Color.LIGHT_GRAY);
	    	}

	    	CRUD crud = (CRUD)value;
	    	if (crud.isBlocked()) {
	    		this.setText(crud.toString() + " (" + Strings.jPSP.BLOCKED + ")");
	    		c.setForeground(Color.RED);
	    	}
	    	return c;
	    }

	}

}
