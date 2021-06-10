package lanpush;

import javax.swing.JFrame;

import system.Sistema;
import system.SystemTrayFrame;

public class LanPush {
	
	private static JFrame mainFrame;
	
	public static void main(String[] args) {
		
		// mainFrame = new SystemTrayFrame("Lanpush", Sistema.getSystemPath() + "lanpush.png")
		mainFrame = new JFrame("Lanpush");
		mainFrame.setUndecorated(true);
		
	}

}
