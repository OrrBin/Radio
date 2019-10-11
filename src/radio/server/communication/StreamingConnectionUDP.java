package radio.server.communication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import radio.client.ClientConfig;
import radio.core.utilities.Util;
import radio.server.ServerConfig;

class StreamingConnectionUDP extends Thread {
	File songFile;
	DataInputStream input;
	BufferedReader bfr;
	DataOutputStream output;
	BufferedInputStream bis;
	Socket clientSocket;
	InetAddress address;
	SendSongAudioDataThread sendAudioThread;
	SendSongDetailsThreard sendDetailsThread;
	boolean isRunning = true;

	public StreamingConnectionUDP(Socket aClientSocket) {
		try {
			clientSocket = aClientSocket;
			address = aClientSocket.getInetAddress();

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
			} catch (IOException e) {
				break;
			}

			if (clientMessage == null)
				continue;

			switch (clientMessage.split(ClientConfig.messageDivider)[0]) {
			case ClientConfig.CsendMeNewSongString:
				songFile = Util.chooseRandomSong(ServerConfig.baseFolder);
				sendDetailsThread = new SendSongDetailsThreard(clientSocket, songFile);
				sendDetailsThread.start();
				break;
			case ClientConfig.CsendMeAudioData:
				if (sendAudioThread != null) {
					sendAudioThread.close();
				}
				sendAudioThread = new SendSongAudioDataThread(clientSocket, songFile);
				sendAudioThread.start();
				break;
			case ClientConfig.CsendByeString:
				isRunning = false;
				if (sendAudioThread != null)
					sendAudioThread.close();
				break;
			}
		}

		if (sendAudioThread != null) {
			sendAudioThread.close();
		}

		if (clientSocket != null) {
			try {
				if (bis != null)
					bis.close();
				if (bfr != null)
					bfr.close();
				if (output != null)
					output.close();
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}