package lcm.lanpush;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import lcm.java.swing.CustomFont;
import lcm.java.swing.Images;
import lcm.java.swing.Layouts;
import lcm.java.swing.RelativeLayout;
import lcm.java.swing.RelativeLayout.Axis;
import lcm.java.swing.Screen;
import lcm.java.swing.SwingComponents;
import lcm.java.swing.SystemTrayFrame;
import lcm.java.swing.Toast;
import lcm.java.system.Sys;
import lcm.java.system.TimeFormatter;
import lcm.java.system.logging.OLog;

public class MainWindow implements PropertyChangeListener {

	static final Config config = Config.getInstance();
	static final ReceiverHandler receiverHandler = ReceiverHandler.getInstance();

    public static final MainWindow INST = new MainWindow();

	final JFrame mainFrame;
    final JPanel mainPane;
    final JLabel statusLabel;
    final JPanel inputPane;
    final JTextField inputText;
    final JPanel messagePane;

    private MainWindow() {
        this.statusLabel = createStatusLabel();
        this.inputText = createInputText();
        this.inputPane = createInputPane(this.inputText);
        this.messagePane = new JPanel(new RelativeLayout(Axis.VERTICAL, 0, 0, true));
		this.mainPane = createMainPane(this.statusLabel, this.inputPane, this.messagePane);

		Image appIcon = Images.getImageFromResource("/lanpush.png");
		this.mainFrame = config.minimizeToTray() ? new SystemTrayFrame("LANPUSH", appIcon) : new JFrame("LANPUSH");
		mainFrame.setState(config.startMinimized() ? JFrame.ICONIFIED : JFrame.NORMAL);
		this.mainFrame.setIconImage(appIcon);
		setWindowSize();
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setJMenuBar(createMenuBar());
		mainFrame.add(mainPane);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Screen.centralizeWindow(mainFrame);

		setFonts();
		config.addPropertyChangeListener(this);
    }

	public void windowStart() {
		if (!config.startMinimized())
			restoreWindow();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Options");
		JMenuItem stopItem = new JMenuItem("Stop");
		JMenuItem reconnectItem = new JMenuItem("Reconnect");
		JMenuItem settingsItem = new JMenuItem("Settings");
		JMenuItem aboutItem = new JMenuItem("About");
		JMenuItem exitItem = new JMenuItem("Exit");

		stopItem.addActionListener(actionEvent ->  {
			OLog.info("Stopping...");
			receiverHandler.stopListening();
		});
		reconnectItem.addActionListener(actionEvent ->  {
			OLog.info("Reconnecting...");
			receiverHandler.reconnect();
		});
		settingsItem.addActionListener(actionEvent ->  {
			OLog.info("Opening settings...");
			new SettingsWindow();
		});
		aboutItem.addActionListener(actionEvent ->  {
			OLog.info("Opening about...");
			new AboutWindow();
		});
		exitItem.addActionListener(actionEvent ->  {
			OLog.info("Exiting...");
			System.exit(0);
		});

		menu.add(stopItem);
		menu.add(reconnectItem);
		menu.add(settingsItem);
		menu.add(aboutItem);
		menu.add(exitItem);
		menuBar.add(menu);

		return menuBar;
	}

	private JPanel createMainPane(JLabel statusLabel, JPanel inputPane, JPanel messagePane) {
		var scrollingMessagePane = SwingComponents.createScrollPane(messagePane, config.getProportionalWidth(1));
		return Layouts.fullVerticalPane(Arrays.asList(statusLabel, inputPane, scrollingMessagePane), 1, 1, 7);
	}

	private JLabel createStatusLabel() {
		var statusLabel = new JLabel("LANPUSH");
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setBackground(Color.YELLOW);
		statusLabel.setOpaque(true);
		return statusLabel;
	}

    private JTextField createInputText() {
        var inputText = new JTextField();
        inputText.setFocusTraversalKeysEnabled(false);
		SwingComponents.addEnterPressedListener(inputText, () -> sendMessage());
        return inputText;
    }

    private JPanel createInputPane(JTextField inputText) {
		JPanel inputPane = new JPanel(new RelativeLayout(Axis.HORIZONTAL, 0, 0, true));
		inputPane.add(inputText, 8.5f);
		
		var sendButton = new JButton("> SEND");
		sendButton.setToolTipText("Send typed message to the configured IPs.");
		sendButton.addActionListener(clickEvent -> {sendMessage();});

		inputPane.add(sendButton, 1.5f);
		return inputPane;
	}

    private void sendMessage() {
        Lanpush.sendMessage(inputText.getText());
        inputText.setText("");
		Toast.makeToast(mainFrame, "MESSAGE SENT!", 2);
    }

	public void createMessageEntry(String msg) {
    	JPanel newLine = new JPanel(new RelativeLayout(Axis.HORIZONTAL, 10, 0, true));
    	JButton copyBtn = new JButton("copy");
    	copyBtn.addActionListener(actionEvent -> {
			Sys.setClipboard(msg);
			Toast.makeToast((JFrame) messagePane.getParent(), "Copied!", 2);
		});
    	JButton browseBtn = new JButton("browse");
    	browseBtn.addActionListener(actionEvent -> {
			String urlBusca = msg.replaceAll(" ", "+");
			if (urlBusca.startsWith("www."))
				urlBusca = "https://" + urlBusca;
			else if (!urlBusca.startsWith("http"))
				urlBusca = "https://www.google.com/search?q=" + urlBusca;
			try {
				Desktop.getDesktop().browse(new URI(urlBusca)); // URL?
			}
			catch (UnsupportedOperationException e) {
				try {
					Sys.exec("xdg-open " + urlBusca);
				}
				catch (Throwable t) {
					String errorMsg = "It's not possible to open the browser on the current system.";
					JOptionPane.showMessageDialog(messagePane, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
					OLog.error(t, errorMsg);
				}
			}
			catch (Throwable t) {
				OLog.error(t, "Error while trying to open browser");
			}
		});
		newLine.add(copyBtn, 1f);
		newLine.add(browseBtn, 1f);
		String dateStr = new TimeFormatter(config.getDateFormat()).localToString(LocalDateTime.now());
		String truncatedMessage = msg.length() > config.getMaxLength() ? msg.substring(0, config.getMaxLength()) : msg;
		JLabel label = new JLabel(String.format("%s %s", dateStr, truncatedMessage));
		newLine.add(label, 8f);
		config.getDefaultFont().apply(label);
		config.getProportionalFont(70).apply(copyBtn, browseBtn);
		messagePane.add(newLine);

		if (config.onReceiveRestore())
			restoreWindow();
		if (mainFrame instanceof SystemTrayFrame && config.onReceiveNotify()) {
			SystemTrayFrame frame = (SystemTrayFrame) mainFrame;
			frame.addMessageListener(event -> {frame.restore();}); // Event for clicking the notification message.
			frame.displayMessage("LANPUSH", " >>> MESSAGE RECEIVED: " + msg);
    	}
    }

	public void restoreWindow() {
    	mainFrame.setState(Frame.NORMAL);
    	mainFrame.setVisible(true);
    	mainFrame.toFront();
    	SwingComponents.refresh(mainFrame);
    }

	public void setWindowSize() {
		mainFrame.setSize(config.getWindowWidth(), config.getWindowHeight());
	}

	public void setFonts() {
		config.getDefaultFont().apply(true, mainFrame);
		CustomFont buttonsFont = config.getProportionalFont(70);
		SwingComponents.filterChildren(mainFrame, component -> component instanceof JButton).stream().forEach(button -> buttonsFont.apply(button));
	}

	public void updateStatus(boolean listening, String text) {
		statusLabel.setBackground(listening ? Color.GREEN : Color.RED);
		statusLabel.setText(text);
	}

	@Override
    public void propertyChange(PropertyChangeEvent evt) {
		switch(evt.getPropertyName()) {
			case Config.EVENT_CONFIG_CHANGED + Config.GUI_FONT_SIZE_KEY -> setFonts();
			case Config.EVENT_CONFIG_CHANGED + Config.GUI_WINDOW_WIDTH_KEY -> setWindowSize();
			case Config.EVENT_CONFIG_CHANGED + Config.GUI_WINDOW_HEIGHT_KEY -> setWindowSize();
		}
    }
}
