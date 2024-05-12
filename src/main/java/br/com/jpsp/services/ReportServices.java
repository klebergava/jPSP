package br.com.jpsp.services;

import br.com.jpsp.gui.SimpleBrowser;
import br.com.jpsp.model.*;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jfree.chart.ChartUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ReportServices {

	private final static Logger log = LogManager.getLogger(ReportServices.class);

	public static final ReportServices instance = new ReportServices();

	private static final TaskServices tasksServices = TaskServices.instance;
	private static final ConfigServices configServices = ConfigServices.instance;

	/**
	 *
	 * @param monthTxt
	 * @param year
	 * @param wrappedType
	 * @param includePieChartType
	 * @param wrappedActivities
	 * @param includePieChartActivity
	 * @param openInDefaultBrowser
	 * @return
	 */
	public File saveSummarizedReport(String monthTxt, int year, Map<String, TaskTypeWrapper> wrappedType,
			boolean includePieChartType, Map<String, TaskActivityWrapper> wrappedActivities,
			boolean includePieChartActivity, boolean openInDefaultBrowser) {


		File html = new File(String.valueOf(FilesUtils.OUTPUT_FOLDER) + FilesUtils.FILE_SEPARATOR + FilesUtils.HTML_REPORT_FILE_NAME);
		if (html.exists()) {
			if (!html.delete()) {
				log.info("Could not delete file " + html.getAbsolutePath());
			}
		}

		try {
			if (html.createNewFile()) {
				StringBuilder content = new StringBuilder();

				content.append("<!DOCTYPE html>\n<html><head><title>Relat&oacute;rio de atividades de ")
						.append(monthTxt).append(" de ")
						.append(year)
						.append(" (Resumido)</title>");

				addCSS(content);

				content.append("</head>\n");
				content.append("<body>\n");

				for (String key : wrappedType.keySet()) {
					TaskTypeWrapper wrapper = wrappedType.get(key);
					content.append("\n<br />\n");

					content.append("<table border='1' width='800'>\n\t<tbody>\n");

					content.append("\t\t<tr>\n");
					content.append("\t\t\t<th colspan='2' bgcolor='#99D9EA'>")
							.append(key)
							.append("</th>\n");
					content.append("\t\t</tr>\n");

					if (wrapper != null) {
						Map<String, Long> times = wrapper.getTaskTimes();
						Long total = 0L;
						for (String k : times.keySet()) {
							Long delta = times.get(k);
							total = total + delta;
							content.append("\t\t<tr>\n");
							content.append("\t\t\t<td width='700'>")
									.append(k)
									.append("</td>\n");
							content.append("\t\t\t<td width='100'>")
									.append(Utils.getTimeByDelta(delta)).append("</td>\n");
							content.append("\t\t</tr>\n");
						}

						content.append("\t\t<tr>\n");
						content.append("\t\t\t<td width='700'><b>TOTAL</b></td>\n");
						content.append("\t\t\t<td width='100'>" + Utils.getTimeByDelta(total) + "</td>\n");
						content.append("\t\t</tr>\n");
					}
					content.append("\t</tbody>\n</table>");

					content.append("\n<br />\n");
				}

				content.append("\t</tbody>\n</table>");

				content.append("\n<br />\n");
				if (includePieChartType) {
					PieChartType pie = new PieChartType(wrappedType, String.valueOf(monthTxt) + "/" + year);
					File png = new File(
							String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR + FilesUtils.PIE_CHART_TYPE_FILE_NAME);
					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
					content.append("<img alt='Pie chart types' src='")
							.append(path)
							.append("' width='1024' height='768' />\n");
					content.append("<br />\n");
				}

				if (includePieChartActivity) {
					PieChartActivity pie = new PieChartActivity(wrappedActivities,
							String.valueOf(monthTxt) + "/" + year);
					File png = new File(String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR
							+ FilesUtils.PIE_CHART_ACTIVITY_FILE_NAME);
					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
					content.append("<img alt='Pie chart activities' src='" + path + "' width='1024' height='768' />\n");
					content.append("<br />\n");
				}

				content.append("\n</body>\n</html>");

				FilesUtils.writeTxtFile(html, content.toString());

				if (openInDefaultBrowser) {
					openBrowser(html);
				} else {
					openHTML(content.toString());
				}
			}
		} catch (IOException e) {
			log.info("Could not generate report (saveSummarizedReport): " + e.getMessage());
			e.printStackTrace();
		}

		return html;
	}

	/**
	 *
	 * @param content
	 */
	private void addCSS(StringBuilder content) {
		content.append("\n<style>\n");
		content.append("table, th, td {\n");
		content.append("\tborder: 1px solid black;\n");
		content.append("\tborder-collapse: collapse;\n");
		content.append("\tpadding: 5px 5px 5px 5px;\n");
		content.append("}\n");
		content.append("th {\n");
		content.append("\tbackground-color: lightblue;\n");
		content.append("}\n");
		content.append("th.sub {\n");
		content.append("\tbackground-color: #DDDDDD;\n");
		content.append("}\n");
		content.append("</style>\n");
	}

	/**
	 *
	 * @param html
	 */
	private void openBrowser(File html) {
		if (Desktop.isDesktopSupported()) {
			try {
				String fullPath = "file:///" + html.getCanonicalPath().replace('\\', '/');
				Desktop.getDesktop().browse(new URI(fullPath));
			} catch (IOException e) {
				log.info("Could not open default OS browser: " + e.getMessage());
				e.printStackTrace();
			} catch (URISyntaxException e) {
				log.info("Wrong URL: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param html
	 */
	private void openHTML(String html) {
		SimpleBrowser browser = new SimpleBrowser(Strings.Report.TITLE, html);
		browser.createAndShow();
	}

	public File saveCompleteReport(String monthTxt, int month, int year, Map<String, TaskTypeWrapper> wrappedTypes,
			boolean includePieChartType, Map<String, TaskActivityWrapper> wrappedActivities,
			boolean includePieChartActivity, boolean openInDefaultBrowser, OrderByDirection order) {

		File html = new File(String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR + FilesUtils.HTML_REPORT_FILE_NAME);
		if (html.exists()) {
			if (!html.delete()) {
				log.info("Could not delete file " + html.getAbsolutePath());
			}
		}

		List<Task> tasks = tasksServices.filterTasksByMonthAndYear(month, year);

		try {
			if (html.createNewFile()) {
				StringBuilder content = new StringBuilder("");

				content.append("<!DOCTYPE html>\n<html><head><title>Relat&oacute;rio de atividades de " + monthTxt
						+ " de " + year + " (Detalhado) </title>");

				addCSS(content);

				content.append("</head>\n");
				content.append("<body>\n");

				content.append("<table border='1' width='1024'>\n\t<tbody>\n");

				content.append("\t\t<tr>\n");
				content.append("\t\t\t<th colspan='8' bgcolor='#99D9EA'>Apontamento de Atividades (" + monthTxt + "/"
						+ year + ")</th>\n");
				content.append("\t\t</tr>\n");

				content.append("\t\t<tr>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Data</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>In&iacute;cio</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>T&eacute;rmino</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Hr. Trab</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>CR/Solic.</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Tipo</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Descri&ccedil;&atilde;o</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Sistema</th>\n");

				content.append("\t\t</tr>\n");

				if (order.isDESC()) {
					Collections.reverse(tasks);
				}

				for (Task t : tasks) {
					content.append("\t\t<tr>\n");
					content.append("\t\t\t<td>" + Utils.date2String(t.getBegin(), "dd/MM/yyyy") + "</td>\n");
					content.append("\t\t\t<td>" + Utils.date2String(t.getBegin(), "HH:mm:ss") + "</td>\n");
					content.append("\t\t\t<td>" + Utils.date2String(t.getEnd(), "HH:mm:ss") + "</td>\n");
					content.append("\t\t\t<td>" + Utils.getTimeByDelta(t.getDelta()) + "</td>\n");
					content.append("\t\t\t<td>" + t.getActivity() + "</td>\n");
					content.append("\t\t\t<td>" + t.getTaskClass() + "</td>\n");
					content.append("\t\t\t<td>" + t.getDescription() + "</td>\n");
					content.append("\t\t\t<td>" + t.getSystem() + "</td>\n");
					content.append("\t\t</tr>\n");
				}

				content.append("\t</tbody>\n</table>");

				content.append("\n<br />\n");
				if (includePieChartType) {
					PieChartType pie = new PieChartType(wrappedTypes, String.valueOf(monthTxt) + "/" + year);

					File png = new File(
							String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR + FilesUtils.PIE_CHART_TYPE_FILE_NAME);
					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
					content.append("<img alt='Pie chart types' src='" + path + "' width='1024' height='768' />\n");
					content.append("<br />\n");
				}

				if (includePieChartActivity) {
					PieChartActivity pie = new PieChartActivity(wrappedActivities,
							String.valueOf(monthTxt) + "/" + year);
					File png = new File(String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR
							+ FilesUtils.PIE_CHART_ACTIVITY_FILE_NAME);

					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
					content.append("<img alt='Pie chart activities' src='" + path + "' width='1024' height='768' />\n");
					content.append("<br />\n");
				}

				content.append("\t</tbody>\n</table>");

				content.append("\n</body>\n</html>");

				FilesUtils.writeTxtFile(html, content.toString());

				if (openInDefaultBrowser) {
					openBrowser(html);
				} else {
					openHTML(content.toString());
				}
			}
		} catch (IOException e) {
			log.info("Could not generate report (saveCompleteReport): " + e.getMessage());
			e.printStackTrace();
		}

		return html;
	}

	private String getOutputFolder() {
		return FilesUtils.OUTPUT_FOLDER;
	}

	/**
	 *
	 * @param monthTxt
	 * @param month
	 * @param year
	 * @param wrappedTypes
	 * @param includePieChartType
	 * @param wrappedActivities
	 * @param includePieChartActivity
	 * @param openInDefaultBrowser
	 * @param order
	 * @return
	 */
	public File saveCompleteGroupedReport(String monthTxt, int month, int year,
			Map<String, TaskTypeWrapper> wrappedTypes, boolean includePieChartType,
			Map<String, TaskActivityWrapper> wrappedActivities, boolean includePieChartActivity,
			boolean openInDefaultBrowser, OrderByDirection order) {
		File html = new File(String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR + FilesUtils.HTML_REPORT_FILE_NAME);
		if (html.exists()) {
			if (!html.delete()) {
				log.info("Could not delete file " + html.getAbsolutePath());
			}
		}

		List<Task> tasks = tasksServices.filterTasksByMonthAndYear(month, year);

		List<Map<String, TaskDateWrapper>> wrappedDates = tasksServices.wrapDates(tasks);

		try {
			if (html.createNewFile()) {
				StringBuilder content = new StringBuilder("");

				content.append("<!DOCTYPE html>\n<html><head><title>Relat&oacute;rio de atividades de " + monthTxt
						+ " de " + year + " (Detalhado, agrupado por data) </title>");

				addCSS(content);

				content.append("</head>\n");
				content.append("<body>\n");

				content.append("<table border='1' width='1024'>\n\t<tbody>\n");

				content.append("\t\t<tr>\n");
				content.append("\t\t\t<th colspan='16' bgcolor='#99D9EA'>Apontamento de Atividades (" + monthTxt + "/"
						+ year + ")</th>\n");
				content.append("\t\t</tr>\n");

				content.append("\t\t<tr>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Data</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>In&iacute;cio</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>T&eacute;rmino</th>\n");
				for (int i = 1; i < 9; i++) {
					content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>I" + i + "</th>\n");
				}

				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Interr.</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Hr. Trab</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>CR/Solic.</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Tipo</th>\n");
				content.append("\t\t\t<th bgcolor='#DDDDDD' class='sub'>Descri&ccedil;&atilde;o</th>\n");

				content.append("\t\t</tr>\n");

				if (order.isDESC()) {
					Collections.reverse(wrappedDates);
				}

				for (Map<String, TaskDateWrapper> map : wrappedDates) {
					String key = map.keySet().iterator().next();
					TaskDateWrapper dateWrap = map.get(key);
					content.append("\t\t<tr>\n");
					content.append("\t\t\t<td>").append(key).append("</td>\n");
					content.append("\t\t\t<td>").append(dateWrap.getBeginHour()).append("</td>\n");
					content.append("\t\t\t<td>").append(dateWrap.getEndHour()).append("</td>\n");
					int[] intervals = dateWrap.getIntervalsAsArrayOfSize(8);
					byte b;
					int j, arrayOfInt1[];
					for (j = (arrayOfInt1 = intervals).length, b = 0; b < j;) {
						int k = arrayOfInt1[b];
						content.append("\t\t\t<td >").append(k).append("</td>\n");
						b++;
					}
					content.append("\t\t\t<td>").append(dateWrap.getInterruption()).append("</td>\n");
					content.append("\t\t\t<td>").append(dateWrap.getWorkedHours()).append("</td>\n");
					content.append("\t\t\t<td>").append(dateWrap.getActivity()).append("</td>\n");
					content.append("\t\t\t<td>").append(dateWrap.getTaskClass()).append("</td>\n");
					content.append("\t\t\t<td>").append(dateWrap.getDescription()).append("</td>\n");
					content.append("\t\t</tr>\n");
				}

				content.append("\t</tbody>\n</table>");

				content.append("\n<br />\n");
				if (includePieChartType) {
					PieChartType pie = new PieChartType(wrappedTypes, String.valueOf(monthTxt) + "/" + year);
					File png = new File(
							String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR + FilesUtils.PIE_CHART_TYPE_FILE_NAME);
					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
					content.append("<img alt='Pie chart types' src='").append(path).append("' width='1024' height='768' />\n");
					content.append("<br />\n");
				}

				if (includePieChartActivity) {
					PieChartActivity pie = new PieChartActivity(wrappedActivities,
							String.valueOf(monthTxt) + "/" + year);
					File png = new File(String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR
							+ FilesUtils.PIE_CHART_ACTIVITY_FILE_NAME);
					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
					content.append("<img alt='Pie chart activities' src='" + path + "' width='1024' height='768' />\n");
					content.append("<br />\n");
				}

				content.append("\t</tbody>\n</table>");

				content.append("\n</body>\n</html>");

				FilesUtils.writeTxtFile(html, content.toString());

				if (openInDefaultBrowser) {
					openBrowser(html);
				} else {
					openHTML(content.toString());
				}
			}
		} catch (IOException e) {
			log.info("Could not generate report (saveCompleteGroupedReport): " + e.getMessage());
			e.printStackTrace();
		}

		return html;
	}

	/**
	 * @param monthTxt
	 * @param month
	 * @param year
	 * @param wrappedTypes
	 * @param includePieChartType
	 * @param wrappedActivities
	 * @param includePieChartActivity
	 * @param directory
	 * @param order
	 * @return
	 */
	public File saveCompleteGroupedReportExcel(String monthTxt, int month, int year,
			Map<String, TaskTypeWrapper> wrappedTypes, boolean includePieChartType,
			Map<String, TaskActivityWrapper> wrappedActivities, boolean includePieChartActivity,
			String directory, OrderByDirection order) {
		Configuration config = configServices.getConfiguration();

		String userName = "";
		if (!Utils.isEmpty(config.getName())) {
			userName = String.valueOf(config.getName()) + "_";
		}
		File excelFile = new File(String.valueOf(directory) + FilesUtils.FILE_SEPARATOR + "Apontamento_" + userName
				+ monthTxt + "_" + year + ".xls");
		if (excelFile.exists()) {
			if (!excelFile.delete()) {
				log.info("Could not delete file " + excelFile.getAbsolutePath());
			}
		}

		List<Task> tasks = tasksServices.filterTasksByMonthAndYear(month, year);

		int beginRow = 0, endRow = 0;

		List<Map<String, TaskDateWrapper>> wrappedDates = tasksServices.wrapDates(tasks);

		try {
			if (excelFile.createNewFile()) {
				int rowCount = 0;
				HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();

				// ABA DETALHADO
				//**********************
				Sheet sheet = hSSFWorkbook.createSheet(Strings.Excel.DETAILED);

				sheet.setColumnWidth(0, 2816);
				sheet.setColumnWidth(1, 2560);
				sheet.setColumnWidth(2, 2560);
				sheet.setColumnWidth(3, 1536);
				sheet.setColumnWidth(4, 1536);
				sheet.setColumnWidth(5, 1536);
				sheet.setColumnWidth(6, 1536);
				sheet.setColumnWidth(7, 1536);
				sheet.setColumnWidth(8, 1536);
				sheet.setColumnWidth(9, 1536);
				sheet.setColumnWidth(10, 1536);
				sheet.setColumnWidth(11, 2048);
				sheet.setColumnWidth(12, 2816);
				sheet.setColumnWidth(13, 13824);
				sheet.setColumnWidth(14, 2048);
				sheet.setColumnWidth(15, 13824);

				Row titleRow = sheet.createRow(rowCount++);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue(Strings.Excel.TITLE);
				titleCell.setCellStyle(titleStyle((Workbook) hSSFWorkbook));

				sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$P$1"));

				Row row = sheet.createRow(rowCount++);
				Cell cell = row.createCell(0);
				cell.setCellValue("");
				sheet.addMergedRegion(CellRangeAddress.valueOf("$A$2:$P$2"));

				row = sheet.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(Strings.Excel.NAME + ":");
				cell.setCellStyle(infoStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(1);
				cell.setCellValue(config.getName());
				cell.setCellStyle(infoStyle((Workbook) hSSFWorkbook));
				sheet.addMergedRegion(CellRangeAddress.valueOf("$B$3:$P$3"));

				row = sheet.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue("");
				sheet.addMergedRegion(CellRangeAddress.valueOf("$A$4:$P$4"));

				row = sheet.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(Strings.Excel.TIME + ":");
				cell.setCellStyle(infoStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(1);
				Task first = tasks.iterator().next();
				Task last = null;
				for (Task t : tasks) {
					last = t;
				}
				cell.setCellValue(String.valueOf(first.getDay()) + " a " + last.getDay() + " de " + monthTxt);
				cell.setCellStyle(infoStyle((Workbook) hSSFWorkbook));
				sheet.addMergedRegion(CellRangeAddress.valueOf("$B$5:$P$5"));

				row = sheet.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue("");
				sheet.addMergedRegion(CellRangeAddress.valueOf("$A$6:$P$6"));

				sheet.createFreezePane(0, 7);

				row = sheet.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(Strings.Excel.COL_DATE);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(1);
				cell.setCellValue(Strings.Excel.COL_START);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(2);
				cell.setCellValue(Strings.Excel.COL_END);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(3);
				cell.setCellValue("I1");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(4);
				cell.setCellValue("I2");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(5);
				cell.setCellValue("I3");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(6);
				cell.setCellValue("I4");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(7);
				cell.setCellValue("I5");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(8);
				cell.setCellValue("I6");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(9);
				cell.setCellValue("I7");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(10);
				cell.setCellValue("I8");
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(11);
				cell.setCellValue(Strings.Excel.COL_INTERRUPTION);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(12);
				cell.setCellValue(Strings.Excel.COL_HOURS);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(13);
				cell.setCellValue(Strings.Excel.COL_TASK);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(14);
				cell.setCellValue(Strings.Excel.COL_TYPE);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));
				cell = row.createCell(15);
				cell.setCellValue(Strings.Excel.COL_DESCRIPTION);
				cell.setCellStyle(subTitleStyle((Workbook) hSSFWorkbook));

//				StringBuilder content = new StringBuilder();

				int cellCount = 0;
				beginRow = rowCount + 1;

				if (order.isDESC()) {
					Collections.reverse(wrappedDates);
				}

				for (Map<String, TaskDateWrapper> map : wrappedDates) {
					cellCount = 0;

					row = sheet.createRow(rowCount++);
					String key = map.keySet().iterator().next();
					TaskDateWrapper dateWrap = map.get(key);

					// data
					cell = row.createCell(cellCount++);
					cell.setCellValue(key);
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook,
							hSSFWorkbook.createDataFormat().getFormat("dd/mm/yyyy")));
					cell = row.createCell(cellCount++);

					// início
					cell.setCellValue(dateWrap.getBeginHour());
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook,
							hSSFWorkbook.createDataFormat().getFormat("[hh]:mm")));
					cell = row.createCell(cellCount++);

					// fim
					cell.setCellValue(dateWrap.getEndHour());
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook,
							hSSFWorkbook.createDataFormat().getFormat("[hh]:mm")));

					// intervalos (i1 até i8)
					int[] intervals = dateWrap.getIntervalsAsArrayOfSize(8);
					byte b;
					int j;
                    int[] arrayOfInt1;
                    for (j = (arrayOfInt1 = intervals).length, b = 0; b < j;) {
						int k = arrayOfInt1[b];
						cell = row.createCell(cellCount++);
						cell.setCellValue(k);
						cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook, (short) 0));

						b++;
					}

					cell = row.createCell(cellCount++);

					// soma total
					cell.setCellStyle(
							contentStyle((Workbook) hSSFWorkbook, hSSFWorkbook.createDataFormat().getFormat("[mm]")));
					cell.setCellFormula("SUM(D" + rowCount + ":K" + rowCount + ")/1440");

					// horas trabalhadas
					cell = row.createCell(cellCount++);
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook,
							hSSFWorkbook.createDataFormat().getFormat("[hh]:mm")));

					cell.setCellFormula("(C" + rowCount + " - B" + rowCount + ") - L" + rowCount);

					// Atividade
					cell = row.createCell(cellCount++);
					cell.setCellValue(dateWrap.getActivity());
					cell.setCellStyle(contentTextStyle((Workbook) hSSFWorkbook));

					// Tipo
					cell = row.createCell(cellCount++);
					cell.setCellValue(dateWrap.getTaskClassAsInt());
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook, (short) 0));

					// descrição
					cell = row.createCell(cellCount++);
					cell.setCellValue(dateWrap.getDescription());
					cell.setCellStyle(contentTextStyle((Workbook) hSSFWorkbook));
				}

				endRow = rowCount;

				row = sheet.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(Strings.Excel.TOTAL);
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
				sheet.addMergedRegion(CellRangeAddress.valueOf("$A$" + rowCount + ":$L$" + rowCount));
				int i;
				for (i = 1; i < 12; i++) {
					cell = row.createCell(i);
					cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
					cell.setCellValue("");
				}

				cell = row.createCell(12);
				cell.setCellStyle(
						totalStyle((Workbook) hSSFWorkbook, hSSFWorkbook.createDataFormat().getFormat("[hh]:mm")));
				cell.setCellFormula("SUM($M$" + beginRow + ":$M$" + endRow + ")");

				cell = row.createCell(13);
				cell.setCellValue("");
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));

				for (i = 13; i < 16; i++) {
					cell = row.createCell(i);
					cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
					cell.setCellValue("");
				}

				sheet.addMergedRegion(CellRangeAddress.valueOf("$N$" + rowCount + ":$P$" + rowCount));

				if (includePieChartType) {
					PieChartType pie = new PieChartType(wrappedTypes, String.valueOf(monthTxt) + "/" + year);
					File png = new File(
							String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR + FilesUtils.PIE_CHART_TYPE_FILE_NAME);
					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
//					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
//					content.append("<img alt='Pie chart types' src='" + path + "' width='1024' height='768' />\n");
				}

				if (includePieChartActivity) {
					PieChartActivity pie = new PieChartActivity(wrappedActivities,
							String.valueOf(monthTxt) + "/" + year);
					File png = new File(String.valueOf(getOutputFolder()) + FilesUtils.FILE_SEPARATOR
							+ FilesUtils.PIE_CHART_ACTIVITY_FILE_NAME);
					ChartUtils.saveChartAsPNG(png, pie.getChart(), 1024, 768);
//					String path = "file:///" + png.getCanonicalPath().replace('\\', '/');
//					content.append("<img alt='Pie chart activities' src='" + path + "' width='1024' height='768' />\n");
				}

				rowCount = 0;

				// ABA DE CONSOLIDADDOS
				//**********************
				Sheet sheet2 = hSSFWorkbook.createSheet(Strings.Excel.CONSOLIDATED);

				sheet2.setColumnWidth(0, 13824);
				sheet2.setColumnWidth(1, 3072);
				sheet2.setColumnWidth(2, 3072);
				sheet2.setColumnWidth(3, 3072);
				sheet2.setColumnWidth(4, 3072);

				row = sheet2.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(Strings.Excel.COL_KIND);
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
				cell = row.createCell(1);
				cell.setCellValue(Strings.Excel.COL_SYSTEM);
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
				cell = row.createCell(2);
				cell.setCellValue(Strings.Excel.COL_DESENV);
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
				cell = row.createCell(3);
				cell.setCellValue(Strings.Excel.COL_DESENV);
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
				cell = row.createCell(4);
				cell.setCellValue(Strings.Excel.COL_OTHERS);
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));

				for (String key : wrappedActivities.keySet()) {
					TaskActivityWrapper value = wrappedActivities.get(key);
					row = sheet2.createRow(rowCount++);

					cell = row.createCell(0);
					cell.setCellValue(value.getActivity());
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook, (short) 1));

					cell = row.createCell(1);
					cell.setCellValue("");
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook, (short) 1));

					cell = row.createCell(2);

					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook,
							hSSFWorkbook.createDataFormat().getFormat("[hh]:mm")));

					cell.setCellFormula("SUMPRODUCT(--((Detalhado!$N$" + beginRow + ":$N$" + endRow + "=TRIM(A"
							+ rowCount + "))*(Detalhado!$O$" + beginRow + ":$O$" + endRow + "=1)), Detalhado!$M$"
							+ beginRow + ":$M$" + endRow + ")");

					cell = row.createCell(3);
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook,
							hSSFWorkbook.createDataFormat().getFormat("[hh]:mm")));

					cell.setCellFormula("SUMPRODUCT(--((Detalhado!$N$" + beginRow + ":$N$" + endRow + "=TRIM(A"
							+ rowCount + "))*(Detalhado!$O$" + beginRow + ":$O$" + endRow + "=2)), Detalhado!$M$"
							+ beginRow + ":$M$" + endRow + ")");

					cell = row.createCell(4);
					cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook,
							hSSFWorkbook.createDataFormat().getFormat("[hh]:mm")));

					cell.setCellFormula("SUMPRODUCT(--((Detalhado!$N$" + beginRow + ":$N$" + endRow + "=TRIM(A"
							+ rowCount + "))*(Detalhado!$O$" + beginRow + ":$O$" + endRow + "=3)), Detalhado!$M$"
							+ beginRow + ":$M$" + endRow + ")");
				}

				rowCount = 0;

				// ABA DE TABELAS
				//**********************
				Sheet sheet3 = hSSFWorkbook.createSheet(Strings.Excel.TABLES);

				sheet3.setColumnWidth(0, 1536);
				sheet3.setColumnWidth(1, 5376);

				row = sheet3.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(Strings.Excel.TASK_TYPE);
				cell.setCellStyle(totalStyle((Workbook) hSSFWorkbook, (short) 1));
				sheet3.addMergedRegion(CellRangeAddress.valueOf("$A$1:$B$1"));

				row = sheet3.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(1.0D);
				cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook, (short) 0));
				cell = row.createCell(1);
				cell.setCellValue(Strings.Excel.DEVELOPMENT);
				cell.setCellStyle(contentTextStyle((Workbook) hSSFWorkbook));

				row = sheet3.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(2.0D);
				cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook, (short) 0));
				cell = row.createCell(1);
				cell.setCellValue(Strings.Excel.COL_CORRECTION);
				cell.setCellStyle(contentTextStyle((Workbook) hSSFWorkbook));

				row = sheet3.createRow(rowCount++);
				cell = row.createCell(0);
				cell.setCellValue(3.0D);
				cell.setCellStyle(contentStyle((Workbook) hSSFWorkbook, (short) 0));
				cell = row.createCell(1);
				cell.setCellValue(Strings.Excel.CONFIG_OTHERS);
				cell.setCellStyle(contentTextStyle((Workbook) hSSFWorkbook));

				FileOutputStream out = new FileOutputStream(excelFile);
				hSSFWorkbook.write(out);
				out.close();
			}

		} catch (IOException e) {
			log.info("Could not generate report (saveCompleteGroupedReportExcel): " + e.getMessage());
			e.printStackTrace();
		}

		return excelFile;
	}

	private CellStyle titleStyle(Workbook wb) {
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 24);
		titleFont.setColor(IndexedColors.BLACK.getIndex());
		CellStyle style = wb.createCellStyle();

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(titleFont);

		return style;
	}

	private CellStyle subTitleStyle(Workbook wb) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 8);
		font.setBold(true);
		font.setColor(IndexedColors.BLACK.getIndex());
		CellStyle style = wb.createCellStyle();

		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);

		short borderColor = IndexedColors.BLACK.getIndex();

		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(borderColor);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(borderColor);
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(borderColor);
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(borderColor);

		return style;
	}

	private CellStyle totalStyle(Workbook wb, short format) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);

		font.setColor(IndexedColors.BLACK.getIndex());
		CellStyle style = wb.createCellStyle();

		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.RIGHT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);

		short borderColor = IndexedColors.BLACK.getIndex();

		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(borderColor);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(borderColor);
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(borderColor);
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(borderColor);

		style.setDataFormat(format);

		return style;
	}

	private CellStyle infoStyle(Workbook wb) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		font.setColor(IndexedColors.BLACK.getIndex());
		CellStyle style = wb.createCellStyle();

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);

		return style;
	}

	private CellStyle contentStyle(Workbook wb, short format) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setColor(IndexedColors.BLACK.getIndex());
		CellStyle style = wb.createCellStyle();

		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);

		short borderColor = IndexedColors.BLACK.getIndex();

		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(borderColor);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(borderColor);
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(borderColor);
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(borderColor);

		style.setDataFormat(format);

		return style;
	}

	private CellStyle contentTextStyle(Workbook wb) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 9);
		font.setColor(IndexedColors.BLACK.getIndex());
		CellStyle style = wb.createCellStyle();

		style.setAlignment(HorizontalAlignment.LEFT);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(font);

		short borderColor = IndexedColors.BLACK.getIndex();

		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(borderColor);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(borderColor);
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(borderColor);
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(borderColor);

		style.setDataFormat((short) 1);

		return style;
	}

}
