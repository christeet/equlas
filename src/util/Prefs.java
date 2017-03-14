package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Properties;

public class Prefs {

	private static Prefs instance;
	private Properties properties = new Properties();
	

	private String filename = "equals.properties";
	private double windowWidth = 800;
	private double windowHeight = 600;
	private String localeTag = Locale.getDefault().toLanguageTag();
	private boolean isMaximized = false;
	private String lastLoggedInUser = "";
	private String outputPath = "/";
	
	
	private void loadFromProperties() {
		windowWidth = Double.parseDouble(properties.getProperty("windowWidth"));
		windowHeight = Double.parseDouble(properties.getProperty("windowHeight"));
		isMaximized = Boolean.parseBoolean(properties.getProperty("isMaximized"));
		localeTag = properties.getProperty("locale");
		lastLoggedInUser = properties.getProperty("lastLoggedInUser");
		outputPath = properties.getProperty("outputPath");
	}
	
	private void saveToProperties() {
		properties.setProperty("windowWidth", Double.toString(windowWidth));
		properties.setProperty("windowHeight", Double.toString(windowHeight));
		properties.setProperty("isMaximized", Boolean.toString(isMaximized));
		properties.setProperty("locale", localeTag);
		properties.setProperty("lastLoggedInUser", lastLoggedInUser);
		properties.setProperty("outputPath", outputPath);
	}
	
	private Prefs() {
    	InputStream input = null;
    	try {
    		input = new FileInputStream(filename);
    		properties.load(input);
    		loadFromProperties();
    	} catch (IOException ex) {
        } finally {
        	if(input!=null) {
        		try {
        			input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
        }
	}

	private void savePreferences() {
		OutputStream output = null;

		try {
			output = new FileOutputStream(filename);

			saveToProperties();
			
			properties.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private static Prefs getInstance() {
		if(null == instance) {
			instance = new Prefs();
		}
		return instance;
	}
	
	public static Prefs get() {
		return getInstance();
	}
	
	public static void save() {
		getInstance().savePreferences();
	}
	
    public double getWindowWidth() {
        return windowWidth;
    }
    
    public void setWindowWidth(double value) {
        this.windowWidth = value;
    }

    public double getWindowHeight() {
        return windowHeight;
    }
    
    public void setWindowHeight(double value) {
        this.windowHeight = value;
    }

    public boolean getMaximized() {
        return isMaximized;
    }
    
    public void setMaximized(boolean value) {
        this.isMaximized = value;
    }
    
    public String getLocale() {
        return localeTag;
    }
    
    public void setLocale(String value) {
        this.localeTag = value;
    }
    
    public String getLastLoggedInUser() {
        return lastLoggedInUser;
    }
    
    public void setLastLoggedInUser(String value) {
        this.lastLoggedInUser = value;
    }
    
    public String getOutputPath() {
        return outputPath;
    }
    
    public void setOutputPath(String value) {
        this.outputPath = value;
    }
    
}
