package br.com.jpsp;

import javax.swing.SwingUtilities;

import br.com.jpsp.gui.GuiSingleton;
import br.com.jpsp.gui.jPSP;
import br.com.jpsp.services.Strings;
import br.com.jpsp.utils.FilesUtils;
import br.com.jpsp.utils.Gui;
import br.com.jpsp.utils.Utils;

public class Main {
	public static void main(String[] a) {
		GuiSingleton.showSplash();
		
		if (Utils.JAVA_VERSION < Utils.JAVA_MIN_VERSION) {
			Gui.showErrorMessage(null, Strings.JAVA_VERSION_ERROR);

			System.exit(0);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				jPSP jpsp = new jPSP();
				jpsp.createAndShow();

				FilesUtils.verifyDBBackup();
			}
		});
	}
}
