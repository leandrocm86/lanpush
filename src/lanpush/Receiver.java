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
    private int erros = 0;
    private boolean terminando = false;
    
    public Receiver() {
    	CDI.set(this);
		Thread fechamento = new Thread(new Runnable() { // Gancho/Trigger de fechamento no programa.
	        public void run() {
	        	terminar();
	        }
	    }, "Shutdown-thread");
		Runtime.getRuntime().addShutdownHook(fechamento);
    }

    public void run() {
        while (erros < 3 && !terminando) {
            try {
                Log.i("Iniciando conexão com " + erros + " erros.");
                DatagramPacket packet = reconectar();
                Log.i("UDP client: about to wait to receive on port " + PORT);
                udpSocket.receive(packet);
                if (!AUTO_MSG && System.currentTimeMillis() - Sender.getLastSent() < 1000) {
                	Log.i("Escutador ignorando mensagem que o app acabou de enviar.");
                	continue;
                }
                String text = new String(packet.getData(), 0, packet.getLength()).trim();
                Log.i("Received: " + text);
                showMessage(text);
                if (EXIT_ON_RECEIVE)
                	return;
            } catch (Throwable t) {
            	if (!terminando) {
	                erros++;
	                Log.e("Erro ao tentar ouvir porta", t);
            	}
            } finally {
                fecharConexao();
            }
        }
        LanPush.alert("Since there were 3 failures, the client will no longer try to connect.");
    }

    private void showMessage(String msg) {
    	if (LanPush.isGUI()) {
    		JPanel pane = CDI.get(JPanel.class);
    		pane.add(criarNovaLinha(msg));
    		SystemTrayFrame frame = CDI.get(SystemTrayFrame.class);
    		frame.restore();
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
					Log.e("Erro ao tentar abrir navegador", t);
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
            Log.i("Socket já estava instanciado ao começar a ouvir. Será fechado...");
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
                    Log.i("Conexão já se encontra fechada.");
                else {
                    Log.i("Fechando conexão...");
//                    udpSocket.disconnect(); -> Disconnect estava travando o fechamento da aplicacao.
                    udpSocket.close();
                }
                udpSocket = null;
            } catch (Throwable t) {
                erros++;
                Log.e("Erro ao tentar fechar conexão", t);
            }
        } else {
            Log.i("Conexão nula não precisa ser fechada.");
        }
    }
    
    public void terminar() {
    	this.terminando = true;
    	fecharConexao();
    }
}