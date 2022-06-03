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
import swing.Fonte;
import swing.RelativeLayout;
import swing.SwingUtils;
import swing.Toast;
import system.SystemTrayFrame;
import utils.CDI;
import utils.Erros;

public class Lanpush {
	
	private static SystemTrayFrame mainFrame;
	private static JPanel mainPane;
	private static JTextField input;
	private static boolean GUI = false;
	
	public static void main(String[] args) {
		
		Receiver receiver = null;
		
		try {
			if (Config.getBoolean("log.output_to_console"))
				Log.setConsole(System.out);
			if (Config.getBoolean("log.file.enabled"))
				Log.iniciar(Files.getLogPath());
			if (args != null && args.length > 0) {
				if ("-l".equals(args[0]) || "--listen".equals(args[0])) {
					Log.i("Starting listener without GUI");
					receiver = new Receiver();
				}
				else {
					Log.i("Sending message: " + args[0]);
					enviarMensagem(args[0]);
				}
			}
			else {
				Log.i("Starting LANPUSH with GUI");
				GUI = true;
				mainFrame = new SystemTrayFrame("LANPUSH", Files.getIconPath(), true);
				CDI.set(mainFrame);
				mainFrame.setSize(Config.getInt("gui.window.width"), Config.getInt("gui.window.height"));
			    mainFrame.setLayout(new BorderLayout());
			    mainPane = new JPanel(SwingUtils.createLayout(RelativeLayout.Y_AXIS));
				mainFrame.add(mainPane);
				
				createInputPane();
				createMessagePane();
					
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				SwingUtils.centralizarJanela(mainFrame);
				new Fonte("Arial", Config.getInt("gui.font.size")).set(mainPane);
				
				if (Config.getBoolean("gui.start_in_tray", false) == Boolean.TRUE) {
					mainFrame.minimizeToTray();
				}
				else mainFrame.setVisible(true);
				
				receiver = new Receiver();
			}
			
			if (receiver != null)
				receiver.run();
		}
		catch (Throwable t) {
			Log.logaErro(t);
			String message = "Error! ";
			if (Log.gravando()) 
				message += "See log for more info.";
			else {
				message += Erros.resumo(t);
			}
			if (args == null || args.length == 0) {
				SwingUtils.showMessage(message);
			}
			else System.out.println(message);
		}
		finally {
			if (receiver != null)
				receiver.terminar();
			Log.terminar();
		}
		System.exit(0);
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
			Toast.makeToast(CDI.get(SystemTrayFrame.class), "MESSAGE SENT!", 2);
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
