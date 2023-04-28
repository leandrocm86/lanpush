package lcm.lanpush;

import java.io.IOException;

import javax.swing.JOptionPane;

import lanpush.connectors.Sender;
import lcm.java.system.logging.OLog;

public class Lanpush {
	

	public static void main(String[] args) {
		if (Config.getLogPath() == null)
			OLog.setPrintStream(System.out);

		try {
			OLog.setFilePath(Config.getLogPath());
			OLog.setMinimumLevel(Config.getLogLevel());
			OLog.info("Starting LANPUSH");
			ReceiverHandler.INST.startListening();
			MainWindow.INST.restoreWindow();
		}
		catch (Throwable t) {
			OLog.error(t, "Error starting LANPUSH");
			alertError();
		}
	}
	
	public static void sendMessage(String msg) {
		try {
			OLog.info("Sending message to '%s' on port %d: '%s'", String.join(", ", Config.getIp()), Config.getUdpPort(), msg);
			Sender.send(Config.getIp(), Config.getUdpPort(), msg);
		} catch (IOException e) {
			OLog.error(e, "Error sending message!");
			alertError();
		}
	}

	private static void alertError() {
		var errorMessage = Config.getLogPath() != null ? "Error! Check the log file for details." : "Error! Read the output for details.";
		JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
