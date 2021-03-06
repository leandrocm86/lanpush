package lanpush;

import io.Log;
import system.Sistema;

public class Files {
	
	private static String rootPath = "";
	
	static {
		rootPath = Sistema.getSystemPath();
//		Log.i("Loading files from path " + rootPath);
	}
	
	public static void setTestFolder(String testPath) {
		rootPath = testPath;
		Log.i("Changing path to " + rootPath);
	}
	
	public static String getIconPath() {
		return rootPath + "lanpush.png";
	}
	
	public static String getConfigPath() {
		return rootPath + "lanpush-cfg.ini";
	}
	
	public static String getLogPath() {
		return Config.get("log.file.path", false) != null ? Config.get("log.file.path").val() : rootPath + "lanpush.log";
	}

}
