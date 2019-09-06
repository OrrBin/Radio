package tit.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import tit.configuration.ClientConfig;
import utilities.Util;

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
		// Ask for a new Song
		try {
			output.writeBytes(Util.clientMessage(ClientConfig.CsendMeAudioData ));
		} catch (IOException e) {
			System.out.println(this.getClass() + " Can't ask for a song");
			e.printStackTrace();
		}
	}
}
