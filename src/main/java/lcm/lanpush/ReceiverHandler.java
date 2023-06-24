package lcm.lanpush;

import java.io.IOException;

import lanpush.connectors.Receiver;
import lcm.java.system.logging.OLog;

class ReceiverHandler {

    static final Config config = Config.getInstance();

    public static final ReceiverHandler INST = new ReceiverHandler();
    private Receiver receiver = new Receiver();
    
    private ReceiverHandler() {}

    void startListening() {
        int port = config.getUdpPort();
        new Thread(() -> {
            try {
                MainWindow.INST.updateStatus(true, "Listening on port " + port);
                String receivedMessage;
                while ((receivedMessage = receiver.listen(port, null)) != null) // TODO: Remover o segundo parametro, e mover shutdown para Sys.
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
}
