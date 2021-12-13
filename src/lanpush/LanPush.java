package lanpush;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import io.Log;
import swing.RelativeLayout;
import swing.SwingUtils;
import system.SystemTrayFrame;
import utils.CDI;
import utils.Erros;
import utils.Str;

public class LanPush {
	
	private static JFrame mainFrame;
	private static JPanel mainPane;
	private static JTextField input;
	private static boolean GUI = false;
	
	public static void main(String[] args) {
		
//		Files.setTestFolder("/home/lcm/SerproDrive/apps/lanpush/tests/");
		
		try {
			Log.iniciar(Files.getLogPath());
			if (args != null && args.length > 0) {
				if ("-l".equals(args[0]) || "--listen".equals(args[0])) {
					Log.i("Iniciando listener sem GUI");
					new Receiver().run();
				}
				else {
					Log.i("Enviando mensagem: " + args[0]);
					enviarMensagem(args[0]);
				}
			}
			else {
				Log.i("Iniciando LANPUSH com GUI");
				GUI = true;
				mainFrame = new SystemTrayFrame("Lanpush", Files.getIconPath(), true);
				CDI.set(mainFrame);
				if (SwingUtils.getScreenHeight() > 1080)
					mainFrame.setSize(1500, 500);
				else
					mainFrame.setSize(800, 500);
			    mainFrame.setLayout(new BorderLayout());
			    mainPane = new JPanel(SwingUtils.createLayout(RelativeLayout.Y_AXIS));
				mainFrame.add(mainPane);
				
				createInputPane();
				createMessagePane();
					
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				SwingUtils.centralizarJanela(mainFrame);
				SwingUtils.setDefaultFont(mainPane);
				mainFrame.setVisible(true);
				new Receiver().run();
			}
		}
		catch(Throwable t) {
			Log.logaErro(t);
			String message = "Error! " + (Str.vazia(t.getMessage()) ? "See log for more info." : t.getMessage());
			if (args == null || args.length == 0) {
				SwingUtils.showMessage(message);
			}
			else System.out.println(message);
		}
		Log.terminar();
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
	
	private static void createMessagePane() {
		JPanel msgPane = new JPanel(SwingUtils.createLayout(RelativeLayout.Y_AXIS, 0, 0, true));
		CDI.set(msgPane);
		JScrollPane scrollPane = SwingUtils.createScrollPane(msgPane, 30, true);
		mainPane.add(scrollPane, 7f);
	}
	
	private static void enviarMensagem(String msg) {
		try {
			Sender.send(msg);
			if (input != null)
				input.setText("");
		} catch (IOException e) {
			SwingUtils.showMessage(Erros.resumo(e));
			SwingUtils.showMessage(Erros.stackTraceToStr(e, 10));
		}
	}
	
	public static void alert(String msg) {
		if (GUI) {
			SwingUtils.showMessage(msg);
		}
		else System.out.println(msg);
	}
	
	public static boolean isGUI() {
		return GUI;
	}
}
