package com.zeus.graph;

import java.util.Locale;
import java.util.ResourceBundle;

public class Resources {

	private static ResourceBundle resourceBundle_;
	private static ResourceBundle defaultResourceBundle_;
	
	private static final Locale DEFAULT_LOCALE = new Locale("en", "US");
	
	private static final String COMPANY_NAME = "Zeus";
	private static final String TOOL_NAME = "Graph Editor";
	private static final String TOOL_VERSION = "0.0.5";
	private static final String TOOL_URL = "http://...";
	private static final String AUTHORS = "Laurent Bindschaedler";
	
	public static void initialize(String _bundleName, Locale _locale) {
		try {
			defaultResourceBundle_ = ResourceBundle.getBundle(_bundleName, DEFAULT_LOCALE);
		} catch(Exception e) {
			// ignored
		}
		
		try {
			resourceBundle_ = ResourceBundle.getBundle(_bundleName, _locale);
		} catch(Exception e) {
			// ignored
		}
	}
	
	public static String resource(String _resourceName) {
		if(_resourceName != null && resourceBundle_ != null && resourceBundle_.containsKey(_resourceName)) {
			return resourceBundle_.getString(_resourceName);
		} else if(_resourceName != null && defaultResourceBundle_ != null && defaultResourceBundle_.containsKey(_resourceName)) {
			return defaultResourceBundle_.getString(_resourceName);
		} else {
			return null;
		}
	}
	
	public static String companyName() {
		return COMPANY_NAME;
	}
	
	public static String toolName() {
		return TOOL_NAME;
	}
	
	public static String fullToolName() {
		return COMPANY_NAME + " " + TOOL_NAME;
	}
	
	public static String toolVersion() {
		return TOOL_VERSION;
	}
	
	public static String fullToolNameVersion() {
		return fullToolName() + " " + toolVersion();
	}
	
	public static String toolURL() {
		return TOOL_URL;
	}
	
	public static String authors() {
		return AUTHORS;
	}
	
	public static String arg(String _resourceString, String _arg1) {
		if(_resourceString == null || _arg1 == null) {
			return _resourceString;
		} else {
			return _resourceString.replaceFirst("%\\d+", _arg1);
		}
	}
	
}