package br.com.jpsp.model;

import java.io.Serializable;

/**
 * 
 * @author kleber
 *
 */
public class Configuration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6994679600973452766L;
	
	private int autoPause;
	private String lookAndFeel;
	private String alertTime;
	private String name;
	private int autoStart;
	private Object[] combosValues = new Object[4];
	
	private final int version = 1;

	public int getAutoPause() {
		return this.autoPause;
	}

	public void setAutoPause(int autoPause) {
		this.autoPause = autoPause;
	}

	public String getLookAndFeel() {
		return this.lookAndFeel;
	}

	public void setLookAndFeel(String lookAndFeel) {
		this.lookAndFeel = lookAndFeel;
	}

	public boolean isAutoPause() {
		return (this.autoPause == 1);
	}

	public String getAlertTime() {
		return this.alertTime;
	}

	public void setAlertTime(String alertTime) {
		this.alertTime = alertTime;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAutoStart() {
		return this.autoStart;
	}

	public void setAutoStart(int autoStart) {
		this.autoStart = autoStart;
	}

	public boolean isAutoStart() {
		return (this.autoStart == 1);
	}
	
	public int version() {
		return version;
	}

	public Object[] getCombosValues() {
		return combosValues;
	}

	public void setCombosValues(Object[] combosValues) {
		this.combosValues = combosValues;
	}

	@Override
	public String toString() {
		return "Configuration [autoPause=" + autoPause + ", lookAndFeel=" + lookAndFeel + ", alertTime=" + alertTime
				+ ", name=" + name + ", autoStart=" + autoStart + ", version=" + version
				+ "]";
	}

}
