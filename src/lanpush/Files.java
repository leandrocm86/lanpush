package lanpush;

import system.Sistema;

public class Files {
	
	private static String rootPath = Sistema.getSystemPath();
	
	public static void setTestFolder(String testPath) {
		rootPath = testPath;
	}
	
	public static String getLogPath() {
		return null;
	}
	
	public static String getIconPath() {
		return null;
	}
	
	public static String getConfigPath() {
		return rootPath + "config.ini";
	}

}
