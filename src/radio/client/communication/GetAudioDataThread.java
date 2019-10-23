package radio.client.communication;

import java.io.DataOutputStream;
import java.io.IOException;

import radio.client.ClientConfig;
import radio.core.utilities.BytesUtil;

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
			output.writeBytes(BytesUtil.clientMessage(ClientConfig.CsendMeAudioData ));
		} catch (IOException e) {
			System.out.println(this.getClass() + " Can't ask for a song");
			e.printStackTrace();
		}
	}
}
