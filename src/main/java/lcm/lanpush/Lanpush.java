package lcm.lanpush;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import lanpush.connectors.Sender;
import lcm.java.swing.CustomFont;
import lcm.java.swing.RelativeLayout;
import lcm.java.swing.RelativeLayout.Axis;
import lcm.java.swing.Screen;
import lcm.java.swing.SwingComponents;
import lcm.java.swing.SystemTrayFrame;
import lcm.java.swing.Toast;
import lcm.java.system.Sys;
import lcm.java.system.logging.OLog;

public class Lanpush {
	
	private static JFrame mainFrame;
	private static JPanel mainPane;
	private static JTextField input;

	private static String rootPath = Sys.getSystemPath();
	
	public static void main(String[] args) {
		if (Config.getLogPath() == null)
			OLog.setPrintStream(System.out);

		try {
			OLog.setFilePath(Config.getLogPath());
			OLog.setMinimumLevel(Config.getLogLevel());
			OLog.info("Starting LANPUSH");
			final String iconPath = rootPath + "lanpush.png";
			if (Config.minimizeToTray())
				mainFrame = new SystemTrayFrame("LANPUSH", iconPath, true);
			else {
				mainFrame = new JFrame("LANPUSH");
				mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(iconPath));
			}
			mainFrame.setVisible(true);
			mainFrame.setSize(Config.getWindowWidth(), Config.getWindowHeight());
			mainFrame.setLayout(new BorderLayout());
			mainPane = new JPanel(new RelativeLayout(Axis.VERTICAL));
			mainFrame.add(mainPane);
			
			createInputPane();
			createMessagePane();
				
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Screen.centralizeWindow(mainFrame);
			new CustomFont("Arial", Config.getFontSize()).apply(mainPane);
			
			if (Config.minimizeToTray()) {
				mainFrame.setState(JFrame.ICONIFIED);
			}
			else {
				mainFrame.setState(JFrame.NORMAL);
			}

			new ReceiverHandler(mainPane).keepListening();
		}
		catch (Throwable t) {
			OLog.error(t, "Error starting LANPUSH");
			alertError();
		}
	}
	
	private static void createInputPane() {
		JPanel inputPane = new JPanel(new RelativeLayout(Axis.HORIZONTAL));
		mainPane.add(inputPane, 1f);
		
		input = new JTextField();
		inputPane.add(input, 9f);
		
		input.setFocusTraversalKeysEnabled(false);
		input.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10)
					sendMessage(input.getText());
			}
		});
		
		JButton okButton = new JButton("Send");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessage(input.getText());
				input.setText("");
			}
		});
		inputPane.add(okButton, 2f);
	}
	
	private static void createMessagePane() {
		JPanel msgPane = new JPanel(new RelativeLayout(Axis.VERTICAL, 0, 0, true));
		JScrollPane scrollPane = SwingComponents.createScrollPane(msgPane, 20, 20);
		mainPane.add(scrollPane, 7f);
		new ReceiverHandler(msgPane);
	}
	
	private static void sendMessage(String msg) {
		try {
			Sender.send(Config.getIp(), Config.getUdpPort(), msg);
		} catch (IOException e) {
			OLog.error(e, "Error sending message!");
			alertError();
		}
		if (input != null)
			input.setText("");
		Toast.makeToast(mainFrame, "MESSAGE SENT!", 2);
	}

	private static void alertError() {
		var errorMessage = Config.getLogPath() != null ? "Error! Check the log file for details." : "Error! Read the output for details.";
		JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
