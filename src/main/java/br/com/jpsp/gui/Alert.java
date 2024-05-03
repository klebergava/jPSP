package br.com.jpsp.gui;

import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Alert extends JFrame {
	private static final long serialVersionUID = 4185852602000435375L;
	private Thread thread;
	private boolean running = false;
	private long timeToAlert;
	private String timeToAlertStr;

	public Alert(String time) {
		super(Strings.Alert.TITLE);
		Gui.setConfiguredLookAndFeel(this);
		this.timeToAlertStr = time;
		if (!Utils.isEmpty(time)) {
			this.timeToAlert = Long.parseLong(time.replaceAll(":", ""));
		}
	}

	public void start() {
		this.thread = new Thread() {
			public void run() {
				Date now = new Date();
				long timeNow = 0L;

				while (Alert.this.running) {
					try {
						Thread.sleep(60000L);
					} catch (InterruptedException interruptedException) {
					}

					now = new Date();
					String tmp = Utils.date2String(now, "HH:mm");
					timeNow = Long.parseLong(tmp.replaceAll(":", ""));

					if (timeNow >= Alert.this.timeToAlert && Alert.this.running) {
						JOptionPane.showMessageDialog(Alert.this, Strings.Alert.TIME_REACHED + ": " + Alert.this.timeToAlertStr,
								Strings.Alert.TIME_ALERT, 0);

						Alert.this.stop();
					}
				}
			}
		};

		this.running = true;
		this.thread.start();
	}

	public void stop() {
		this.running = false;
	}
}
