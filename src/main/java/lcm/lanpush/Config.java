package lcm.lanpush;

import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import lcm.java.swing.CustomFont;
import lcm.java.swing.Screen;
import lcm.java.system.logging.LogLevel;
import lcm.java.system.logging.OLog;

public class Config {

	
	private static final String CONNECTION_UDP_PORT_KEY = "connection.udp_port";
	private static final String CONNECTION_IP_KEY = "connection.ip";
	private static final String LOG_PATH_KEY = "log.file.path";
	private static final String LOG_LEVEL_KEY = "log.level";
	private static final String GUI_MINIMIZE_KEY = "gui.minimize_to_tray";
	private static final String GUI_WINDOW_WIDTH_KEY = "gui.window.width";
	private static final String GUI_WINDOW_HEIGHT_KEY = "gui.window.height";
	private static final String GUI_FONT_SIZE_KEY = "gui.font.size";
	private static final String GUI_MESSAGE_DATE_FORMAT_KEY = "gui.message.date_format";
	private static final String GUI_MESSAGE_MAX_LENGTH_KEY = "gui.message.max_length";
	private static final String GUI_ON_RECEIVE_NOTIFY = "gui.on_receive.notify";
	private static final String GUI_ON_RECEIVE_RESTORE = "gui.on_receive.restore";
	
	private static Preferences prefs = Preferences.userRoot().node("lcm.lanpush");

	private Config() {
		// File file = new File(prefs.absolutePath());
		// if (!file.exists()) {
		// 	try {
		// 		prefs.exportNode(new FileOutputStream(file));
		// 		OLog.info("Preferences file created.");
		// 	} catch (IOException | BackingStoreException e) {
		// 		OLog.error(e, "Error while trying to save preferences file at '%s'", prefs.absolutePath());
		// 	}
		// }
		if (getLogLevel() == LogLevel.DEBUG) {
			try {
				String loadedPrefs = List.of(prefs.keys()).stream().map(key -> key + " = " + prefs.get(key, null)).collect(Collectors.joining("\n"));
				OLog.debug("Loaded preferences:\n%s", loadedPrefs);
			} catch (BackingStoreException e) {
				OLog.error(e, "Could not print preferences!");
			}
		}
	}

	public static int getUdpPort() {
		return prefs.getInt(CONNECTION_UDP_PORT_KEY, 1050);
	}

	public static String[] getIp() {
		return prefs.get(CONNECTION_IP_KEY, "192.168.0.255").split(",");
	}

	public static String getLogPath() {
		return prefs.get(LOG_PATH_KEY, null);
	}

	public static LogLevel getLogLevel() {
		return LogLevel.valueOf(prefs.get(LOG_LEVEL_KEY, "INFO"));
	}

	public static boolean minimizeToTray() {
		return prefs.getBoolean(GUI_MINIMIZE_KEY, false);
	}

	public static int getWindowWidth() {
		return prefs.getInt(GUI_WINDOW_WIDTH_KEY, Screen.getScreenWidth() > 1920 ? 1500 : 1000);
	}

	public static int getProportionalWidth(float proportionPercentage) {
		return Math.round(getWindowWidth() * proportionPercentage / 100);
	}

	public static int getWindowHeight() {
		return prefs.getInt(GUI_WINDOW_HEIGHT_KEY, 500);
	}

	public static int getProportionalHeight(float proportionPercentage) {
		return Math.round(getWindowHeight() * proportionPercentage / 100);
	}

	public static int getFontSize() {
		return prefs.getInt(GUI_FONT_SIZE_KEY, 35);
	}

	public static CustomFont getProportionalFont(float proportionPercentage) {
		return new CustomFont("Arial", Math.round(getFontSize() * proportionPercentage / 100));
	}

	public static CustomFont getDefaultFont() {
		return new CustomFont("Arial", getFontSize());
	}

	public static String getDateFormat() {
		return prefs.get(GUI_MESSAGE_DATE_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss");
	}

	public static int getMaxLength() {
		return prefs.getInt(GUI_MESSAGE_MAX_LENGTH_KEY, 50);
	}

	public static boolean onReceiveNotify() {
		return prefs.getBoolean(GUI_ON_RECEIVE_NOTIFY, true);
	}

	public static boolean onReceiveRestore() {
		return prefs.getBoolean(GUI_ON_RECEIVE_RESTORE, true);
	}

}
