package tit.communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

import tit.configuration.ServerConfig;
import tit.dataManagment.SongPicker;

/**
 * 
 *  
 *
 */
public class TCPServer {   

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
					SongPicker sp = new SongPicker();
					clientsPickers.put(clientIP, sp);
				}
				
				Connection c = new Connection(clientSocket);   
			}   
		}   
		catch(IOException e) {  
			System.out.println("Listen :"+e.getMessage());}   
	}  
}  







//	public static void main(String args[]) throws Exception
//	{
//		String clientSentence;
//		String capitalizedSentence;
//		ServerSocket welcomeSocket = new ServerSocket(6789);
//
//		while(true)
//		{
//			Socket connectionSocket = welcomeSocket.accept();
//			BufferedReader inFromClient =
//					new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
//			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
//			clientSentence = inFromClient.readLine();
//			System.out.println("Received: " + clientSentence);
//			capitalizedSentence = clientSentence.toUpperCase() + '\n';
//			outToClient.writeBytes(capitalizedSentence);
//		}
//	}


//TODO : function for the server 
//	public static byte[] leIntToByteArray(int i) {
//	    final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
//	    bb.order(ByteOrder.LITTLE_ENDIAN);
//	    bb.putInt(i);
//	    return bb.array();
//}
