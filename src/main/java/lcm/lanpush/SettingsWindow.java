package lcm.lanpush;

import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import lcm.java.swing.Images;
import lcm.java.swing.Layouts;
import lcm.java.swing.RelativeLayout;
import lcm.java.swing.RelativeLayout.Axis;
import lcm.java.swing.Screen;
import lcm.java.swing.SwingComponents;
import lcm.java.system.logging.LogLevel;
import lcm.java.system.logging.OLog;

public class SettingsWindow implements PropertyChangeListener {

    static final Config config = Config.getInstance();

    private final JFrame settingsFrame;

    private JPanel contentPane;
    private final JTextField udpPortOption = new JTextField();
    private final JTextField ipOption = new JTextField();
    private final JTextField logPathOption = new JTextField();
    private final JButton logPathChooserButton = new JButton("...");
    private final JFileChooser fileChooser = new JFileChooser();
    private final JComboBox<String> logLevelOption = new JComboBox<>(LOG_LEVEL_OPTIONS);
    private final JCheckBox minimizeToTrayOption = new JCheckBox();
    private final JCheckBox startMinimized = new JCheckBox();
    private final JTextField windowWidthOption = new JTextField();
    private final JTextField windowHeightOption = new JTextField();
    private final JTextField fontSizeOption = new JTextField();
    private final JTextField messageDateFormatOption = new JTextField();
    private final JTextField messageMaxLengthOption = new JTextField();
    private final JCheckBox onReceiveNotifyOption = new JCheckBox();
    private final JCheckBox onReceiveRestoreOption = new JCheckBox();

    private static final String[] LOG_LEVEL_OPTIONS = new String[] {"DEBUG", "INFO", "WARN", "ERROR"};

    private static final String HINT_UDP = "The UDP port used to receive and to send messages. It must be a number between 1 and 65535.";
    private static final String HINT_IP = "The IP address(es) used for sending messages to. Comma is used as separator when using multiple IPs.";
    private static final String HINT_LOG_PATH = "Path to the log file. If blank, the log will be printed to the console and not be persisted.";
    private static final String HINT_LOG_LEVEL = "The minimum level to be printed on log. DEBUG prints all the app info, and ERROR only prints error messages.";
    private static final String HINT_MINIMIZE_TO_TRAY = "Whether to add the app to the system's tray when it gets minimized.";
    private static final String HINT_START_MINIMIZED = "Whether the app should start minimized.";
    private static final String HINT_WINDOW_WIDTH = "The width of the main window (in pixels).";
    private static final String HINT_WINDOW_HEIGHT = "The height of the main window (in pixels).";
    private static final String HINT_FONT_SIZE = "The font size to be used in the app's texts.";
    private static final String HINT_MESSAGE_DATE_FORMAT = "The date format of the messages. It follows Java's DateTimeFormatter formats.";
    private static final String HINT_MESSAGE_MAX_LENGTH = "The max length of the messages to be displayed in the main window. They get truncated when exceeding this limit.";
    private static final String HINT_ON_RECEIVE_NOTIFY = "Whether to display a notification when a message is received.";
    private static final String HINT_ON_RECEIVE_RESTORE = "Whether to restore the main window when a message is received and the app is in background.";
    

    public SettingsWindow() {
        settingsFrame = new JFrame("Settings");
        setWindowSize();

        createContentPane();
        initializeValues();
        restrictInputs();
        setUpdateEvents();

        Screen.centralizeWindow(settingsFrame);
        settingsFrame.setVisible(true);
        SwingComponents.refresh(contentPane);

        config.addPropertyChangeListener(this);
    }

    private void setWindowSize() {
        settingsFrame.setSize(config.getProportionalWidth(60), config.getWindowHeight());
    }

    private void createContentPane() {
        var optionPanes = new ArrayList<JPanel>();
        optionPanes.add(createOptionPanel("UDP port", udpPortOption, 20, HINT_UDP));
        optionPanes.add(createOptionPanel("IP address", ipOption, 80, HINT_IP));
        optionPanes.add(createOptionPanel("log file folder", createLogFilePanel(), 90, HINT_LOG_PATH));
        optionPanes.add(createOptionPanel("log level", logLevelOption, 40, HINT_LOG_LEVEL));
        optionPanes.add(createOptionPanel("Minimize to tray", minimizeToTrayOption, 10, HINT_MINIMIZE_TO_TRAY));
        optionPanes.add(createOptionPanel("Start minimized", startMinimized, 10, HINT_START_MINIMIZED));
        optionPanes.add(createOptionPanel("Window width", windowWidthOption, 20, HINT_WINDOW_WIDTH));
        optionPanes.add(createOptionPanel("Window height", windowHeightOption, 20, HINT_WINDOW_HEIGHT));
        optionPanes.add(createOptionPanel("Font size", fontSizeOption, 15, HINT_FONT_SIZE));
        optionPanes.add(createOptionPanel("Message date format", messageDateFormatOption, 55, HINT_MESSAGE_DATE_FORMAT));
        optionPanes.add(createOptionPanel("Message max length", messageMaxLengthOption, 15, HINT_MESSAGE_MAX_LENGTH));
        optionPanes.add(createOptionPanel("Notify on message received", onReceiveNotifyOption, 10, HINT_ON_RECEIVE_NOTIFY));
        optionPanes.add(createOptionPanel("Restore on message received", onReceiveRestoreOption, 10, HINT_ON_RECEIVE_RESTORE));
        contentPane = Layouts.fullVerticalPane(optionPanes);
        config.getProportionalFont(60).apply(true, contentPane);
        int scrollSize = config.getProportionalHeight(5);
        var scrollPane = SwingComponents.createScrollPane(contentPane, scrollSize);
        settingsFrame.setContentPane(scrollPane);
    }

    private JPanel createLogFilePanel() {
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".log") || f.getName().toLowerCase().endsWith(".txt");
            }
            @Override
            public String getDescription() {
                return "Text/log files (*.log, *.txt)";
            }
        });
        logPathChooserButton.addActionListener(e -> {
            fileChooser.setSelectedFile(new File(logPathOption.getText().isBlank() ? "lanpush.log" : logPathOption.getText()));
            if (fileChooser.showOpenDialog(MainWindow.INST.mainFrame) == JFileChooser.APPROVE_OPTION) {
                var selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null)
                    logPathOption.setText(selectedFile.getAbsolutePath());
            }
            settingsFrame.toFront();
        });
        var filePanel = new JPanel(new RelativeLayout(Axis.HORIZONTAL));
        filePanel.add(logPathOption, 8.5f);
        filePanel.add(logPathChooserButton, 1.5f);
        return filePanel;
    }

    private boolean canWriteOnFile(File file) {
       if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
               return false;
            }
        }
        return file.canWrite();
    }

    private void logPathChanged() {
        if (logPathOption.getText().isBlank()) {
            config.setLogPath(null);
            return;
        }
        var selectedFile = new File(logPathOption.getText());
        if (this.canWriteOnFile(selectedFile)) {
            config.setLogPath(logPathOption.getText());
            OLog.info("Log path selected: %s", selectedFile.getAbsolutePath());
        }
        else
            JOptionPane.showMessageDialog(null, "LANPUSH cannot write to the selected path. Check its permissions.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel createOptionPanel(String labelText, JComponent component, int maxWidthPercentage, String hint) {
        var label = new JLabel(labelText + "  ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        component.setToolTipText(hint);
        var hintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var questionLabel = Images.createTooltipLabel(hint, config.getFontSize());

        hintPanel.add(questionLabel);
        questionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        float inputWidth = maxWidthPercentage/100f;
        return Layouts.horizontalPane(Arrays.asList(label, component, hintPanel), 1f, inputWidth, 1-inputWidth);
    }

    private void initializeValues() {
        udpPortOption.setText(String.valueOf(config.getUdpPort()));
        ipOption.setText(String.join(",", config.getIp()));
        logPathOption.setText(config.getLogPath());
        logLevelOption.setSelectedItem(config.getLogLevel().name());
        minimizeToTrayOption.setSelected(config.minimizeToTray());
        startMinimized.setSelected(config.startMinimized());
        windowWidthOption.setText(String.valueOf(config.getWindowWidth()));
        windowHeightOption.setText(String.valueOf(config.getWindowHeight()));
        fontSizeOption.setText(String.valueOf(config.getFontSize()));
        messageDateFormatOption.setText(config.getDateFormat());
        messageMaxLengthOption.setText(String.valueOf(config.getMaxLength()));
        onReceiveNotifyOption.setSelected(config.onReceiveNotify());
        onReceiveRestoreOption.setSelected(config.onReceiveRestore());
    }

    private void setUpdateEvents() {
        udpPortOption.addFocusListener(new ConfigChanged(() -> config.setUdpPort(udpPortOption.getText())));
        ipOption.addFocusListener(new ConfigChanged(() -> config.setIp(ipOption.getText())));
        logPathOption.addFocusListener(new ConfigChanged(() -> {logPathChanged();}));
        logPathChooserButton.addFocusListener(new ConfigChanged(() -> {logPathChanged();}));
        logLevelOption.addFocusListener(new ConfigChanged(() -> config.setLogLevel(LogLevel.valueOf(logLevelOption.getSelectedItem().toString()))));
        minimizeToTrayOption.addFocusListener(new ConfigChanged(() -> config.setMinimizeToTray(minimizeToTrayOption.isSelected())));
        startMinimized.addFocusListener(new ConfigChanged(() -> config.setStartMinimized(startMinimized.isSelected())));
        windowWidthOption.addFocusListener(new ConfigChanged(() -> config.setWindowWidth(windowWidthOption.getText())));
        windowHeightOption.addFocusListener(new ConfigChanged(() -> config.setWindowHeight(windowHeightOption.getText())));
        fontSizeOption.addFocusListener(new ConfigChanged(() -> config.setFontSize(fontSizeOption.getText())));
        messageDateFormatOption.addFocusListener(new ConfigChanged(() -> config.setDateFormat(messageDateFormatOption.getText())));
        messageMaxLengthOption.addFocusListener(new ConfigChanged(() -> config.setMaxLength(messageMaxLengthOption.getText())));
        onReceiveNotifyOption.addFocusListener(new ConfigChanged(() -> config.setOnReceiveNotify(onReceiveNotifyOption.isSelected())));
        onReceiveRestoreOption.addFocusListener(new ConfigChanged(() -> config.setOnReceiveRestore(onReceiveRestoreOption.isSelected())));
    }

    private class ConfigChanged implements FocusListener {
        Runnable updateAction;
        ConfigChanged(Runnable updateAction) {
            this.updateAction = updateAction;
        }
        @Override
        public void focusLost(FocusEvent arg0) {
            updateAction.run();
        }
        @Override
        public void focusGained(FocusEvent arg0) {}
    }

    private void restrictInputs() {
        final String onlyNumbersRegex = "^[0-9]+$";
        final String onlyNumbersDotsAndCommasRegex = "^[0-9,.]+$";
        SwingComponents.restrictInput(fontSizeOption, onlyNumbersRegex);
        SwingComponents.restrictInput(messageMaxLengthOption, onlyNumbersRegex);
        SwingComponents.restrictInput(windowWidthOption, onlyNumbersRegex);
        SwingComponents.restrictInput(windowHeightOption, onlyNumbersRegex);
        SwingComponents.restrictInput(ipOption, onlyNumbersDotsAndCommasRegex);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case Config.EVENT_CONFIG_CHANGED + Config.GUI_FONT_SIZE_KEY -> {createContentPane(); SwingComponents.refresh(settingsFrame);}
            case Config.EVENT_CONFIG_CHANGED + Config.GUI_WINDOW_WIDTH_KEY -> setWindowSize();
            case Config.EVENT_CONFIG_CHANGED + Config.GUI_WINDOW_HEIGHT_KEY -> setWindowSize();
        }
    }

}
