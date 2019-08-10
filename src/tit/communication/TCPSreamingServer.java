package tit.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

import tit.configuration.ServerConfig;
import tit.dataManagment.SongPicker;

public class TCPSreamingServer extends Thread{

	static HashMap<String, SongPicker> clientsPickers;

	public static void main (String args[]) throws ClassNotFoundException, SQLException   
	{   
		clientsPickers = new HashMap<>();
		try
		{   
			
			//Create new listening socket
			ServerSocket listenSocket = new ServerSocket(ServerConfig.serverPort);   
		
			System.out.println("server start listening... ... ...");  
			while(true) {   
				Socket clientSocket = listenSocket.accept(); 
				String clientIP = clientSocket.getInetAddress().toString();
				if(!clientsPickers.containsKey(clientIP))
				{
//					SongPicker sp = new SongPicker();
					SongPicker sp = null;
					clientsPickers.put(clientIP, sp);
				}
				
				StreamingConnection c = new StreamingConnection(clientSocket);   
			}   
		}   
		catch(IOException e) {  
			System.out.println("Listen :"+e.getMessage());}   
	}  
}

//TODO : function for the server
//	public static byte[] leIntToByteArray(int i) {
//	    final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
//	    bb.order(ByteOrder.LITTLE_ENDIAN);
//	    bb.putInt(i);
//	    return bb.array();
//}
