package br.com.jpsp.gui.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.resources.Images;
import br.com.jpsp.model.Task;
import br.com.jpsp.model.TaskActivityWrapper;
import br.com.jpsp.model.TaskTypeWrapper;
import br.com.jpsp.services.OrderByDirection;
import br.com.jpsp.services.PieChartActivity;
import br.com.jpsp.services.PieChartType;
import br.com.jpsp.services.ReportServices;
import br.com.jpsp.services.Strings;
import br.com.jpsp.services.TaskServices;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

/**
 *
 */
public class ReportWindow extends JFrame {
	private static final long serialVersionUID = 4353742431227760939L;
	private final static Logger log = LogManager.getLogger(ReportWindow.class);

	private final TaskServices taskServices = TaskServices.instance;
	private final ReportServices reportServices = ReportServices.instance;

	private JComboBox<String> months;
	private JComboBox<Integer> years;
	private JComboBox<OrderByDirection> orderBy;
	private JButton generateHTML;
	private JButton pieChartByType;
	private JButton pieChartByActivity;
	private final ButtonGroup radios = new ButtonGroup();
	private JButton generateExcel;
	private JTextField filePath;
	private JCheckBox openInDefaultBrowser;
	private JCheckBox includePieChartType;
	private JCheckBox includePieChartActivity;
	private final JRadioButton summarized = new JRadioButton(Strings.Report.SUMMARY, true);
	private final JRadioButton complete = new JRadioButton(Strings.Report.DETAILED, true);
	private final JRadioButton completeGrouped = new JRadioButton(Strings.Report.DETAILED_GROUPED, true);

	public ReportWindow() {
		super(Strings.Report.TITLE);
		Gui.setConfiguredLookAndFeel(this);
	}

	public void createAndShow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(mountMain(), "Center");

		setIconImage(Images.REPORT_MINI_IMG);
		pack();
		setLocationRelativeTo(this);
		setResizable(false);
		setVisible(true);
	}

	private JPanel mountMain() {
		JPanel main = new JPanel(new BorderLayout());

		main.setBorder(Gui.getLinedBorder(Strings.Report.TITLE, Gui.getFont(1, Integer.valueOf(16)), Color.WHITE));
		main.setBackground(GuiSingleton.DARK_BG_COLOR);

		this.months = Gui.createMonthsCombo();
		this.months.setSelectedIndex(Utils.getCurrentMonth());

		this.years = new JComboBox<Integer>();
		Gui.loadYearsComboUntilCurrent(2000, this.years);

		this.orderBy = new JComboBox<OrderByDirection>();
		this.orderBy.addItem(OrderByDirection.ASC);
		this.orderBy.addItem(OrderByDirection.DESC);
		this.orderBy.setSelectedIndex(0);

		this.generateHTML = new JButton(Strings.Report.HTML);
		this.generateHTML.setIcon(Images.HTML_ICON);
		this.generateHTML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReportWindow.this.generateHTMLReport();
			}
		});

		this.generateExcel = new JButton(Strings.Report.EXCEL);
		this.generateExcel.setIcon(Images.EXCEL_ICON);
		this.generateExcel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReportWindow.this.generateReportExcel();
			}
		});

		this.pieChartByType = new JButton(Strings.Report.TYPES);
		this.pieChartByType.setIcon(Images.PIE_CHART);
		this.pieChartByType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReportWindow.this.showPieChartType();
			}
		});

		this.pieChartByActivity = new JButton(Strings.Report.TASK_CHART);
		this.pieChartByActivity.setIcon(Images.PIE_CHART);
		this.pieChartByActivity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReportWindow.this.showPieChartActivity();
			}
		});

		this.openInDefaultBrowser = new JCheckBox(Strings.Report.OPEN_DEFAULT_BROWSER);
		this.includePieChartType = new JCheckBox(Strings.Report.INCLUDE_GRAPH);
		this.includePieChartActivity = new JCheckBox(Strings.Report.INCLUDE_TASK_GRAPH);

		JPanel htmlSettings = new JPanel(new SpringLayout());
		htmlSettings.setBorder(new EmptyBorder(5, 5, 5, 5));

		htmlSettings.add(this.openInDefaultBrowser);
		htmlSettings.add(this.includePieChartType);
		htmlSettings.add(this.includePieChartActivity);

		JPanel radioButtons = new JPanel(new FlowLayout());
		radioButtons.add(this.summarized);
		this.radios.add(this.summarized);
		radioButtons.add(this.complete);
		this.radios.add(this.complete);
		radioButtons.add(this.completeGrouped);
		this.radios.add(this.completeGrouped);
		htmlSettings.add(radioButtons);


		JPanel chartButtons = new JPanel(new FlowLayout());
		chartButtons.add(this.pieChartByType);
		chartButtons.add(this.pieChartByActivity);
		htmlSettings.add(chartButtons);

		htmlSettings.setBorder(Gui.getTitledBorder(Strings.Report.HTML_OPTIONS, Gui.getFont(1, Integer.valueOf(13)), Color.BLUE));

		Gui.makeCompactGrid(htmlSettings, 5, 1, 0, 0, 5, 5);

		JPanel options = new JPanel(new BorderLayout());
		options.add(htmlSettings, "South");

		JPanel dateParams = new JPanel(new SpringLayout());
		dateParams.setBorder(new EmptyBorder(5, 5, 5, 5));

		dateParams.add(new JLabel(Strings.Report.MONTH + ": "));
		dateParams.add(this.months);

		dateParams.add(new JLabel(Strings.Report.YEAR + ": "));
		dateParams.add(this.years);

		dateParams.add(new JLabel(Strings.Report.ORDER_BY + ": "));
		dateParams.add(this.orderBy);
		Gui.makeCompactGrid(dateParams, 1, 6, 5, 5, 5, 5);

		JPanel dateParamsPanel = new JPanel(new BorderLayout());
		dateParamsPanel.setBorder(Gui.getTitledBorder(Strings.Report.DATE_PARAMS, Gui.getFont(1, Integer.valueOf(13)), Color.BLUE));
		dateParamsPanel.add(dateParams, "Center");

		options.add(dateParamsPanel, "Center");

		JPanel info = new JPanel(new BorderLayout());
		info.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.filePath = new JTextField("", 60);
		this.filePath.setEditable(false);
		this.filePath.setForeground(Color.BLUE);
		info.add(new JLabel(Strings.Report.GENERATED_FILE + ": "), "West");
		info.add(this.filePath, "Center");

		JButton exit = new JButton(Strings.GUI.CANCEL);
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ReportWindow.this.dispose();
			}
		});

		JPanel buttonsPanel = new JPanel(new BorderLayout());
		buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		buttonsPanel.add(exit, "West");

		info.add(buttonsPanel, "South");

		JPanel reportButtons = new JPanel(new SpringLayout());
		reportButtons.setBorder(Gui.getEmptyBorder(5));
		reportButtons.add(this.generateHTML);
		reportButtons.add(this.generateExcel);
		Gui.makeCompactGrid(reportButtons, 2, 1, 10, 10, 10, 10);

		main.add(options, "Center");
		main.add(reportButtons, "East");
		main.add(info, "South");

		return main;
	}

	/**
	 *
	 */
	private void generateHTMLReport() {
		GuiSingleton.showLoadingScreen(Strings.LOADING_GENERATE_REPORT, true, 0, 0);

		String monthTxt = Objects.requireNonNull(this.months.getSelectedItem()).toString();
		int month = this.months.getSelectedIndex();
		int year = Integer.parseInt(Objects.requireNonNull(this.years.getSelectedItem()).toString());

		new Thread( () -> {
			synchronized (ReportWindow.this) {
				File html = null;
				boolean isComplete = this.complete.isSelected();
				boolean isCompleteGrouped = this.completeGrouped.isSelected();
				Map<String, TaskTypeWrapper> wrappedType = getWrappedTaskTasksTypes();
				Map<String, TaskActivityWrapper> wrappedActivity = getWrappedTaskTasksActivities();

				if (isComplete) {
					html = this.reportServices.saveCompleteReport(monthTxt, month, year, wrappedType,
							this.includePieChartType.isSelected(), wrappedActivity, this.includePieChartActivity.isSelected(),
							this.openInDefaultBrowser.isSelected(), (OrderByDirection)this.orderBy.getSelectedItem());
				} else if (isCompleteGrouped) {
					html = this.reportServices.saveCompleteGroupedReport(monthTxt, month, year, wrappedType,
							this.includePieChartType.isSelected(), wrappedActivity, this.includePieChartActivity.isSelected(),
							this.openInDefaultBrowser.isSelected(), (OrderByDirection)this.orderBy.getSelectedItem());
				} else {
					html = this.reportServices.saveSummarizedReport(monthTxt, year, wrappedType,
							this.includePieChartType.isSelected(), wrappedActivity, this.includePieChartActivity.isSelected(),
							this.openInDefaultBrowser.isSelected());
				}

				GuiSingleton.disposeLoadingScreen();

				if (html.exists()) {
					try {
						this.filePath.setText(html.getCanonicalPath());
						if (this.openInDefaultBrowser.isSelected()) {
							String message = Strings.Report.SUCCESS.replaceAll("&1", html.getCanonicalPath());
							Gui.showMessage(this, message);
						}
					} catch (IOException e) {
						log.error("generateHTMLReport()" + e.getMessage());
						e.printStackTrace();
					}
				} else {
					Gui.showErrorMessage(this, Strings.Report.ERROR);
					log.error(Strings.Report.ERROR);
				}
			}
		}).start();


	}

	private void generateReportExcel() {

		String monthTxt = Objects.requireNonNull(this.months.getSelectedItem()).toString();
		int month = this.months.getSelectedIndex();
		int year = Integer.parseInt(Objects.requireNonNull(this.years.getSelectedItem()).toString());

		Map<String, TaskTypeWrapper> wrappedType = getWrappedTaskTasksTypes();
		Map<String, TaskActivityWrapper> wrappedActivity = getWrappedTaskTasksActivities();

		String outputFolder = FilesUtils.OUTPUT_FOLDER;

		JFileChooser fc = new JFileChooser(
				new File(String.valueOf(FilesUtils.USER_HOME_DIR) + FilesUtils.FILE_SEPARATOR + "Desktop"));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			GuiSingleton.showLoadingScreen(Strings.LOADING_GENERATE_REPORT, true, 0, 0);
			File dir = fc.getSelectedFile();
			try {
				outputFolder = dir.getCanonicalPath();
			} catch (IOException e) {
				log.error("generateReportExcel()" + e.getMessage());
				GuiSingleton.disposeLoadingScreen();
				e.printStackTrace();
			}
		} else {
			return;
		}

		final String outputFile = outputFolder;

		new Thread(() -> {

			synchronized (ReportWindow.this) {

				try {
					File excel = this.reportServices.saveCompleteGroupedReportExcel(monthTxt, month, year, wrappedType,
							this.includePieChartType.isSelected(), wrappedActivity, this.includePieChartActivity.isSelected(),
							outputFile, (OrderByDirection)this.orderBy.getSelectedItem());
					GuiSingleton.disposeLoadingScreen();

					if (excel.exists()) {
						try {
							this.filePath.setText(excel.getCanonicalPath());
							Gui.showMessage(ReportWindow.this, Strings.Report.EXCEL_GENERATED.replaceAll("&1", excel.getCanonicalPath()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						Gui.showErrorMessage(ReportWindow.this, Strings.Report.ERROR);
						log.error("generateReportExcel() " + Strings.Report.ERROR + " " + excel.getAbsolutePath());
					}
				} catch (Exception ex) {
					GuiSingleton.disposeLoadingScreen();
					log.error("generateReportExcel()" + ex.getMessage());
					ex.printStackTrace();
				}

			}
		}).start();;

	}

	/**
	 *
	 * @return
	 */
	private Map<String, TaskTypeWrapper> getWrappedTaskTasksTypes() {
		int month = this.months.getSelectedIndex();
		int year = Integer.parseInt(Objects.requireNonNull(this.years.getSelectedItem()).toString());
		List<Task> tasks = this.taskServices.getTasksOfPeriod(month, year);
		Collections.sort(tasks, new Comparator<Task>() {

			@Override
			public int compare(Task o1, Task o2) {
				return o1.getBegin().compareTo(o2.getBegin());
			}
		});
        return this.taskServices.wrapType(tasks);
	}

	/**
	 *
	 * @return
	 */
	private Map<String, TaskActivityWrapper> getWrappedTaskTasksActivities() {
		int month = this.months.getSelectedIndex();
		int year = Integer.parseInt(Objects.requireNonNull(this.years.getSelectedItem()).toString());
		List<Task> tasks = this.taskServices.getTasksOfPeriod(month, year);

		Collections.sort(tasks, new Comparator<Task>() {

			@Override
			public int compare(Task o1, Task o2) {
				return o1.getBegin().compareTo(o2.getBegin());
			}
		});

        return this.taskServices.wrapActivity(tasks);
	}

	private void showPieChartType() {
		Map<String, TaskTypeWrapper> wrapped = getWrappedTaskTasksTypes();
		JFrame frame = new JFrame(Strings.Report.CHART_TYPE);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(Images.PIE_CHART_IMG);

		String monthTxt = Objects.requireNonNull(this.months.getSelectedItem()).toString();
		int year = Integer.parseInt(Objects.requireNonNull(this.years.getSelectedItem()).toString());

		PieChartType pie = new PieChartType(wrapped, String.valueOf(monthTxt) + "/" + year);
		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add((Component) pie.createChart(), "Center");

		frame.pack();
		frame.setVisible(true);
	}

	private void showPieChartActivity() {
		Map<String, TaskActivityWrapper> wrapped = getWrappedTaskTasksActivities();
		JFrame frame = new JFrame(Strings.Report.TASK_CHART);
		frame.setDefaultCloseOperation(2);

		String monthTxt = Objects.requireNonNull(this.months.getSelectedItem()).toString();
		int year = Integer.parseInt(Objects.requireNonNull(this.years.getSelectedItem()).toString());

		PieChartActivity pie = new PieChartActivity(wrapped, String.valueOf(monthTxt) + "/" + year);
		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().add((Component) pie.createChart(), "Center");

		frame.pack();
		frame.setVisible(true);
	}
}
