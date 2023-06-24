package lcm.lanpush;

import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import lcm.java.swing.Layouts;
import lcm.java.swing.SwingComponents;

public class AboutWindow {

    private static final String DESCRIPTION =
        """
            <html>
                THIS IS STILL A WORK IN PROGRESS THIS IS STILL A WORK IN PROGRESS THIS IS STILL A WORK IN PROGRESS.
                <a href='https://www.google.com'>THIS IS STILL A WORK IN PROGRESS</a> THIS IS STILL A WORK IN PROGRESSTHIS IS STILL A WORK IN PROGRESS.
                THIS IS STILL A WORK IN PROGRESSTHIS IS STILL A WORK IN PROGRESSTHIS IS STILL A WORK IN PROGRESS.
            </html>
        """;
    
    final Config config = Config.getInstance();

    final JFrame aboutFrame;
    final JPanel aboutPane;
    final JLabel appIcon;
    final JLabel descriptionText;
    final JButton closeButton;

    public AboutWindow() {
        aboutFrame = new JFrame("About LANPUSH");
        appIcon = new JLabel();
        appIcon.setIcon(new ImageIcon(SwingComponents.getImageFromResource("/lanpush.png")));
        descriptionText = new JLabel(DESCRIPTION);
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> aboutFrame.dispose());
        aboutPane = Layouts.verticalPane(Arrays.asList(appIcon, descriptionText, closeButton), 30, 50, 20);
        aboutFrame.setContentPane(aboutPane);

        config.getDefaultFont().apply(aboutFrame);
        aboutFrame.setSize(config.getProportionalWidth(50), config.getProportionalHeight(50));
        aboutFrame.setVisible(true);
        aboutFrame.toFront();
    }

}
