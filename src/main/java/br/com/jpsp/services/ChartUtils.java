package br.com.jpsp.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import br.com.jpsp.utils.FilesUtils;

public class ChartUtils {

	public static void exportToPNG(JPanel panel) {
		BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		panel.printAll(g);
		g.dispose();
		try { 
		    ImageIO.write(image, "png", new File(FilesUtils.DEFAULT_OUTPUT_FOLDER + FilesUtils.FILE_SEPARATOR + "arquivo.png")); 
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
}
