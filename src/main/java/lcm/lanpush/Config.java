package lcm.lanpush;

import java.time.format.DateTimeFormatter;
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
	private static final String GUI_MINIMIZE_TO_TRAY_KEY = "gui.minimize_to_tray";
	private static final String GUI_START_MINIMIZED_KEY = "gui.start_minimized";
	private static final String GUI_WINDOW_WIDTH_KEY = "gui.window.width";
	private static final String GUI_WINDOW_HEIGHT_KEY = "gui.window.height";
	private static final String GUI_FONT_SIZE_KEY = "gui.font.size";
	private static final String GUI_MESSAGE_DATE_FORMAT_KEY = "gui.message.date_format";
	private static final String GUI_MESSAGE_MAX_LENGTH_KEY = "gui.message.max_length";
	private static final String GUI_ON_RECEIVE_NOTIFY = "gui.on_receive.notify";
	private static final String GUI_ON_RECEIVE_RESTORE = "gui.on_receive.restore";
	
	private static Preferences prefs = Preferences.userRoot().node("lcm.lanpush");

	private Config() {}

	public static void init() {
		OLog.setMinimumLevel(getLogLevel());
		if (Config.getLogPath() != null && !Config.getLogPath().isBlank())
			OLog.setFilePath(Config.getLogPath());
		// else // TODO: DESCOMENTAR QUANDO ESTIVER PRONTO
			OLog.setPrintStream(System.out);

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

	public static void setUdpPort(String port) {
		if (changeInt(CONNECTION_UDP_PORT_KEY, Config.getUdpPort(), port))
			ReceiverHandler.INST.reconnect();
	}

	public static String[] getIp() {
		return prefs.get(CONNECTION_IP_KEY, "192.168.0.255").split(",");
	}

	public static void setIp(String ip) {
		changeString(CONNECTION_IP_KEY, String.join(",", Config.getIp()), ip);
	}

	public static String getLogPath() {
		return prefs.get(LOG_PATH_KEY, "");
	}

	public static void setLogPath(String path) {
		if (changeString(LOG_PATH_KEY, getLogPath(), path)) {
			OLog.setFilePath(path != null && path.isBlank() ? null : path);
			OLog.info("OLog file setted: %s", path);
		}
	}

	public static LogLevel getLogLevel() {
		return LogLevel.valueOf(prefs.get(LOG_LEVEL_KEY, "INFO"));
	}

	public static void setLogLevel(LogLevel level) {
		if (changeString(LOG_LEVEL_KEY, getLogLevel().name(), level.name()))
			OLog.setMinimumLevel(level);
	}

	public static boolean minimizeToTray() {
		return prefs.getBoolean(GUI_MINIMIZE_TO_TRAY_KEY, false);
	}

	public static void setMinimizeToTray(boolean minimize) {
		if (changeBoolean(GUI_MINIMIZE_TO_TRAY_KEY, minimizeToTray(), minimize))
			Lanpush.showWarning("Changing the tray icon visibility will not take effect until you restart the application!");
	}

	public static boolean startMinimized() {
		return prefs.getBoolean(GUI_START_MINIMIZED_KEY, false);
	}

	public static void setStartMinimized(boolean start) {
		changeBoolean(GUI_START_MINIMIZED_KEY, startMinimized(), start);
	}

	public static int getWindowWidth() {
		return prefs.getInt(GUI_WINDOW_WIDTH_KEY, Screen.getScreenWidth() > 1920 ? 1500 : 1000);
	}

	public static void setWindowWidth(String width) {
		if (changeInt(GUI_WINDOW_WIDTH_KEY, getWindowWidth(), width))
			MainWindow.INST.updateSize();
	}

	public static int getProportionalWidth(float proportionPercentage) {
		return Math.round(getWindowWidth() * proportionPercentage / 100);
	}

	public static int getWindowHeight() {
		return prefs.getInt(GUI_WINDOW_HEIGHT_KEY, 500);
	}

	public static void setWindowHeight(String height) {
		if (changeInt(GUI_WINDOW_HEIGHT_KEY, getWindowHeight(), height))
			MainWindow.INST.updateSize();
	}

	public static int getProportionalHeight(float proportionPercentage) {
		return Math.round(getWindowHeight() * proportionPercentage / 100);
	}

	public static int getFontSize() {
		return prefs.getInt(GUI_FONT_SIZE_KEY, 25);
	}

	public static void setFontSize(String size) {
		if (changeInt(GUI_FONT_SIZE_KEY, getFontSize(), size)) {
			SettingsWindow.updateFont();
			MainWindow.INST.updateFont();
		}
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

	public static void setDateFormat(String dateFormat) {
		DateTimeFormatter.ofPattern(dateFormat); // Tests the format so an error is thrown when it's invalid.
		changeString(GUI_MESSAGE_DATE_FORMAT_KEY, getDateFormat(), dateFormat);
	}

	public static int getMaxLength() {
		return prefs.getInt(GUI_MESSAGE_MAX_LENGTH_KEY, 50);
	}

	public static void setMaxLength(String maxLength) {
		changeInt(GUI_MESSAGE_MAX_LENGTH_KEY, getMaxLength(), maxLength);
	}

	public static boolean onReceiveNotify() {
		return prefs.getBoolean(GUI_ON_RECEIVE_NOTIFY, true);
	}

	public static void setOnReceiveNotify(boolean notify) {
		changeBoolean(GUI_ON_RECEIVE_NOTIFY, onReceiveNotify(), notify);
	}

	public static boolean onReceiveRestore() {
		return prefs.getBoolean(GUI_ON_RECEIVE_RESTORE, true);
	}

	public static void setOnReceiveRestore(boolean restore) {
		changeBoolean(GUI_ON_RECEIVE_RESTORE, onReceiveRestore(), restore);
	}

	private static boolean changeInt(String key, int oldValue, String newValueString) {
		int newValue = Integer.parseInt(newValueString);
		if (oldValue != newValue) {
			OLog.info("Changing '%s' from %d to %d", key, oldValue, newValue);
			prefs.putInt(key, newValue);
			return true;
		}
		return false;
	}

	private static boolean changeBoolean(String key, boolean oldValue, boolean newValue) {
		if (oldValue != newValue) {
			OLog.info("Changing '%s' from %b to %b", key, oldValue, newValue);
			prefs.putBoolean(key, newValue);
			return true;
		}
		return false;
	}

	private static boolean changeString(String key, String oldValue, String newValue) {
		if (oldValue == null)
			oldValue = "";
		if (newValue == null)
			newValue = "";
		if (!oldValue.trim().equals(newValue.trim())) {
			OLog.info("Changing '%s' from '%s' to '%s'", key, oldValue, newValue);
			prefs.put(key, newValue);
			return true;
		}
		return false;
	}

}
