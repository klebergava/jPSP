package br.com.jpsp.utils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import br.com.jpsp.model.Task;

public class Utils {
	public static final String DD_MM_YYYY = "dd/MM/yyyy";
	public static final String DD_MM_YYYY_HH_mm_ss = "dd/MM/yyyy HH:mm:ss";
	public static final String HH_mm_ss = "HH:mm:ss";
	public static final String HH_mm = "HH:mm";
	public static final String _00_00_00 = "   00:00:00   ";
	public static final String _00_00 = "   00:00   ";
	public static final String YYYYMMDD_HHMMSS = "yyyyMMdd_HHmmss";
	public static final String DD_MM_YYYY_HH_mm = "dd/MM/yyyy HH:mm";
	public static final double JAVA_VERSION = getVersion();
	public static final double JAVA_MIN_VERSION = 1.8D;
	
	public static final long _1_HOUR_MILI = 3600000L;
	public static final long _1_MINUTE_MILI = 60000L;
	public static final long _1_SECOND_MILI = 1000L;
	public static final long _1_DAY_MILI = _1_HOUR_MILI * 24;
	
	public static final String DEFAULT_SEPARATOR = ";";

	public static int SCREEN_WIDTH = 0;
	public static int SCREEN_HEIGHT = 0;

	static {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		SCREEN_WIDTH = (int) screenSize.getWidth();
		SCREEN_HEIGHT = (int) screenSize.getHeight();
	}

	public static double getVersion() {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos + 1);
		return Double.parseDouble(version.substring(0, pos));
	}

	public static boolean isNumber(String s) {
		boolean isNumero = false;

		if (s == null || "".equals(s.trim())) {
			return false;
		}

		try {
			isNumero = true;
		} catch (NumberFormatException numberFormatException) {
		}

		return isNumero;
	}

	public static String date2String(Date date, String format) {
		String str = (new SimpleDateFormat(format, new Locale("pt", "BR"))).format(date);
		return str;
	}

	public static Date string2Date(String dateStr, String format) {
		Date date = null;
		if (dateStr != null) {
			SimpleDateFormat formatador = new SimpleDateFormat(format);
			formatador.setLenient(false);

			date = formatador.parse(dateStr, new ParsePosition(0));
		}
		return date;
	}

	public static String format(double d) {
		DecimalFormat df = new DecimalFormat("###,###,##0.00");
		return df.format(d);
	}

	public static String format(double d, String ptrn) {
		DecimalFormat df = new DecimalFormat(ptrn);
		return df.format(d);
	}

	public static String toDoubleStr(String d) {
		String valueStr = d.replaceAll("[.]", "");
		valueStr = valueStr.replace(',', '.');
		return valueStr;
	}

	public static boolean isEmpty(String str) {
		return !(str != null && !str.trim().equals(""));
	}

	public static boolean isEmpty(Collection<?> collection) {
		return !(collection != null && !collection.isEmpty());
	}

	public static String getTimeByDelta(long delta) {
		String time = "";

		long hours = (long)(delta / _1_HOUR_MILI);
		delta -= (hours * _1_HOUR_MILI);
		
		long minutes = (long)(delta / _1_MINUTE_MILI);
		delta -= (minutes * _1_MINUTE_MILI);
		
		long seconds = (long)(delta / _1_SECOND_MILI);
		
		time = String.format("%02d", new Object[] { Long.valueOf(hours) }) 
				+ ":" + String.format("%02d", new Object[] { Long.valueOf(minutes) })
				+ ":" + String.format("%02d", new Object[] { Long.valueOf(seconds) });
		
		return time;
		/*
		String time = "";

		long seconds = delta / 1000L;
		long minutes = seconds / 60L;
		seconds -= minutes * 60L;
		long hours = minutes / 60L;

		minutes -= hours * 60L;

		time = String.valueOf(String.format("%02d", new Object[] { Long.valueOf(hours) })) + ":"
				+ String.format("%02d", new Object[] { Long.valueOf(minutes) }) + ":"
				+ String.format("%02d", new Object[] { Long.valueOf(seconds) });

		return time;
		*/
	}

	public static String getTimeByDelta(long delta, boolean showSeconds) {
		if (showSeconds) {
			return getTimeByDelta(delta);
		}

		String time = "";

		long hours = (long)(delta / _1_HOUR_MILI);
		delta -= (hours * _1_HOUR_MILI);
		
		long minutes = (long)(delta / _1_MINUTE_MILI);
		delta -= (minutes * _1_MINUTE_MILI);
		
		time = String.format("%02d", new Object[] { Long.valueOf(hours) }) 
				+ ":" + String.format("%02d", new Object[] { Long.valueOf(minutes) });
		
		return time;		
		/*
		String time = "";

		long seconds = delta / 1000L;
		long minutes = seconds / 60L;
		seconds -= minutes * 60L;
		long hours = minutes / 60L;

		minutes -= hours * 60L;

		time = String.valueOf(String.format("%02d", new Object[] { Long.valueOf(hours) })) + ":"
				+ String.format("%02d", new Object[] { Long.valueOf(minutes) });

		return time;
		*/
	}

	public static String getShortTimeByDelta(long delta) {
		String time = "";

		long hours = (long)(delta / _1_HOUR_MILI);
		delta -= (hours * _1_HOUR_MILI);
		
		long minutes = (long)(delta / _1_MINUTE_MILI);
		delta -= (minutes * _1_MINUTE_MILI);
		
		time = String.format("%02d", new Object[] { Long.valueOf(hours) }) 
				+ ":" + String.format("%02d", new Object[] { Long.valueOf(minutes) });
		
		return time;
		/*
		long seconds = delta / 1000L;
		long minutes = seconds / 60L;
		seconds -= minutes * 60L;
		long hours = minutes / 60L;

		minutes -= hours * 60L;

		time = String.valueOf(String.format("%02d", new Object[] { Long.valueOf(hours) })) + ":"
				+ String.format("%02d", new Object[] { Long.valueOf(minutes) });

		return time;
		*/
	}

	public static String getMinutesByDelta(long delta) {
		String time = "";
		long seconds = delta / 1000L;
		long minutes = seconds / 60L;
		time = Long.toString(minutes);

		return time;
	}

	public static long getDeltaByTime(String time) {
		long delta = 0L;

		if (time != null) {
			time = time.trim();
			String[] split = time.split("[:]");
			String h = split[0];
			String m = split[1];
			String s = split[2];

			long seconds = (new Long(s)).longValue();
			long minutes = (new Long(m)).longValue();
			long hours = (new Long(h)).longValue();

			minutes *= 60L;
			seconds += minutes;

			hours *= 60L;
			hours *= 60L;
			seconds += hours;

			delta = 1000L * seconds;
		}

		return delta;
	}

	public static int getCurrentMonth() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		return cal.get(2);
	}

	public static int getCurrentDay() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		return cal.get(5);
	}

	public static boolean isEven(long num) {
		return (num % 2L == 0L);
	}

	public static String getDayOfWeek(int dayOfWeekCalendar) {
		String dayOfWeek = "";
		String[] weekDays = getWeekDaysStrings();
		dayOfWeek = weekDays[dayOfWeekCalendar];
		return dayOfWeek;
	}

	public static String[] getWeekDaysStrings() {
		String[] weekDays = (new DateFormatSymbols()).getWeekdays();
		int lastIndex = weekDays.length - 1;

		if (weekDays[lastIndex] == null || weekDays[lastIndex].length() <= 0) {
			String[] weekDaysStrings = new String[lastIndex];
			System.arraycopy(weekDays, 0, weekDaysStrings, 0, lastIndex);
			return weekDaysStrings;
		}
		return weekDays;
	}

	public static String stringRPad(String txt, int size) {
		StringBuffer fixed = new StringBuffer(txt);
		if (txt != null && fixed.length() < size) {
			int diff = size - fixed.length();
			for (int i = 1; i <= diff; i++) {
				fixed.append(' ');
			}
		}

		return fixed.toString();
	}

	public static int getCurrentYear() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		return cal.get(1);
	}

	public static long getInterval(Task previousTask, Task currentTask) {
		long interval = 0L;
		Date end = previousTask.getEnd();
		Date begin = currentTask.getBegin();

		if (begin != null && end != null && end.before(begin)) {
			interval = begin.getTime() - end.getTime();
		}

		return interval;
	}

	public static String toUpper(String str) {
		String upper = str;
		if (upper == null) {
			upper = "";
		}
		return upper.toUpperCase();
	}

	public static Date sumMinutesToDate(Date date, int minutes) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(12, minutes);
		return cal.getTime();
	}

	public static String dayOfWeek(Date today) {
		String day = "";
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		switch (cal.get(7)) {
		case 2:
			day = "Segunda-feira";
			break;
		case 3:
			day = "Ter�a-feira";
			break;
		case 4:
			day = "Quarta-feira";
			break;
		case 5:
			day = "Quinta-feira";
			break;
		case 6:
			day = "Sexta-feira";
			break;
		case 7:
			day = "S�bado";
			break;
		case 1:
			day = "Domingo";
			break;
		}
		return day;
	}

	public static String toLower(String str) {
		String upper = str;
		if (upper == null) {
			upper = "";
		}
		return upper.toLowerCase();
	}

	public static String lpad(String string, int tamanho, char caracter) {
		String novaString = string;

		if (string != null) {
			if (tamanho > 0) {
				int qtdCaracteres = tamanho - string.length();
				if (qtdCaracteres >= 0) {
					for (; qtdCaracteres > 0; qtdCaracteres--) {
						novaString = caracter + novaString;
					}
				} else {
					novaString = novaString.substring(0, tamanho);
				}
			} else {
				novaString = "";
			}
		}
		return novaString;
	}

}
