package br.com.jpsp.services;

import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import br.com.jpsp.model.TaskTypeWrapper;
import br.com.jpsp.model.TypeClassification;
import br.com.jpsp.utils.Utils;

@SuppressWarnings("deprecation")
public class PieChartType {
	private Map<String, TaskTypeWrapper> wrapped;
	private String label;
	private String titleInfo;
	private final TypeClassificationServices services = TypeClassificationServices.instance;

	public PieChartType(Map<String, TaskTypeWrapper> wrapped, String titleInfo) {
		this.wrapped = wrapped;
		this.titleInfo = titleInfo;
	}

	public ChartPanel createChart() {
		ChartPanel panel = new ChartPanel(getChart());
		return panel;
	}

	@SuppressWarnings({ "rawtypes" })
	public JFreeChart getChart() {
		String allTypes = getAllTypes();
		DefaultPieDataset dataset = createDataSet();
		JFreeChart chart = ChartFactory.createPieChart3D(Strings.Chart.TITLE.replaceAll("&1", allTypes) + " - " + this.titleInfo,
				(PieDataset) dataset, true, true, false);

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setStartAngle(270.0D);
		plot.setForegroundAlpha(0.75F);
		plot.setInteriorGap(0.05D);

		return chart;
	}

	private String getAllTypes() {
		Set<TypeClassification> allTypes = services.getAll();
		StringBuilder sb = new StringBuilder("");
		for (TypeClassification tc : allTypes) {
			sb.append("/ " + tc.getDescription());
		}
		return sb.toString().substring(2);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private DefaultPieDataset createDataSet() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		if (this.wrapped != null) {

			for (String key : this.wrapped.keySet()) {
				TaskTypeWrapper value = this.wrapped.get(key);
				this.label = String.valueOf(value.getTaskClass()) + " (" + Utils.getTimeByDelta(value.getTotalTime())
						+ ")";
				dataset.setValue(this.label, new Double(value.getTotalTime()));
			}
		}
		return dataset;
	}
}
