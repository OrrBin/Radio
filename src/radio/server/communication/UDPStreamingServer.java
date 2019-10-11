package radio.server.communication;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

import radio.server.ServerConfig;

public class UDPStreamingServer extends Thread {

	static HashMap<InetAddress, StreamingConnectionUDP> clients = new HashMap<>();
	
	public static void main (String args[])
	{
		try
		{
			//Create new listening socket
			ServerSocket listenSocket = new ServerSocket(ServerConfig.serverPort);   
		
			System.out.println("server start listening... ... ...");  
			while(true) {   
				Socket clientSocket = listenSocket.accept(); 
				InetAddress address = listenSocket.getInetAddress();
				clients.put(address, new StreamingConnectionUDP(clientSocket));
			}   
		}   
		catch(IOException e) {  
			System.out.println("Listen :"+e.getMessage());}   
	}  
}
