package lanpush;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import io.Log;
import swing.Fonte;
import swing.RelativeLayout;
import swing.SwingUtils;
import swing.Toast;
import system.Sistema;
import system.SystemTrayFrame;
import utils.CDI;
import utils.Data;

public class Receiver {
	
	private static final int PORT = Config.getInt("connection.port");

    private static DatagramSocket udpSocket;
    private static final int MAX_LENGTH = Config.getInt("gui.max_message_length_display");
    private static final boolean AUTO_MSG = Config.getBoolean("gui.auto_message");
    private static final int FONT_SIZE = Config.getInt("gui.font.size");
    private static final boolean EXIT_ON_RECEIVE = Config.getBoolean("connection.exit_on_receive");
    private static final boolean ON_RECEIVE_RESTORE = Config.getBoolean("gui.onreceive.restore");
    private static final boolean ON_RECEIVE_NOTIFICATION = Config.getBoolean("gui.onreceive.notification");
    private int erros = 0;
    private boolean finishing = false;
    
    public Receiver() {
		Thread fechamento = new Thread(new Runnable() { // Trigger for program closing.
	        public void run() {
	        	terminar();
	        }
	    }, "Shutdown-thread");
		Runtime.getRuntime().addShutdownHook(fechamento);
    }

    public void run() {
        while (erros < 3 && !finishing) {
            try {
                Log.i("Starting connection with " + erros + " erros.");
                DatagramPacket packet = reconectar();
                Log.i("About to listen on UDP port " + PORT);
                udpSocket.receive(packet);
                if (!AUTO_MSG && System.currentTimeMillis() - Sender.getLastSent() < 1000) {
                	Log.i("Receiver ignoring message the app itself just sent.");
                	continue;
                }
                String text = new String(packet.getData(), 0, packet.getLength()).trim();
                Log.i("Received: " + text);
                showMessage(text);
                if (EXIT_ON_RECEIVE)
                	return;
            } catch (Throwable t) {
            	if (!finishing) {
	                erros++;
	                Log.e(t, "Error while trying to listen UDP port");
            	}
            } finally {
                fecharConexao();
            }
        }
        Lanpush.alert("Since there were 3 failures, the client will no longer try to connect.");
    }

    private void showMessage(String msg) {
    	if (Lanpush.isGUI()) {
    		JPanel pane = CDI.get(JPanel.class);
    		pane.add(criarNovaLinha(msg));
    		SystemTrayFrame frame = CDI.get(SystemTrayFrame.class);
    		if (ON_RECEIVE_RESTORE)
    			frame.restore();
    		if (ON_RECEIVE_NOTIFICATION) {
    			frame.addMessageListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						frame.restore();
					}
				});
    			frame.displayMessage("LANPUSH", " >>> MESSAGE RECEIVED: " + msg);
    		}
    	}
    	else {
    		System.out.println(getHora() + msg);
    	}
	}
    
    private JPanel criarNovaLinha(String msg) {
    	JPanel novaLinha = new JPanel(SwingUtils.createLayout(RelativeLayout.X_AXIS, 10, 0, true));
    	JButton copyBtn = new JButton("copy");
    	copyBtn.addActionListener(new ActionListener() {
    		@Override
			public void actionPerformed(ActionEvent arg0) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(msg), null);
				Toast.makeToast(CDI.get(SystemTrayFrame.class), "Copied!", 2);
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
					Desktop.getDesktop().browse(new URL(urlBusca).toURI());
				}
				catch (UnsupportedOperationException e) {
					try {
						Sistema.executar("xdg-open " + urlBusca);
					}
					catch (Throwable t) {
						SwingUtils.showMessage("It's not possible to open the browser on the current system.");
						Log.logaErro(t);
					}
				}
				catch (Throwable t) {
					Log.e(t, "Error while trying to open browser");
				}
			}
		});
		novaLinha.add(copyBtn, 1f);
		novaLinha.add(browseBtn, 1f);
		
		JLabel label = new JLabel(getHora() + (msg.length() > MAX_LENGTH ? msg.substring(0, MAX_LENGTH) + "(...)" : msg));
		novaLinha.add(label, 8f);
		new Fonte("Arial", FONT_SIZE).set(label);
		new Fonte("Arial", (int) Math.round(FONT_SIZE * 0.7)).set(copyBtn, browseBtn);
		return novaLinha;
    }
    
    private String getHora() {
    	return new Data().toStr(Data.DATA_dd_MM_HH_mm_ss) + ": ";
    }

	private DatagramPacket reconectar() throws SocketException {
        if (udpSocket != null) {
            Log.i("Socket was already created when trying new connection. It will be closed...");
            fecharConexao();
        }
        udpSocket = new DatagramSocket(PORT);
        byte[] message = new byte[8000];
        return new DatagramPacket(message, message.length);
    }

    private void fecharConexao() {
        if (udpSocket != null) {
            try {
                if (udpSocket.isClosed())
                    Log.i("Connection already closed.");
                else {
                    Log.i("Closing connection...");
//                    udpSocket.disconnect(); -> Disconnect estava travando o fechamento da aplicacao.
                    udpSocket.close();
                }
                udpSocket = null;
            } catch (Throwable t) {
                erros++;
                Log.e(t, "Error while trying to close connection.");
            }
        } else {
            Log.i("Null connection doesn't need to be closed.");
        }
    }
    
    public void terminar() {
    	this.finishing = true;
    	fecharConexao();
    }
}