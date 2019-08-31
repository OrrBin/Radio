package tit.communication;

import tit.configuration.ServerConfig;
import tit.dataManagment.SongPicker;
import tit.objects.ClientState;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.HashMap;

public class UDPStreamingServer extends Thread {

	static HashMap<InetAddress, StreamingConnectionUDP> clients = new HashMap<>();
	
	public static void main (String args[]) throws ClassNotFoundException, SQLException   
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
