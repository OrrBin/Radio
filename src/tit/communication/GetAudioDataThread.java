package tit.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import tit.configuration.ClientConfig;

public class GetAudioDataThread extends Thread {

	DataOutputStream output;

	public GetAudioDataThread(DataOutputStream output) {
		super();
		this.output = output;
	}

	@Override
	public void run() {
		try {
			getAudioData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getAudioData() throws IOException {

		// clientSocket = new Socket(ServerConfig.serverAddr, ServerConfig.serverPort);

//		output = new DataOutputStream(clientSocket.getOutputStream());
		// Ask for a new Song
		try {
			output.writeBytes(ClientConfig.CsendMeAudioData + ClientConfig.messageDivider + System.lineSeparator());
		} catch (IOException e) {
			System.out.println(this.getClass() + " Can't ask for a song");
			e.printStackTrace();
		}
	}
}
