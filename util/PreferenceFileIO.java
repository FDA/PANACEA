package com.eng.cber.na.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import com.eng.cber.na.NetworkAnalysisVisualization;
import com.eng.cber.na.util.json.JSONException;
import com.eng.cber.na.util.json.JSONObject;

/**
 * Class for reading and writing a preferences file for PANACEA.
 * Uses JSON format.
 * 
 * The available preferences are found in command.configCommand
 */

public class PreferenceFileIO {

	private int maxEdgesToDraw, frameIntervalToAnimate, edgeDarkness;
	private boolean debugging;
	
	private String[] prefNames = {"MaxEdgesToDraw", 
								  "FrameIntervalToAnimate",
								  "EdgeDarkness",
							      "Debugging"};
	
	public void writePreferenceFile(String fileName) {
		NetworkAnalysisVisualization nv = NetworkAnalysisVisualization.getInstance();
		
		Object[] prefValues = {nv.getMaxEdgeSizeToDisplay(),
							   nv.getRepaintInterval(),
							   nv.getEdgeDarkness(),
							   nv.isStartLogging()};
		
		JSONObject jsonObject = new JSONObject();
		
		try {
			PrintWriter writer = new PrintWriter(fileName);
			
			// Put information into a JSONObject as key/value pairs.
			for (int i = 0; i < prefNames.length; i++) {
				jsonObject.put(prefNames[i], prefValues[i]);
			}
			
			writer.write(jsonObject.toString(3));
			
			writer.flush();
			writer.close();
						
		} catch (FileNotFoundException fnfe) {
			System.out.println("Could not write Preference File");
			fnfe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean readPreferenceFile(File file) {
		try {
			InputStream inputStream = new FileInputStream(file);
			String fileAsString = org.apache.commons.io.IOUtils.toString(inputStream);
			JSONObject jsonObject = new JSONObject(fileAsString);
		
			maxEdgesToDraw = jsonObject.getInt("MaxEdgesToDraw");
			frameIntervalToAnimate = jsonObject.getInt("FrameIntervalToAnimate");
			edgeDarkness = jsonObject.getInt("EdgeDarkness");
			debugging = jsonObject.getBoolean("Debugging");
			
			// Check if the numbers are within acceptable ranges.
			if (maxEdgesToDraw < 10000)
				throw new PreferenceOutOfBoundsException("\"MaxEdgesToDraw\" cannot be less than 10,000");
			if (frameIntervalToAnimate < 0)
				throw new PreferenceOutOfBoundsException("\"FrameIntervalToAnimate\" cannot be negative");
			if (edgeDarkness < 0 || edgeDarkness > 255)
				throw new PreferenceOutOfBoundsException("\"EdgeDarkness\" must be between 0 and 255");
			
		} catch (FileNotFoundException fnfe) {
			System.out.println("Error: Could not find Preferences file: " + file.getAbsolutePath());
			fnfe.printStackTrace();
			return false;
		} catch (IOException ioe) {
			System.out.println("Error reading Preferences file:" + file.getAbsolutePath());
			ioe.printStackTrace();
			return false;
		} catch (PreferenceOutOfBoundsException poobe) {
			System.out.println("Error in Preferences file: " + poobe.getMessage() + " from file: " + file.getAbsolutePath());
			return false;
		} catch (JSONException jsone) {
			System.out.println("JSON Parsing Error in Preferences file: " + file.getAbsolutePath());
			return false;
		}
		return true;
	}
	
	public int getMaxEdgesToDraw() {
		return maxEdgesToDraw;
	}
	
	public int getFrameIntervalToAnimate() {
		return frameIntervalToAnimate;
	}
	
	public int getEdgeDarkness() {
		return edgeDarkness;
	}
	
	public boolean getDebugging() {
		return debugging;
	}
	
	public class PreferenceOutOfBoundsException extends Exception {
		public PreferenceOutOfBoundsException(String message) {
			super(message);
		}
	}
}
