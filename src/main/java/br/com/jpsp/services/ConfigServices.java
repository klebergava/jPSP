package br.com.jpsp.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import br.com.jpsp.model.Configuration;
import br.com.jpsp.utils.FilesUtils;

public class ConfigServices {
	private static final String KEY = "CONFIGURATION_INSTANCE";
	private static final Map<String, Configuration> BUFFER = new HashMap<String, Configuration>();
	public static final ConfigServices instance = new ConfigServices();
	
	private ConfigServices() {
		checkFile();
	}
	
	private void checkFile() {
		try {
			File datFile = new File(FilesUtils.USER_CONFIG_DATA_FILE);
			if (!datFile.exists()) {
				createEmptyFile();
			}
			
			File oldDatFile = new File(FilesUtils.OLD_USER_CONFIG_DATA_FILE);
			if (oldDatFile.exists()) {
				oldDatFile.delete();
			}
		} catch (Exception ex) {}
	}

	private void createEmptyFile() {
		Configuration defaultConfig = new Configuration();
		defaultConfig.setAutoPause(1);
		defaultConfig.setAlertTime("17:00");
		defaultConfig.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		defaultConfig.setName(FilesUtils.USER_NAME);
		defaultConfig.setAutoStart(0);
		
		File outputFolder = new File(FilesUtils.DEFAULT_OUTPUT_FOLDER);
		try {
			defaultConfig.setOutputFolder(outputFolder.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.updateConfiguration(defaultConfig);
	}

	public synchronized Configuration getConfiguration() {
		checkFile();
		
		Configuration instance = BUFFER.get(KEY);
		if (instance == null) {
			
	        try {
	        	File datFile = new File(FilesUtils.USER_CONFIG_DATA_FILE);
	            FileInputStream fileIn = new FileInputStream(datFile);
	            ObjectInputStream in = new ObjectInputStream(fileIn);
	            instance = (Configuration) in.readObject();
	            in.close();
	            fileIn.close();
	        } catch (IOException i) {
	            i.printStackTrace();
	        } catch (ClassNotFoundException c) {
	            System.out.println("Employee class not found");
	            c.printStackTrace();
	        }
			
			BUFFER.put(KEY, instance);
		}

		return instance;
	}
	
	/**
	 * 
	 * @param config
	 * @return
	 */
	public synchronized boolean updateConfiguration(Configuration config) {
		boolean updated = false;
		
        try {
        	File datFile = new File(FilesUtils.USER_CONFIG_DATA_FILE);
            FileOutputStream fileOut = new FileOutputStream(datFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(config);
            out.close();
            fileOut.close();
            
            BUFFER.put(KEY, config);
            
            updated = true;
        } catch (IOException i) {
            i.printStackTrace();
        }
		
		
		return updated;
	}
	
}
