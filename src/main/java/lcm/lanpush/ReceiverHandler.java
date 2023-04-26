package lcm.lanpush;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import lanpush.connectors.Receiver;
import lcm.java.swing.CustomFont;
import lcm.java.swing.RelativeLayout;
import lcm.java.swing.RelativeLayout.Axis;
import lcm.java.swing.SwingComponents;
import lcm.java.swing.SystemTrayFrame;
import lcm.java.swing.Toast;
import lcm.java.system.Sys;
import lcm.java.system.TimeFormatter;
import lcm.java.system.logging.OLog;

class ReceiverHandler {

    private JPanel messagesPanel;
	private JFrame mainFrame;
    private Receiver receiver;

    ReceiverHandler(JPanel messagePanel) {
        this.messagesPanel = messagePanel;
		this.mainFrame = (JFrame) messagePanel.getParent();
        this.receiver = new Receiver();
    }

    void keepListening() {
        int port = Config.getUdpPort();
        new Thread(() -> {
            try {
                String receivedMessage;
                while ((receivedMessage = receiver.listen(port, null)) != null) // TODO: Remover o segundo parametro, e mover shutdown para Sys.
                    displayMessage(receivedMessage);
            } catch (IOException e) {
                OLog.error(e, "Error while listening on port " + port);
            }
        }).start();
    }

	void stopListening() {
		receiver.stop();
	}

    private void displayMessage(String receivedMessage) {
        OLog.info("Received message: " + receivedMessage);
        messagesPanel.add(createMessageEntry(receivedMessage));
		if (Config.onReceiveRestore())
			restoreWindow();
		if (mainFrame instanceof SystemTrayFrame && Config.onReceiveNotify()) {
			SystemTrayFrame frame = (SystemTrayFrame) mainFrame;
			frame.addMessageListener(event -> {frame.restore();}); // Event for clicking the notification message.
			frame.displayMessage("LANPUSH", " >>> MESSAGE RECEIVED: " + receivedMessage);
    	}
    }

    private JPanel createMessageEntry(String msg) {
    	JPanel newLine = new JPanel(new RelativeLayout(Axis.HORIZONTAL, 10, 0, true));
    	JButton copyBtn = new JButton("copy");
    	copyBtn.addActionListener(new ActionListener() {
    		@Override
			public void actionPerformed(ActionEvent arg0) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(msg), null); // TODO: Colocar Clipboard no Sys.
				Toast.makeToast((JFrame) messagesPanel.getParent(), "Copied!", 2);
			}
		});
    	JButton browseBtn = new JButton("browse");
    	browseBtn.addActionListener(new ActionListener() {
    		@Override
			public void actionPerformed(ActionEvent arg0) {
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
						JOptionPane.showMessageDialog(messagesPanel, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
						OLog.error(t, errorMsg);
					}
				}
				catch (Throwable t) {
					OLog.error(t, "Error while trying to open browser");
				}
			}
		});
		newLine.add(copyBtn, 1f);
		newLine.add(browseBtn, 1f);

		String dateStr = new TimeFormatter(Config.getDateFormat()).localToString(LocalDateTime.now());
		JLabel label = new JLabel(dateStr + (msg.length() > Config.getMaxLength() ? msg.substring(0, Config.getMaxLength()) + "(...)" : msg));
		newLine.add(label, 8f);
		new CustomFont("Arial", Config.getFontSize()).apply(label);
		new CustomFont("Arial", (int) Math.round(Config.getFontSize() * 0.7)).apply(copyBtn, browseBtn);
		return newLine;
    }

	public void restoreWindow() {
    	mainFrame.setState(Frame.NORMAL);
    	mainFrame.setVisible(true);
    	mainFrame.toFront();
    	SwingComponents.refresh(mainFrame);
    }
    
}
