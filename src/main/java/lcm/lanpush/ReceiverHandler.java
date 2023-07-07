package lcm.lanpush;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import lanpush.connectors.Receiver;
import lcm.java.system.Sys;
import lcm.java.system.logging.OLog;

class ReceiverHandler implements PropertyChangeListener {

    static final Config config = Config.getInstance();

    private static final ReceiverHandler INST = new ReceiverHandler();
    private Receiver receiver = new Receiver();
    
    private ReceiverHandler() {
        Sys.addShutdownHook(() -> {
            OLog.info("Shutting down and disconnecting...");
            receiver.stop();
        });
        config.addPropertyChangeListener(this);
    }

    public static ReceiverHandler getInstance() {
        return INST;
    }

    void startListening() {
        int port = config.getUdpPort();
        new Thread(() -> {
            try {
                MainWindow.INST.updateStatus(true, "Listening on port " + port);
                String receivedMessage;
                while ((receivedMessage = receiver.listen(port)) != null)
                    displayMessage(receivedMessage);
            } catch (IOException e) {
                MainWindow.INST.updateStatus(false, "Error while listening on port " + port + ". Read the logs for details.");
                OLog.error(e, "Error while listening on port " + port);
            }
        }).start();
    }

	void stopListening() {
		receiver.stop();
        MainWindow.INST.updateStatus(false, "Stopped listening.");
	}

    void reconnect() {
        stopListening();
        startListening();
    }

    private void displayMessage(String receivedMessage) {
        OLog.info("Received message: " + receivedMessage);
        MainWindow.INST.createMessageEntry(receivedMessage);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Config.EVENT_CONFIG_CHANGED + Config.CONNECTION_UDP_PORT_KEY)) {
            OLog.info("Port number changed. Receiver will reconnect...");
			reconnect();
        }
    }
}
