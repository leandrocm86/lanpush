package lanpush;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import io.Log;
import utils.Str;

public class Sender {
    private static final Str[] HOSTS = Config.getAll("send_to_ips");
    private static final int PORT = Config.getInt("connection.port");

    public static void send(String message) throws IOException {
    	
    	for (Str host : HOSTS) {
    		Log.i("Sending message to " + host);
    		
	        // Get the internet address of the specified host
	        InetAddress address = InetAddress.getByName(host.toString());
	
	        // Initialize a datagram packet with data and address
	        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, PORT);
	
	        // Create a datagram socket, send the packet through it, close it.
	        DatagramSocket dsocket = new DatagramSocket();
	        dsocket.send(packet);
	        dsocket.close();
    	}
    }
}