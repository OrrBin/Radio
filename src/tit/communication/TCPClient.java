package tit.communication;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.naming.CommunicationException;

import tit.audio.Song;
import tit.configuration.ClientConfig;
import tit.configuration.DataManagmenetConfig;
import tit.configuration.ServerConfig;
import tit.dataManagment.DataManagmentUtilities;

/**
 * 
 *  
 *
 */
public class TCPClient
{

	Socket clientSocket ;
	DataOutputStream output;
	DataInputStream input;

	DataOutputStream outToServer;
	InputStreamReader inFromServer;
	BufferedReader stringInFromServer;

	private File musicDirectory;
	private File imagesDirectory;

	public TCPClient(String server, int port, File baseDirectory) throws UnknownHostException, IOException, CommunicationException 
	{
		this.musicDirectory = new File(baseDirectory.getPath() + ClientConfig.DefaultMusicFolder);
		this.imagesDirectory = new File(baseDirectory.getPath() + ClientConfig.DefaultImagesFolder);
	}

	public Song getSongData(String category) throws IOException
	{

		clientSocket = new Socket(ServerConfig.serverIP, ServerConfig.serverPort);

		output = new DataOutputStream( clientSocket.getOutputStream()); 

		//Ask for a new Song
		try	{
			output.writeBytes(ClientConfig.CsendMeNewSongString + 
					ClientConfig.messageDivider + category + System.lineSeparator() );
		} catch (IOException e)	{
			System.out.println(this.getClass() + " Can't ask for a song");
			e.printStackTrace();
		}

		//TODO: check if needed
		//			try	{
		//				Thread.sleep(500);
		//			} catch (InterruptedException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}

		//Create files 
		File songFile = new File(this.musicDirectory + DataManagmenetConfig.pathSeparator
				+ DataManagmenetConfig.currentSongFileName);
		if(songFile.exists())
		{
			songFile.delete();

		}

		try {
			songFile.createNewFile();
		} catch (IOException e) {
			System.out.println("Cannot create new song file");
			e.printStackTrace();
		}

		File imageFile = new File(this.imagesDirectory + DataManagmenetConfig.pathSeparator
				+ DataManagmenetConfig.currentImageFileName);
		if(imageFile.exists())
		{
			imageFile.delete();

		}

		try {
			imageFile.createNewFile();
		} catch (IOException e) {
			System.out.println("Cannot create new image file");
			e.printStackTrace();
		}

		InputStream is = null;
		FileOutputStream songFos = null;
		FileOutputStream imageFos = null;
		BufferedOutputStream songBos = null;
		BufferedOutputStream imageBos = null;

		ByteArrayOutputStream baos = null;

		int bufferSize = 0;
		try
		{
			is = clientSocket.getInputStream();
			bufferSize = clientSocket.getReceiveBufferSize();

			songFos = new FileOutputStream(songFile);
			imageFos = new FileOutputStream(imageFile);
			songBos = new BufferedOutputStream(songFos);
			imageBos = new BufferedOutputStream(imageFos);

			baos = new ByteArrayOutputStream();

			byte[] songLenBytes = new byte[ServerConfig.fileSizeHeader];
			byte[] imageLenBytes = new byte[ServerConfig.fileSizeHeader];

			int songLen = 0;
			int imageLen = 0;

			byte[] bytes = new byte[bufferSize];
			int index = 0;
			int count = 0;
			while((count = is.read(bytes)) > 0)
			{
				if(index == 0)
				{
					//Read the song file size into variable
					for(int i = 0; i < ServerConfig.fileSizeHeader; i++)
					{
						songLenBytes[i] = bytes[i];
					}
					songLen = byteArrayToLeInt(songLenBytes);

					//Read the image file size into variable
					for(int i = 0; i < ServerConfig.fileSizeHeader; i++)
					{
						imageLenBytes[i] = bytes[ServerConfig.fileSizeHeader + i];
					}
					imageLen = byteArrayToLeInt(imageLenBytes);

					int offset = ServerConfig.fileSizeHeader * 2;

					//Read the start of the file
					baos.write(bytes, offset, count - offset);					
				}

				else
				{
					baos.write(bytes, 0, count);
				}

				index++;
			}

			//Insert data to files
			byte[] generalBytes = baos.toByteArray();
			//TODO
//			byte[] songBytes = new byte[songLen];
//			byte[] imageBytes = new byte[imageLen];
			songBos.write(generalBytes, 0 , songLen);

			int off = songLen;

			imageBos.write(generalBytes, off, imageLen);

		}
		catch(IOException e)
		{
			//Try to ask for  another song
			//			getSongData(category);
			e.printStackTrace();
		}
		finally
		{
			if(songBos != null)
			{
				songBos.flush();
				songBos.close();
			}

			if(imageBos != null)
			{
				imageBos.flush();
				imageBos.close();
			}
			if(baos != null)
				baos.close();

			if(is != null)
				is.close();

			if(output != null)
				output.close();

			if(clientSocket != null)
				clientSocket.close();
		}

		return DataManagmentUtilities.createSong(songFile, imageFile);

	}


	public String[] getCategories() throws IOException
	{
		clientSocket = new Socket(ServerConfig.serverIP, ServerConfig.serverPort);

		output = new DataOutputStream( clientSocket.getOutputStream()); 

		//Ask for a new Song
		try	{
			output.writeBytes(ClientConfig.CsendMeCategoriesString + ClientConfig.messageDivider + System.lineSeparator());
		} catch (IOException e)	{
			System.out.println(this.getClass() + " Can't ask for a song");
			e.printStackTrace();
		}
		
		InputStream is = null;
		FileOutputStream songFos = null;
		FileOutputStream imageFos = null;
		BufferedOutputStream songBos = null;
		BufferedOutputStream imageBos = null;

		ByteArrayOutputStream baos = null;

		int bufferSize = 0;
		try
		{
			is = clientSocket.getInputStream();
			bufferSize = clientSocket.getReceiveBufferSize();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		baos = new ByteArrayOutputStream();
		byte[] bytes = new byte[bufferSize];
		int count = 0;
		while((count = is.read(bytes)) > 0)
		{
				baos.write(bytes,0,count);
		}
		
		String categoriesList = new String(baos.toByteArray());
		
		String[] categoriesArr = categoriesList.split(",");
		
//		ArrayList<String> categories = new ArrayList<>();
//		for(String s : categoriesArr)
//			categories.add(s);
//		
//		return categories;
		return categoriesArr;
		
	}
	
	

	public static int byteArrayToLeInt(byte[] b) 
	{
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}


	public static void main(String argv[]) throws Exception
	{
		//		new MainPage().initAndShowGUI(new SongMp3("Small Change Girl", "Poor Boy / Lucky Man", "Asaf Avidan",
		//				new File("C:\\orr\\songs\\SmallChangeGirl.mp3"), new File("C:\\orr\\songs\\sultans of swing.jpg"),100));
		//		String sentence;
		//		String modifiedSentence;
		//		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
		//		Socket clientSocket = new Socket("localhost", 6789);
		//		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		//		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		//		outToServer.writeBytes(sentence + '\n');
		//		sentence = inFromUser.readLine();
		//		outToServer.writeBytes(sentence + '\n');
		//		modifiedSentence = inFromServer.readLine();
		//		System.out.println("FROM SERVER: " + modifiedSentence);
		//		clientSocket.close();

		TCPClient tcp = new TCPClient(ServerConfig.serverIP, ServerConfig.serverPort, new File("D:\\RadioTit-client"));
		//		Thread.sleep(1000);
		//		tcp.getSongFromServer(new File("C:\\orr\\songs\\tcpTest"), "asaf");
		tcp.getSongData("asaf");
		//		tcp.getSongData(new File("C:\\orr\\songs\\tcpTest"), "asaf");
	}

	
}
