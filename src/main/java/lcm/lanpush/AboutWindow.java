package lcm.lanpush;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;

import lcm.java.swing.Images;
import lcm.java.swing.Layouts;
import lcm.java.swing.Screen;
import lcm.java.swing.SwingComponents;
import lcm.java.system.logging.OLog;

public class AboutWindow {

    private static final String DESCRIPTION =
        """
            <html>
                LANPUSH aims to facilitate exchanging text content between devices in the same local network.
                There's an <a href='https://play.google.com/store/apps/details?id=lcm.lanpush'>Android app</a> on the play store. You can check its <a href='https://github.com/leandrocm86/lanpush-android'>github repository</a> for more details.
                There's also a simple CLI version (for terminal usage) available as snap, flatpak and JAR. <a href='https://github.com/leandrocm86/lanpush-cli'>Check its repository</a> for details.
                Finally, this GUI (desktop) version is available for Windows and Linux (snap and flatpak). <a href='https://github.com/leandrocm86/lanpush'>Its repository is also on github</a>.
                All projects are free, open sourced, have no ads, and are open to suggestions.
            </html>
        """;
    
    final Config config = Config.getInstance();

    final JFrame aboutFrame;
    final JPanel aboutPane;
    final JLabel appIcon;
    final JEditorPane descriptionText;
    final JButton closeButton;

    public AboutWindow() {
        aboutFrame = new JFrame("About LANPUSH");
        appIcon = new JLabel();
        var image = Images.getImageFromResource("/lanpush.png");
        image = Images.resizeByHeight(image, config.getProportionalHeight(15));
        appIcon.setIcon(new ImageIcon(image));
        appIcon.setHorizontalAlignment(SwingConstants.CENTER);
        var versionLabel = new JLabel("version: 2.0.1");
        var topPane = Layouts.verticalPane(Arrays.asList(appIcon, versionLabel), 7, 3);

        descriptionText = new JEditorPane();
        descriptionText.setContentType("text/html");
        descriptionText.setEditable(false);
        descriptionText.setOpaque(false);
        descriptionText.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true); // Enable changing font size.
        descriptionText.setPreferredSize(new Dimension(config.getWindowWidth(), config.getProportionalHeight(50)));
        descriptionText.setText(DESCRIPTION.replace("\n", "<br>"));
        descriptionText.addHyperlinkListener(event -> {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                try {
                    Desktop.getDesktop().browse(event.getURL().toURI());
                } catch (IOException | URISyntaxException e) {
                    OLog.error(e, "Could not open link on browser: '%s'", event.getURL());
                }
        });
        var scrollPane = SwingComponents.createScrollPane(descriptionText, config.getProportionalWidth(2));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> aboutFrame.dispose());
        aboutPane = Layouts.fullVerticalPane(Arrays.asList(topPane, scrollPane, closeButton), 25, 65, 10);
        aboutFrame.setContentPane(aboutPane);

        config.getDefaultFont().apply(aboutFrame);
        aboutFrame.setSize(config.getProportionalWidth(50), config.getWindowHeight());
        Screen.centralizeWindow(aboutFrame);
        aboutFrame.setVisible(true);
        aboutFrame.toFront();
        
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

}
