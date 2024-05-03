package br.com.jpsp.services;

import br.com.jpsp.model.TaskActivityWrapper;
import br.com.jpsp.utils.Utils;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

@SuppressWarnings("deprecation")
public class PieChartActivity {
	private Map<String, TaskActivityWrapper> wrapped;
	private String label;
	private String titleInfo;

	public PieChartActivity(Map<String, TaskActivityWrapper> wrapped, String titleInfo) {
		this.wrapped = wrapped;
		this.titleInfo = titleInfo;
	}

	public ChartPanel createChart() {
		ChartPanel panel = new ChartPanel(getChart());
		return panel;
	}

	public JFreeChart getChart() {
		DefaultPieDataset<String> dataset = createDataSet();
		JFreeChart chart = ChartFactory.createPieChart3D("Atividades (CR/Sol./etc) - " + this.titleInfo,
				(PieDataset<String>) dataset, true, true, false);

		PiePlot3D plot = (PiePlot3D) chart.getPlot();
		plot.setStartAngle(270.0D);
		plot.setForegroundAlpha(0.75F);
		plot.setInteriorGap(0.05D);

		return chart;
	}

	private DefaultPieDataset<String> createDataSet() {
		DefaultPieDataset<String> dataset = new DefaultPieDataset<String>();
		if (this.wrapped != null) {

			for (String key : this.wrapped.keySet()) {
				TaskActivityWrapper value = this.wrapped.get(key);
				this.label = String.valueOf(value.getActivity()) + " (" + Utils.getTimeByDelta(value.getTotalTime())
						+ ")";
				dataset.setValue(this.label, new Double(value.getTotalTime()));
			}
		}
		return dataset;
	}
}

