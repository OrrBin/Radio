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
	public final static File songFile = new File("D:\\projects\\Radio\\RadioTit-server\\music\\Your Anchor.mp3");
	// public final static File songFile = new
	// File("D:\\projects\\Radio\\RadioTit-server\\wav\\Good Times Bad Times.wav");

	DataInputStream input;
	BufferedReader bfr;
	DataOutputStream output;
	BufferedInputStream bis;
	Socket clientSocket;
	InetAddress address;
	SendSongAudioDataThread sendAudioThread;
	SendSongDetailsThreard sendDetailsThread;
	SendCategoriesThread sendCategoriesThread;
	boolean isRunning = true;

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

		while (isRunning) {

			try {
				// Read Massege From client
				clientMessage = bfr.readLine();
				System.out.println(this.getClass().getName() + " clientMessage = " + clientMessage);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

			System.out.println(String.format("Got message: %s", clientMessage));

			switch (clientMessage.split(ClientConfig.messageDivider)[0]) {
			case ClientConfig.CsendMeNewSongString:
				if (sendDetailsThread == null) {
					sendDetailsThread = new SendSongDetailsThreard(clientSocket, songFile);
					sendDetailsThread.start();
				}
				break;
			case ClientConfig.CsendMeCategoriesString:
				if (sendCategoriesThread == null) {
					sendCategoriesThread = new SendCategoriesThread(clientSocket);
					sendCategoriesThread.start();
				}
				break;
			case ClientConfig.CsendMeAudioData:
				System.out.println("sending song data!!!!");
				if (sendAudioThread == null) {
					sendAudioThread = new SendSongAudioDataThread(clientSocket, songFile);
					sendAudioThread.start();
				}

				break;
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