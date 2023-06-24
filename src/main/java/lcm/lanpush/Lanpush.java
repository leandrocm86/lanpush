package lcm.lanpush;

import java.io.IOException;

import javax.swing.JOptionPane;

import lanpush.connectors.Sender;
import lcm.java.system.logging.OLog;

public class Lanpush {

	static final Config config = Config.getInstance();

	public static void main(String[] args) {
		try {
			OLog.info("Starting LANPUSH");
			ReceiverHandler.INST.startListening();
			MainWindow.INST.windowStart();
		}
		catch (Throwable t) {
			OLog.error(t, "Error starting LANPUSH");
			alertError();
		}
	}
	
	public static void sendMessage(String msg) {
		try {
			OLog.info("Sending message to '%s' on port %d: '%s'", String.join(", ", config.getIp()), config.getUdpPort(), msg);
			Sender.send(config.getIp(), config.getUdpPort(), msg);
		} catch (IOException e) {
			OLog.error(e, "Error sending message!");
			alertError();
		}
	}

	private static void alertError() {
		var errorMessage = config.getLogPath().isBlank() ? "Error! Read the output for details." : "Error! Check the log file for details.";
		JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public static void showWarning(String warningMessage) {
		JOptionPane.showMessageDialog(null, warningMessage, "Warning", JOptionPane.WARNING_MESSAGE);
	}
}
