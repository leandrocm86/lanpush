package lcm.lanpush;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import lcm.java.swing.RelativeLayout;
import lcm.java.swing.RelativeLayout.Axis;
import lcm.java.swing.Screen;
import lcm.java.swing.SwingComponents;

public class SettingsWindow {
    private final JFrame settingsFrame;

    private final JTextField udpPortOption = new JTextField();
    private final JTextField ipOption = new JTextField();
    private final JTextField logPathOption = new JTextField();
    private final JButton logPathChooserButton = new JButton("...");
    // private final JFileChooser logPathChooser = new JFileChooser();
    private final JComboBox<String> logLevelOption = new JComboBox<>(LOG_LEVEL_OPTIONS);
    private final JCheckBox minimizeToTrayOption = new JCheckBox();
    private final JTextField windowWidthOption = new JTextField();
    private final JTextField windowHeightOption = new JTextField();
    private final JTextField fontSizeOption = new JTextField();
    private final JTextField messageDateFormatOption = new JTextField();
    private final JTextField messageMaxLengthOption = new JTextField();
    private final JCheckBox onReceiveNotifyOption = new JCheckBox();
    private final JCheckBox onReceiveRestoreOption = new JCheckBox();

    private static final String[] LOG_LEVEL_OPTIONS = new String[] {"DEBUG", "INFO", "WARN", "ERROR"};

    public SettingsWindow() {
        settingsFrame = new JFrame("Settings");
        JPanel contentPane = new JPanel();
        int scrollSize = Config.getProportionalHeight(5);
        var scrollPane = SwingComponents.createScrollPane(contentPane, scrollSize);
        // contentPane.setLayout(new RelativeLayout(Axis.VERTICAL, 0, 0, true));

        var optionPanes = new ArrayList<JPanel>();
        optionPanes.add(createOptionPanel("UDP port", udpPortOption, 20, "The UDP port to listen on"));
        optionPanes.add(createOptionPanel("IP address", ipOption, 80, "The IP address to listen on"));
        var filePanel = new JPanel(new RelativeLayout(Axis.HORIZONTAL, true));
        filePanel.add(logPathOption, 8.5f);
        filePanel.add(logPathChooserButton, 1.5f);
        optionPanes.add(createOptionPanel("log file folder", filePanel, 90, "the folder to store log files"));
        optionPanes.add(createOptionPanel("log level", logLevelOption, 40, "the log level"));
        optionPanes.add(createOptionPanel("Minimize to tray", minimizeToTrayOption, 10, "Minimize to tray"));
        optionPanes.add(createOptionPanel("Window width", windowWidthOption, 20, "Window width"));
        optionPanes.add(createOptionPanel("Window height", windowHeightOption, 20, "Window height"));
        optionPanes.add(createOptionPanel("Font size", fontSizeOption, 15, "Font size"));
        optionPanes.add(createOptionPanel("Message date format", messageDateFormatOption, 55, "Message date format"));
        optionPanes.add(createOptionPanel("Message max length", messageMaxLengthOption, 15, "Message max length"));
        optionPanes.add(createOptionPanel("Notify on message received", onReceiveNotifyOption, 10, "Notify on message received"));
        optionPanes.add(createOptionPanel("Restore on message received", onReceiveRestoreOption, 10, "Restore on message received"));

        var layout = new RelativeLayout(Axis.VERTICAL, true);
        contentPane.setLayout(layout);
        optionPanes.forEach(pane -> contentPane.add(pane));

        // Set the window size and make it visible
        settingsFrame.setSize(Config.getProportionalWidth(60), Config.getWindowHeight());
        Screen.centralizeWindow(settingsFrame);
        Config.getProportionalFont(60).apply(contentPane);
        settingsFrame.add(scrollPane, BorderLayout.CENTER);
        // settingsFrame.pack();
        settingsFrame.setVisible(true);
        SwingComponents.refresh(contentPane);
    }

    private JPanel createOptionPanel(String labelText, JComponent component, int maxWidthPercentage, String hint) {
        var label = new JLabel(labelText + "  ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        component.setToolTipText(hint);
        var questionLabel = SwingComponents.createTooltipLabel(hint);
        questionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        float inputWidth = maxWidthPercentage/100f;
        return RelativeLayout.fullHorizontalPane(Arrays.asList(label, component, questionLabel), 1f, inputWidth, 1-inputWidth);
    }

}
