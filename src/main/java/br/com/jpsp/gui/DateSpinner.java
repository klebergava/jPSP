package br.com.jpsp.gui;

import br.com.jpsp.utils.Utils;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;

public class DateSpinner extends SpinnerDateModel {
	private static final long serialVersionUID = -6463364118905990972L;
	private SpinnerModel linkedModel = null;
	private Boolean checkOnPrevious = null;
	private JLabel dayOfWeek = null;

	private final Refreshable refreshable;

	public DateSpinner(Date initDate, Date earliestDate, Date latestDate, int field, Boolean checkOnPrevious,
			JLabel dayOfWeek, Refreshable refreshable) {
		super(initDate, earliestDate, latestDate, field);
		this.checkOnPrevious = checkOnPrevious;
		this.dayOfWeek = dayOfWeek;
		this.refreshable = refreshable;
	}

	public Boolean getCheckOnPrevious() {
		return this.checkOnPrevious;
	}

	public void setCheckOnPrevious(Boolean checkOnPrevious) {
		this.checkOnPrevious = checkOnPrevious;
	}

	public void setLinkedModel(SpinnerModel linkedModel) {
		this.linkedModel = linkedModel;
	}

	public Object getNextValue() {
		Object value = super.getNextValue();
		Date thisDate = (Date) value;
		Calendar cal = Calendar.getInstance();
		cal.setTime(thisDate);

		if (this.linkedModel != null && this.checkOnPrevious == null) {

			Date linkedModelDate = (Date) this.linkedModel.getValue();

			if (linkedModelDate.before(thisDate)) {
				this.linkedModel.setValue(thisDate);
			}
		}
		updateDayOfWeek(cal.get(7));
		if (this.refreshable != null) {
			this.refreshable.refresh();
		}
		return value;
	}

	public Object getPreviousValue() {
		Object value = super.getPreviousValue();
		Date thisDate = (Date) value;
		Calendar cal = Calendar.getInstance();

		if (thisDate.before(new Date()) && this.checkOnPrevious != null) {
			value = new Date();
		} else if (this.linkedModel != null && this.checkOnPrevious != null && this.checkOnPrevious.booleanValue()) {

			Date linkedModelDate = (Date) this.linkedModel.getValue();

			if (linkedModelDate.after(thisDate)) {
				this.linkedModel.setValue(thisDate);
			}
		}

		cal.setTime((Date) value);
		updateDayOfWeek(cal.get(7));
		if (this.refreshable != null) {
			this.refreshable.refresh();
		}
		return value;
	}

	private void updateDayOfWeek(int dayOfWeekCalendar) {
		if (this.dayOfWeek != null) {
			String txt = Utils.stringRPad(Utils.getDayOfWeek(dayOfWeekCalendar), 15);
			this.dayOfWeek.setText(txt);
		}
	}

	public JLabel getDayOfWeek() {
		return this.dayOfWeek;
	}

	public void setDayOfWeek(JLabel dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public static JSpinner createSpinner(SpinnerModel linkedModel, Boolean checkOnPrevious, JLabel dayOfWeekLabel,
			Font font, Refreshable refreshable) {
		Calendar calendar = Calendar.getInstance();
		Date initDate = calendar.getTime();
		calendar.add(1, -50);
		Date earliestDate = calendar.getTime();
		calendar.add(1, 100);
		Date latestDate = calendar.getTime();
		DateSpinner dateModel = new DateSpinner(initDate, earliestDate, latestDate, 1, checkOnPrevious, dayOfWeekLabel,
				refreshable);
		if (linkedModel != null) {
			dateModel.setLinkedModel(linkedModel);
		}
		JSpinner spinner = new JSpinner(dateModel);
		spinner.setValue(initDate);
		spinner.setFont(font);
		spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy HH:mm:ss"));
		return spinner;
	}
}
