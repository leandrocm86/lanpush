package lcm.lanpush;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SettingsFrame extends JFrame {

    public SettingsFrame() {
        super();
    }

    private void buildPane() {
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        this.add(pane);

        JLabel label = new JLabel("Hello WOrld!");
        pane.add(label);
    }
    
}
