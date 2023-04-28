package lcm.lanpush;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

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
        var scrollPane = SwingComponents.createScrollPane(contentPane, scrollSize, scrollSize);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); // TODO: Incorporate that on SwingComponents
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        settingsFrame.setContentPane(scrollPane);
        contentPane.setLayout(new RelativeLayout(Axis.VERTICAL, 0, 0, false));

        contentPane.add(createOptionPanel("UDP port", udpPortOption, 5, "The UDP port to listen on"));
        contentPane.add(createOptionPanel("IP address", ipOption, 20, "The IP address to listen on"));
        var filePanel = new JPanel(new RelativeLayout(Axis.HORIZONTAL));
        filePanel.add(logPathOption, 9f);
        filePanel.add(logPathChooserButton, 1f);
        contentPane.add(createOptionPanel("Log file folder", filePanel, 30, "The folder to store log files"));
        contentPane.add(createOptionPanel("Log level", logLevelOption, 5, "The log level"));
        contentPane.add(createOptionPanel("Minimize to tray", minimizeToTrayOption, 0, "Minimize to tray"));
        contentPane.add(createOptionPanel("Window width", windowWidthOption, 4, "Window width"));
        contentPane.add(createOptionPanel("Window height", windowHeightOption, 4, "Window height"));
        contentPane.add(createOptionPanel("Font size", fontSizeOption, 2, "Font size"));
        contentPane.add(createOptionPanel("Message date format", messageDateFormatOption, 15, "Message date format"));
        contentPane.add(createOptionPanel("Message max length", messageMaxLengthOption, 2, "Message max length"));
        contentPane.add(createOptionPanel("Notify on message received", onReceiveNotifyOption, 0, "Notify on message received"));
        contentPane.add(createOptionPanel("Restore on message received", onReceiveRestoreOption, 0, "Restore on message received"));
        
        // Set the window size and make it visible
        settingsFrame.setSize(Config.getProportionalWidth(50), Config.getWindowHeight());
        Screen.centralizeWindow(settingsFrame);
        Config.getProportionalFont(70).apply(contentPane);
        settingsFrame.setVisible(true);
        SwingComponents.refresh(contentPane);
    }

    private JPanel createOptionPanel(String labelText, JComponent component, int expectedMaxLength, String hint) {
        // int gapSize = Config.getProportionalWidth(1.5f);
        JPanel panel = new JPanel(new RelativeLayout(Axis.HORIZONTAL, 0, 0, true));

        var label = new JLabel(labelText + "  ");
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        // TODO: encapsulate this logic in SwingComponents
        component.setToolTipText(hint);
        if (expectedMaxLength > 0) {
            Dimension dim = component.getPreferredSize();
            dim.width = expectedMaxLength * component.getFontMetrics(component.getFont()).charWidth('a');
            component.setPreferredSize(dim);
        }

        // TODO: encapsulate this logic in SwingComponents
        var questionLabel = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        questionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        questionLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(component, hint);
            }
        });

        var valuesPanel = new JPanel(new RelativeLayout(Axis.HORIZONTAL, 0, 0, true));
        valuesPanel.add(component, 9f);
        valuesPanel.add(questionLabel, 1f);

        panel.add(label, 1f);
        panel.add(valuesPanel, 1f);
        
        return panel;
    }

}
