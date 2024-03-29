package lcm.lanpush;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
	
	public static final String CONNECTION_UDP_PORT_KEY = "connection.udp_port";
	public static final String CONNECTION_IP_KEY = "connection.ip";
	public static final String LOG_PATH_KEY = "log.file.path";
	public static final String LOG_LEVEL_KEY = "log.level";
	public static final String GUI_MINIMIZE_TO_TRAY_KEY = "gui.minimize_to_tray";
	public static final String GUI_START_MINIMIZED_KEY = "gui.start_minimized";
	public static final String GUI_WINDOW_WIDTH_KEY = "gui.window.width";
	public static final String GUI_WINDOW_HEIGHT_KEY = "gui.window.height";
	public static final String GUI_FONT_SIZE_KEY = "gui.font.size";
	public static final String GUI_MESSAGE_DATE_FORMAT_KEY = "gui.message.date_format";
	public static final String GUI_MESSAGE_MAX_LENGTH_KEY = "gui.message.max_length";
	public static final String GUI_ON_RECEIVE_NOTIFY = "gui.on_receive.notify";
	public static final String GUI_ON_RECEIVE_RESTORE = "gui.on_receive.restore";

	public static final String EVENT_CONFIG_CHANGED = "event.config_changed.";
	
	private static PropertyChangeSupport propertyObservable = new PropertyChangeSupport(new Config());
	
	private Preferences prefs = Preferences.userRoot().node("lcm.lanpush");

	private static final Config instance = new Config();

	private Config() {
		OLog.setMinimumLevel(getLogLevel());
		if (getLogPath() != null && !getLogPath().isBlank())
			OLog.setFilePath(getLogPath());
		else 
			OLog.setPrintStream(System.out);

		if (getLogLevel() == LogLevel.DEBUG) {
			try {
				String loadedPrefs = List.of(prefs.keys()).stream().map(key -> key + " = " + prefs.get(key, null)).collect(Collectors.joining("\n"));
				OLog.debug("Loaded preferences from '%s':\n%s", prefs.absolutePath(), loadedPrefs);
			} catch (BackingStoreException e) {
				OLog.error(e, "Could not print preferences!");
			}
		}
	}

	public static Config getInstance() {
		return instance;
	}

	public int getUdpPort() {
		return prefs.getInt(CONNECTION_UDP_PORT_KEY, 1050);
	}

	public void setUdpPort(String port) {
		changeInt(CONNECTION_UDP_PORT_KEY, getUdpPort(), port);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyObservable.addPropertyChangeListener(listener);
	}

	public String[] getIp() {
		return prefs.get(CONNECTION_IP_KEY, "192.168.0.255").split(",");
	}

	public void setIp(String ip) {
		changeString(CONNECTION_IP_KEY, String.join(",", getIp()), ip);
	}

	public String getLogPath() {
		return prefs.get(LOG_PATH_KEY, "");
	}

	public void setLogPath(String path) {
		if (changeString(LOG_PATH_KEY, getLogPath(), path)) {
			OLog.setFilePath(path != null && path.isBlank() ? null : path);
			OLog.info("OLog file setted: %s", path);
		}
	}

	public LogLevel getLogLevel() {
		return LogLevel.valueOf(prefs.get(LOG_LEVEL_KEY, "INFO"));
	}

	public void setLogLevel(LogLevel level) {
		if (changeString(LOG_LEVEL_KEY, getLogLevel().name(), level.name()))
			OLog.setMinimumLevel(level);
	}

	public boolean minimizeToTray() {
		return prefs.getBoolean(GUI_MINIMIZE_TO_TRAY_KEY, false);
	}

	public void setMinimizeToTray(boolean minimize) {
		if (changeBoolean(GUI_MINIMIZE_TO_TRAY_KEY, minimizeToTray(), minimize))
			Lanpush.showWarning("Changing the tray icon visibility will not take effect until you restart the application!");
	}

	public boolean startMinimized() {
		return prefs.getBoolean(GUI_START_MINIMIZED_KEY, false);
	}

	public void setStartMinimized(boolean startMinimized) {
		changeBoolean(GUI_START_MINIMIZED_KEY, startMinimized(), startMinimized);
	}

	public int getWindowWidth() {
		return prefs.getInt(GUI_WINDOW_WIDTH_KEY, Screen.getScreenWidth() > 1920 ? 1500 : 1000);
	}

	public void setWindowWidth(String width) {
		changeInt(GUI_WINDOW_WIDTH_KEY, getWindowWidth(), width);
	}

	public int getProportionalWidth(float proportionPercentage) {
		return Math.round(getWindowWidth() * proportionPercentage / 100);
	}

	public int getWindowHeight() {
		return prefs.getInt(GUI_WINDOW_HEIGHT_KEY, 500);
	}

	public void setWindowHeight(String height) {
		changeInt(GUI_WINDOW_HEIGHT_KEY, getWindowHeight(), height);
	}

	public int getProportionalHeight(float proportionPercentage) {
		return Math.round(getWindowHeight() * proportionPercentage / 100);
	}

	public int getFontSize() {
		return prefs.getInt(GUI_FONT_SIZE_KEY, 25);
	}

	public void setFontSize(String size) {
		changeInt(GUI_FONT_SIZE_KEY, getFontSize(), size);
	}

	public CustomFont getProportionalFont(float proportionPercentage) {
		return new CustomFont("Arial", Math.round(getFontSize() * proportionPercentage / 100));
	}

	public CustomFont getDefaultFont() {
		return new CustomFont("Arial", getFontSize());
	}

	public String getDateFormat() {
		return prefs.get(GUI_MESSAGE_DATE_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss");
	}

	public void setDateFormat(String dateFormat) {
		DateTimeFormatter.ofPattern(dateFormat); // Tests the format so an error is thrown when it's invalid.
		changeString(GUI_MESSAGE_DATE_FORMAT_KEY, getDateFormat(), dateFormat);
	}

	public int getMaxLength() {
		return prefs.getInt(GUI_MESSAGE_MAX_LENGTH_KEY, 50);
	}

	public void setMaxLength(String maxLength) {
		changeInt(GUI_MESSAGE_MAX_LENGTH_KEY, getMaxLength(), maxLength);
	}

	public boolean onReceiveNotify() {
		return prefs.getBoolean(GUI_ON_RECEIVE_NOTIFY, true);
	}

	public void setOnReceiveNotify(boolean notify) {
		changeBoolean(GUI_ON_RECEIVE_NOTIFY, onReceiveNotify(), notify);
	}

	public boolean onReceiveRestore() {
		return prefs.getBoolean(GUI_ON_RECEIVE_RESTORE, true);
	}

	public void setOnReceiveRestore(boolean restore) {
		changeBoolean(GUI_ON_RECEIVE_RESTORE, onReceiveRestore(), restore);
	}

	private boolean changeInt(String key, int oldValue, String newValueString) {
		int newValue = Integer.parseInt(newValueString);
		if (oldValue != newValue) {
			OLog.info("Changing '%s' from %d to %d", key, oldValue, newValue);
			prefs.putInt(key, newValue);
			propertyObservable.firePropertyChange(EVENT_CONFIG_CHANGED + key, oldValue, newValue);
			return true;
		}
		return false;
	}

	private boolean changeBoolean(String key, boolean oldValue, boolean newValue) {
		if (oldValue != newValue) {
			OLog.info("Changing '%s' from %b to %b", key, oldValue, newValue);
			prefs.putBoolean(key, newValue);
			propertyObservable.firePropertyChange(EVENT_CONFIG_CHANGED + key, oldValue, newValue);
			return true;
		}
		return false;
	}

	private boolean changeString(String key, String oldValue, String newValue) {
		if (oldValue == null)
			oldValue = "";
		if (newValue == null)
			newValue = "";
		if (!oldValue.trim().equals(newValue.trim())) {
			OLog.info("Changing '%s' from '%s' to '%s'", key, oldValue, newValue);
			prefs.put(key, newValue);
			propertyObservable.firePropertyChange(EVENT_CONFIG_CHANGED + key, oldValue, newValue);
			return true;
		}
		return false;
	}

}
