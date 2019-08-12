package tit.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sound.sampled.*;

import tit.audio.Song;
import tit.configuration.ClientConfig;
import tit.configuration.GeneralConfig;
import tit.configuration.ServerConfig;
import tit.dataManagment.SongPicker;
import tit.dbUtilities.AudioUtil;
import tit.dbUtilities.DBUtil;
import utilities.Util;

class StreamingConnectionUDP extends Thread {
	public final static File songFile = new File("D:\\projects\\Radio\\RadioTit-server\\music\\Back In Black.mp3");
//	public final static File songFile = new File("D:\\projects\\Radio\\RadioTit-server\\wav\\Good Times Bad Times.wav");

	DataInputStream input;
	BufferedReader bfr;
	DataOutputStream output;
	BufferedInputStream bis;
	Socket clientSocket;
	InetAddress address;
	// BufferedOutputStream outToClient;

	public StreamingConnectionUDP(Socket aClientSocket) throws ClassNotFoundException, SQLException {
		try {
			clientSocket = aClientSocket;
			address = aClientSocket.getInetAddress();
			// TODO BufferedOutputStream outToClient = null;

			input = new DataInputStream(clientSocket.getInputStream());
			bfr = new BufferedReader(new InputStreamReader(input));
			output = new DataOutputStream(clientSocket.getOutputStream());
			this.start();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		}
	}

	public void run() {
		String clientMessage = null;
		try {
			//Read Massege From client
			clientMessage = bfr.readLine();
			System.out.println(this.getClass().getName() + " clientMessage = " + clientMessage);
		} catch (IOException e) 
		{
			e.printStackTrace();
			//				throw new CancellationException("Can not read message from client");
		}
		switch(clientMessage.split(ClientConfig.messageDivider)[0])
		{
		case ClientConfig.CsendMeNewSongString:
			try {
				sendSongData(songFile);
			} catch (IOException | UnsupportedAudioFileException e) {
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
		case ClientConfig.CsendMeAudioData:
			System.out.println("sending song data!!!!");
			try {
				sendSongAudioData(songFile);
			} catch (IOException | UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
			break;
		}   
		
	}

	private void sendSongData(File songFile) throws IOException, UnsupportedAudioFileException {
		long fileSize = songFile.length();

		FileInputStream songFis = null;
		BufferedInputStream songBis = null;
		BufferedOutputStream out = null;
		String songName = "song name";
		String albumName = "album name";
		String artistName = "artist name";

		AudioFormat decodedFormat = AudioUtil.getFormat(songFile);

		float sampleRate = decodedFormat.getSampleRate();
		int sampleSizeInBits = decodedFormat.getSampleSizeInBits();
		int channels = decodedFormat.getChannels();
		boolean signed = true;
		boolean bigEndian = decodedFormat.isBigEndian();
		
		
		System.out.println("song : " + songName);
		System.out.println("album : " + albumName);
		System.out.println("artist : " + artistName);
		System.out.println("sample rate : " + sampleRate);
		System.out.println("sample size in bits : " + sampleSizeInBits);
		System.out.println("channels : " + channels);
		System.out.println("signed : " + signed);
		System.out.println("bigEndian : " + bigEndian);

		try {

			// Create output stream
			out = new BufferedOutputStream(clientSocket.getOutputStream());

			byte[] songBytes = new byte[clientSocket.getSendBufferSize()];

			songFis = new FileInputStream(songFile);
			songBis = new BufferedInputStream(songFis);

			/*************** Send song properties headers *****************/
			// Send song name size header
			out.write(Util.getStringSizeInBytes(songName));

			// Send song name header
			out.write(Util.StringToByteArray(songName));

			// Send album name size header
			out.write(Util.getStringSizeInBytes(albumName));

			// Send album name header
			out.write(Util.StringToByteArray(albumName));

			// Send artist name size header
			out.write(Util.getStringSizeInBytes(artistName));

			// Send artist name header
			out.write(Util.StringToByteArray(artistName));

			/*************** Send song file properties headers *****************/
			// Send song file size in bytes
			out.write(Util.LongToByteArray(fileSize));

			/*************** Send AudioFormat properties headers *****************/
			// Send sample rate header
			out.write(Util.FloatToByteArray(sampleRate));

			// Send sample size in bits header
			out.write(Util.leIntToByteArray(sampleSizeInBits));

			// Send channels header
			out.write(Util.leIntToByteArray(channels));

			// Send signed header
			out.write(Util.booleanToByteArray(signed));

			// Send bigEndian header
			out.write(Util.booleanToByteArray(bigEndian));

		} finally {
			songFis.close();
			songBis.close();
			out.flush();
			out.close();
			// TODO: clientSocket.close();
		}

	}

	public void sendSongAudioData(File songFile) throws IOException, UnsupportedAudioFileException {
		DatagramSocket socket = null;
		AudioInputStream audioIS = null;
		try {
			socket = new DatagramSocket();
			audioIS = AudioUtil.getFAudioInputStream(songFile);
			int count, numPackets = 0;
			byte[] songBytes = new byte[ServerConfig.DATAGRAM_PACKET_SIZE];
			while ((count = audioIS.read(songBytes)) > 0) {
				DatagramPacket packet = new DatagramPacket(songBytes, count, address, ClientConfig.UdpPort);
				socket.send(packet);
				numPackets++;
				
				Thread.sleep(15);
			}
			
		System.out.println(numPackets);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
			if (audioIS != null)
				audioIS.close();
		}
	}

	private void sendCategories() throws ClassNotFoundException, IOException
	{
		BufferedOutputStream out = null;
		try
		{
			//Create output stream
			out = new BufferedOutputStream(clientSocket.getOutputStream());

//			DBUtil dbUtil = new DBUtil();
//			ArrayList<String> categories = dbUtil.getCategories();
//
			StringBuffer strbuf = new StringBuffer();

			//Default category
			strbuf.append(GeneralConfig.randomCategory);
//			for(String s : categories)
//			{
//				strbuf.append(",").append(s);
//			}

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
	/**
	 * returns byte array with {ServerConfig.FileNameHeaderLengthSize} cells, that
	 * holds the int
	 * 
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