package tit.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.util.ArrayList;

import tit.audio.Song;
import tit.configuration.ClientConfig;
import tit.configuration.GeneralConfig;
import tit.configuration.ServerConfig;
import tit.dataManagment.DataManagerException;
import tit.dataManagment.SongPicker;
import tit.dbUtilities.DBUtil;

class Connection extends Thread 
{   
	DataInputStream input;   
	BufferedReader bfr;
	DataOutputStream output;  
	BufferedInputStream bis;
	Socket clientSocket;
	//	BufferedOutputStream outToClient;

	SongPicker songPicker;

	public Connection (Socket aClientSocket) throws ClassNotFoundException, SQLException 
	{   
		try {   
			clientSocket = aClientSocket;
			//TODO			BufferedOutputStream outToClient = null; 



			input = new DataInputStream(clientSocket.getInputStream()); 
			bfr = new BufferedReader(new InputStreamReader(input));
			output =new DataOutputStream(clientSocket.getOutputStream());

			songPicker = TCPServer.clientsPickers.get(aClientSocket.getInetAddress().toString());
			this.start();   
		}   
		catch(IOException e) {  
			System.out.println("Connection:"+e.getMessage());  
		}   
	}   

	public void run() {   

		//		boolean connectionOpen = true;
		//		while(connectionOpen)
		//		{
		String clientMessage = null;
		try {
			//Read Massege From client
			clientMessage = bfr.readLine();
			System.out.println("clientMessage = " + clientMessage);
		} catch (IOException e) 
		{
			e.printStackTrace();
			//				throw new CancellationException("Can not read message from client");
		}
		switch(clientMessage.split(ClientConfig.messageDivider)[0])
		{
		case ClientConfig.CsendMeNewSongString:
			try 
			{
				sendSongData(clientMessage.split(ClientConfig.messageDivider)[1]);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			break;

		case ClientConfig.CsendMeCategoriesString:
			try 
			{
				sendCategories();
			}
			catch (ClassNotFoundException | IOException e) 
			{
				e.printStackTrace();
			}
			break;

		case ClientConfig.CsendByeThereString: closeConnection();	break;
		}   
		//		}
	}  

	//	BufferedOutputStream outToClient;



	private void sendSongData(String category) throws IOException
	{
		//Shuffle a song
		Song song = songPicker.shuffle(category);

		//Files to send
		File songFile = song.getSongFile();
		File imageFile = song.getSongImage();

		FileInputStream songFis = null;
		FileInputStream imageFis = null;
		BufferedInputStream songBis = null;
		BufferedInputStream imageBis = null;
		BufferedOutputStream out = null;

		//Create output stream
		out = new BufferedOutputStream(clientSocket.getOutputStream());

		try
		{
			//Create song file size header
			long songLength = songFile.length();
			if(songLength > Integer.MAX_VALUE)
				throw new DataManagerException(getClass() + " Song file " + songFile.getPath()
				+ " is too large");
			byte[] songFileLength = leIntToByteArray((int)songLength);

			//Send song file size header
			out.write(songFileLength, 0, ServerConfig.fileSizeHeader);

			//Create image file size header
			long imageLength = imageFile.length();
			if(imageLength > Integer.MAX_VALUE)
				throw new DataManagerException(getClass() + " Image file " + imageFile.getPath()
				+ " is too large");
			byte[] imageFileLength = leIntToByteArray((int)imageLength);

			//Send image file size header
			out.write(imageFileLength, 0, ServerConfig.fileSizeHeader);

			//Create byte array to hold song file data 
			//			byte[] songBytes = new byte[(int) songLength];
			byte[] songBytes = new byte[clientSocket.getSendBufferSize()];

			songFis = new FileInputStream(songFile);
			songBis = new BufferedInputStream(songFis);

			int count = 0;
			//Send the file's content
			System.out.println(getClass() + " sanding song...");
			while((count = songBis.read(songBytes)) > 0)
			{
				out.write(songBytes, 0, count);
			}
			System.out.println(getClass() + " song sent...");

			//Create byte array to hold image file data 
			byte[] imageBytes = new byte[(int) imageLength];

			imageFis = new FileInputStream(imageFile);
			imageBis = new BufferedInputStream(imageFis);

			count = 0;
			//Send the file's content
			System.out.println("Sending image...");
			while((count = imageBis.read(imageBytes)) > 0)
			{
				out.write(imageBytes, 0, count);
			}
			System.out.println("image was sent...");

		}
		catch(DataManagerException e)
		{
			e.printStackTrace();
			//Try to send another song
			//			sendSongData(category);
		}
		finally
		{
			if(songFis != null)
				songFis.close();
			if(songBis != null)
				songBis.close();

			if(imageFis != null)
				imageFis.close();
			if(imageBis != null)
				imageBis.close();

			if(out != null)
			{
				out.flush();
				out.close();
			}
		}

	}


	private void sendCategories() throws ClassNotFoundException, IOException
	{
		BufferedOutputStream out = null;
		try
		{
			//Create output stream
			out = new BufferedOutputStream(clientSocket.getOutputStream());

			DBUtil dbUtil = new DBUtil();
			ArrayList<String> categories = dbUtil.getCategories();

			StringBuffer strbuf = new StringBuffer();

			//Default category
			strbuf.append(GeneralConfig.randomCategory);
			for(String s : categories)
			{
				strbuf.append(",").append(s);
			}

			String stringToSend = strbuf.toString();
			stringToSend.substring(0,stringToSend.length() - 1);

			out.write(stringToSend.getBytes());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally 
		{
			if(out != null)
			{
				out.flush();
				out.close();
			}
		}
	}

	private void closeConnection() 
	{
		// TODO Auto-generated method stub

	}

	/**
	 * returns byte array with {ServerConfig.FileNameHeaderLengthSize} cells,
	 * that holds the int
	 * @param i
	 * @return
	 */
	public static byte[] leIntToByteArray(int i) {
		final ByteBuffer bb = ByteBuffer.allocate(ServerConfig.fileSizeHeader);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(i);
		return bb.array();
	}	
}