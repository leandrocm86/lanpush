package lanpush;

import java.awt.TextArea;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import io.Log;
import swing.SwingUtils;
import system.SystemTrayFrame;
import utils.CDI;
import utils.Data;

public class Receiver {

    private static DatagramSocket udpSocket;
    private static long ultimaMensagem = 0;
    private int erros = 0;
    private boolean terminando = false;
    private boolean GUI;
    
    public Receiver(boolean GUI) {
    	CDI.set(this);
    	this.GUI = GUI;
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
                Log.i("UDP client: about to wait to receive");
                udpSocket.receive(packet);
                String text = new String(packet.getData(), 0, packet.getLength()).trim();
                Log.i("Received: " + text);
                if (System.currentTimeMillis() - ultimaMensagem > 3000) // Espera um tempo pra ouvir de novo, evitando mensagens duplicadas.
                    showMessage(text);
                ultimaMensagem = System.currentTimeMillis();
            } catch (Throwable t) {
            	if (!terminando) {
	                erros++;
	                Log.e("Erro ao tentar ouvir porta", t);
            	}
            } finally {
                fecharConexao();
            }
        }
        SwingUtils.showMessage("Como houveram 3 erros, o client está deixando de ouvir.");
        System.exit(1);
    }

    private void showMessage(String msg) {
    	msg = new Data().toStr(Data.DATA_dd_MM_HH_mm_ss) + ": " + msg + "\n";
    	if (GUI) {
			CDI.get(TextArea.class).append(msg);
			CDI.get(SystemTrayFrame.class).restore();
    	}
    	else {
    		System.out.println(msg);
    	}
	}

	private DatagramPacket reconectar() throws SocketException {
        if (udpSocket != null) {
            Log.i("Socket já estava instanciado ao começar a ouvir. Será fechado...");
            fecharConexao();
        }
        udpSocket = new DatagramSocket(1050);
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