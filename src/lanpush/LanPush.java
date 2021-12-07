package lanpush;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import io.Log;
import swing.Fonte;
import swing.RelativeLayout;
import swing.SwingUtils;
import system.Sistema;
import system.SystemTrayFrame;
import utils.CDI;
import utils.Erros;

public class LanPush {
	
	private static JFrame mainFrame;
	private static JPanel mainPane;
	private static JTextField input;
	
	public static void main(String[] args) {
		
		try {
			Log.iniciar(Sistema.getSystemPath() + "lanpush.log");
			if (args != null && args.length > 0) {
				if ("-l".equals(args[0]) || "--listen".equals(args[0])) {
					Log.i("Iniciando listener sem GUI");
					new Receiver(false).run();
				}
				else {
					Log.i("Enviando mensagem: " + args[0]);
					enviarMensagem(args[0]);
				}
			}
			else {
				Log.i("Iniciando LANPUSH com GUI");
				mainFrame = new SystemTrayFrame("Lanpush", Sistema.getSystemPath() + "lanpush.png", true);
			    mainFrame.setSize(800, 700);
			    mainFrame.setLayout(new BorderLayout());
			    mainPane = new JPanel(SwingUtils.createLayout(RelativeLayout.Y_AXIS));
				mainFrame.add(mainPane);
				CDI.set(mainFrame);
				
				createInputPane();
				createTextArea();
					
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				SwingUtils.centralizarJanela(mainFrame);
				Fonte.ARIAL_40.set(mainPane);
				mainFrame.setVisible(true);
				new Receiver(true).run();
			}
			Log.terminar();
		}
		catch(Throwable t) {
			if (args == null || args.length == 0) {
				SwingUtils.showMessage(Erros.resumo(t));
				SwingUtils.showMessage(Erros.stackTraceToStr(t, 10));
			}
			else {
				System.out.println(Erros.resumo(t));
				System.out.println(Erros.stackTraceToStr(t, 30));
			}
		}
	}
	
	private static void createInputPane() {
		JPanel inputPane = new JPanel(SwingUtils.createLayout(RelativeLayout.X_AXIS));
		mainPane.add(inputPane, 1f);
		
		input = new JTextField();
		inputPane.add(input, 9f);
		
		input.setFocusTraversalKeysEnabled(false);
		input.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10)
					enviarMensagem(input.getText());
			}
		});
		
		JButton okButton = new JButton("Send");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enviarMensagem(input.getText());
				input.setText("");
			}
		});
		inputPane.add(okButton, 2f);
	}
	
	private static void createTextArea() {
		TextArea textArea = new TextArea();
		CDI.set(textArea);
		mainPane.add(textArea, 9f);
	}
	
	private static void enviarMensagem(String msg) {
		try {
			Sender.send(msg);
		} catch (IOException e) {
			SwingUtils.showMessage(Erros.resumo(e));
			SwingUtils.showMessage(Erros.stackTraceToStr(e, 10));
		}
	}
}
