package lcm.lanpush;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;

import javax.imageio.ImageIO;
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

public class MainWindow {

    public static final MainWindow INST = new MainWindow();

	private final JFrame mainFrame;
    private final JPanel mainPane;
    private final JLabel statusLabel;
    private final JPanel inputPane;
    private final JTextField inputText;
    private final JPanel messagePane;

    private MainWindow() {
        this.statusLabel = createStatusLabel();
        this.inputText = createInputText();
        this.inputPane = createInputPane(this.inputText);
        this.messagePane = new JPanel(new RelativeLayout(Axis.VERTICAL, 0, 0, true));
		this.mainPane = createMainPane(this.statusLabel, this.inputPane, this.messagePane);
		this.mainFrame = createMainFrame(this.mainPane);
    }

	private JFrame createMainFrame(JPanel mainPane) {
		JFrame mainFrame = null;
		// var root = Lanpush.class.getResource("/");
		// String path = root != null ? root.getPath() : "?";
		// OLog.info("Path /: %s", path);
		// OLog.info(Sys.read("ls -l " + path));

		// root = Lanpush.class.getResource(".");
		// String path2 = root != null ? root.getPath() : "?";
		// OLog.info("Path .: %s", path2);
		// OLog.info(Sys.read("ls -l " + path2));

		// TODO: Aceitar Image no construtor e unificar setTrayImage com updateTrayImage
		var imageStream = Lanpush.class.getResourceAsStream("/lanpush.png");
		BufferedImage icon = null;
		try {
			icon = ImageIO.read(imageStream);
		} catch (IOException e) {
			OLog.error("Could not read lanpush.png");
			e.printStackTrace();
		}
		if (Config.minimizeToTray()) {
			mainFrame = new SystemTrayFrame("LANPUSH");
			if (icon != null)
				((SystemTrayFrame) mainFrame).setTrayImage(icon, "LANPUSH");
		}
		else {
			mainFrame = new JFrame("LANPUSH");
		}
		mainFrame.setIconImage(icon);
		mainFrame.setSize(Config.getWindowWidth(), Config.getWindowHeight());
		mainFrame.setLayout(new BorderLayout());
		
		mainFrame.setJMenuBar(createMenuBar());
		mainFrame.add(mainPane);
		
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Screen.centralizeWindow(mainFrame);
		Config.getDefaultFont().apply(mainPane);
		mainFrame.setState(Config.startMinimized() ? JFrame.ICONIFIED : JFrame.NORMAL);

		return mainFrame;
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
			ReceiverHandler.INST.stopListening();
		});
		reconnectItem.addActionListener(actionEvent ->  {
			OLog.info("Reconnecting...");
			ReceiverHandler.INST.reconnect();
		});
		settingsItem.addActionListener(actionEvent ->  {
			OLog.info("Opening settings...");
			new SettingsWindow();
		});
		aboutItem.addActionListener(actionEvent ->  {
			OLog.info("Opening about...");
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

		Config.getDefaultFont().apply(menu, stopItem, reconnectItem, settingsItem, aboutItem, exitItem);

		return menuBar;
	}

	private JPanel createMainPane(JLabel statusLabel, JPanel inputPane, JPanel messagePane) {
		var scrollingMessagePane = SwingComponents.createScrollPane(messagePane, Config.getProportionalWidth(1));
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
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(msg), null); // TODO: Colocar Clipboard no Sys.
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
		String dateStr = new TimeFormatter(Config.getDateFormat()).localToString(LocalDateTime.now());
		String truncatedMessage = msg.length() > Config.getMaxLength() ? msg.substring(0, Config.getMaxLength()) : msg;
		JLabel label = new JLabel(String.format("%s %s", dateStr, truncatedMessage));
		newLine.add(label, 8f);
		Config.getDefaultFont().apply(label);
		Config.getProportionalFont(70).apply(copyBtn, browseBtn);
		messagePane.add(newLine);

		if (Config.onReceiveRestore())
			restoreWindow();
		if (mainFrame instanceof SystemTrayFrame && Config.onReceiveNotify()) {
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

	public void updateSize() {
		mainFrame.setSize(Config.getWindowWidth(), Config.getWindowHeight());
		// SwingComponents.refresh(mainFrame);
	}

	public void updateFont() {
		// TODO: METODO EM SwingComponents QUE RETORNA TODOS OS FILHOS QUE ATENDAM UMA CONDICAO
		// DAÍ, RECUPERAR OS BOTOES E REAPLICAR A FONTE COM 70%
		Config.getDefaultFont().apply(true, mainFrame);
		// SwingComponents.refresh(mainFrame);
	}

	public void updateStatus(boolean listening, String text) {
		statusLabel.setBackground(listening ? Color.GREEN : Color.RED);
		statusLabel.setText(text);
	}
}
