package lanpush;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swing.Fonte;
import swing.RelativeLayout;
import swing.SwingUtils;
import system.Sistema;

public class LanPush {
	
	private static JFrame mainFrame;
	private static JPanel mainPane;
	private static JTextField input;
	
	public static void main(String[] args) {
		
		try {
			mainFrame = new JFrame("Lanpush");
		    mainFrame.setSize(800, 100);
		    mainFrame.setLayout(new BorderLayout());
		    mainFrame.setUndecorated(true);
		    mainPane = new JPanel(SwingUtils.createLayout(RelativeLayout.Y_AXIS));
			mainFrame.add(mainPane);
			
			createInputPane();
				
			mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			SwingUtils.centralizarJanela(mainFrame);
			Fonte.ARIAL_40.set(mainPane);
			mainFrame.setVisible(true);
		}
		catch(Throwable t) {
			JOptionPane.showMessageDialog(null, "Oooops: " + t.getMessage());
		}
	}
	
	private static void createInputPane() {
		JPanel inputPane = new JPanel(SwingUtils.createLayout(RelativeLayout.X_AXIS));
		mainPane.add(inputPane, new Float(5));
		
		input = new JTextField();
		inputPane.add(input, new Float(8));
		
		input.setFocusTraversalKeysEnabled(false);
		input.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 10)
					enviarMensagem();
			}
		});
		
		JButton okButton = new JButton("Enviar");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enviarMensagem();
			}
		});
		inputPane.add(okButton, new Float(2));
	}
	
	private static void enviarMensagem() {
		Sistema.executar("echo ' " + input.getText() + "' | nc -w 1 -b -u 192.168.0.255 1050");
		System.exit(0);
	}
}
