package tit.communication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.SQLException;

import tit.configuration.ClientConfig;

class StreamingConnectionUDP extends Thread {

	// TODO : why must be static ??
	public static File dirRandom(File dirPath) {
		File[] files = dirPath.listFiles();
		return files[(int) (files.length * Math.random())];
	}

	// public final static File songFile = dirRandom(new
	// File("RadioTit-server/music/"));
	public final static File songFile = new File("RadioTit-server/music/Your Anchor.mp3");

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
			case ClientConfig.CsendByeString:
				isRunning = false;
				sendAudioThread.close();
				break;
			}
		}

		if (sendAudioThread != null) {
			sendAudioThread.close();
		}

		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}