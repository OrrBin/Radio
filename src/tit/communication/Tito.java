//package tit.communication;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.util.ArrayList;
//
//import javax.naming.CommunicationException;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.DataLine;
//import javax.sound.sampled.SourceDataLine;
//import javax.sound.sampled.TargetDataLine;
//import javax.xml.transform.Source;
//
//import tit.audio.Song;
//import tit.configuration.ClientConfig;
//import tit.configuration.DataManagmenetConfig;
//import tit.configuration.ServerConfig;
//import tit.dataManagment.DataManagmentUtilities;
//
///**
// * 
// *  
// *
// */
//public class TCPClient
//{
//
//	Socket clientSocket ;
//	DataOutputStream output;
//	DataInputStream input;
//
//	DataOutputStream outToServer;
//	InputStreamReader inFromServer;
//	BufferedReader stringInFromServer;
//
//	private File musicDirectory;
//	private File imagesDirectory;
//
//	public TCPClient(String server, int port, File baseDirectory) throws UnknownHostException, IOException, CommunicationException 
//	{
//		this.musicDirectory = new File(baseDirectory.getPath() + ClientConfig.DefaultMusicFolder);
//		this.imagesDirectory = new File(baseDirectory.getPath() + ClientConfig.DefaultImagesFolder);
//	}
//
//	public void getSongData(String category) throws IOException
//	{
//
//		/*clientSocket = new Socket(ServerConfig.serverIP, ServerConfig.serverPort);
//
//		output = new DataOutputStream( clientSocket.getOutputStream()); 
//
//		//Ask for a new Song
//		try	{
//			output.writeBytes(ClientConfig.CsendMeNewSongString + 
//					ClientConfig.messageDivider + category + System.lineSeparator() );
//		} catch (IOException e)	{
//			System.out.println(this.getClass() + " Can't ask for a song");
//			e.printStackTrace();
//		}
//
//		//TODO: check if needed
//		//			try	{
//		//				Thread.sleep(500);
//		//			} catch (InterruptedException e) {
//		//				// TODO Auto-generated catch block
//		//				e.printStackTrace();
//		//			}
//*/
//		InputStream is = null;
//		FileOutputStream songFos = null;
//		FileOutputStream imageFos = null;
//		BufferedOutputStream songBos = null;
//		BufferedOutputStream imageBos = null;
//
//		ByteArrayOutputStream baos = null;
//
//		SourceDataLine line;
//		SourceDataLine sline;
//		AudioFormat format;
//		
//		File soundFile = new File("D:\\sounds\\Ring05.wav");
//		FileInputStream fis;
//		BufferedInputStream bis;
//		
//		int bufferSize = 0;
//		try
//		{
////			is = clientSocket.getInputStream();
//			
//			fis = new FileInputStream(soundFile);
//			bis = new BufferedInputStream(fis);
//			
////			bufferSize = clientSocket.getReceiveBufferSize();
//			format = new AudioFormat(8000,16,1,true,false);
//			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
//			line = (SourceDataLine) AudioSystem.getLine(info);
//			line.open(format);
//			line.start();
//			
//			byte[] bytes = null;
//			int count;
//			while((count = bis.read(bytes)) != 0)
//			{
//				line.write(bytes,0,count);
//			}
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//
//	public String[] getCategories() throws IOException
//	{
//		clientSocket = new Socket(ServerConfig.serverIP, ServerConfig.serverPort);
//
//		output = new DataOutputStream( clientSocket.getOutputStream()); 
//
//		//Ask for a new Song
//		try	{
//			output.writeBytes(ClientConfig.CsendMeCategoriesString + ClientConfig.messageDivider + System.lineSeparator());
//		} catch (IOException e)	{
//			System.out.println(this.getClass() + " Can't ask for a song");
//			e.printStackTrace();
//		}
//		
//		InputStream is = null;
//		FileOutputStream songFos = null;
//		FileOutputStream imageFos = null;
//		BufferedOutputStream songBos = null;
//		BufferedOutputStream imageBos = null;
//
//		ByteArrayOutputStream baos = null;
//
//		int bufferSize = 0;
//		try
//		{
//			is = clientSocket.getInputStream();
//			bufferSize = clientSocket.getReceiveBufferSize();
//		}
//		catch(IOException e)
//		{
//			e.printStackTrace();
//		}
//		
//		baos = new ByteArrayOutputStream();
//		byte[] bytes = new byte[bufferSize];
//		int count = 0;
//		while((count = is.read(bytes)) > 0)
//		{
//				baos.write(bytes,0,count);
//		}
//		
//		String categoriesList = new String(baos.toByteArray());
//		
//		String[] categoriesArr = categoriesList.split(",");
//		
////		ArrayList<String> categories = new ArrayList<>();
////		for(String s : categoriesArr)
////			categories.add(s);
////		
////		return categories;
//		return categoriesArr;
//		
//	}
//	
//	
//
//	public static int byteArrayToLeInt(byte[] b) 
//	{
//		final ByteBuffer bb = ByteBuffer.wrap(b);
//		bb.order(ByteOrder.LITTLE_ENDIAN);
//		return bb.getInt();
//	}
//
//
//	public static void main(String argv[]) throws Exception
//	{
//		//		new MainPage().initAndShowGUI(new SongMp3("Small Change Girl", "Poor Boy / Lucky Man", "Asaf Avidan",
//		//				new File("C:\\orr\\songs\\SmallChangeGirl.mp3"), new File("C:\\orr\\songs\\sultans of swing.jpg"),100));
//		//		String sentence;
//		//		String modifiedSentence;
//		//		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
//		//		Socket clientSocket = new Socket("localhost", 6789);
//		//		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//		//		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//		//		outToServer.writeBytes(sentence + '\n');
//		//		sentence = inFromUser.readLine();
//		//		outToServer.writeBytes(sentence + '\n');
//		//		modifiedSentence = inFromServer.readLine();
//		//		System.out.println("FROM SERVER: " + modifiedSentence);
//		//		clientSocket.close();
//
//		TCPClient tcp = new TCPClient(ServerConfig.serverIP, ServerConfig.serverPort, new File("D:\\RadioTit-client"));
//		//		Thread.sleep(1000);
//		//		tcp.getSongFromServer(new File("C:\\orr\\songs\\tcpTest"), "asaf");
//		tcp.getSongData("asaf");
//		//		tcp.getSongData(new File("C:\\orr\\songs\\tcpTest"), "asaf");
//	}
//
//	
//}
