package radio.client.communication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.naming.CommunicationException;
import javax.sound.sampled.AudioFormat;
import radio.client.ClientConfig;
import radio.client.audio.PlayerPropetrties;
import radio.client.ui.App;
import radio.core.audio.SongDescriptors;
import radio.core.utilities.BytesUtil;
import radio.server.ServerConfig;

/**
 * 
 *  
 *
 */
public class ServerConnector {

	Socket clientSocket;
	DataOutputStream output;
	DataInputStream input;

	DataOutputStream outToServer;
	InputStreamReader inFromServer;
	BufferedReader stringInFromServer;

	public ServerConnector(String server, int port)
			throws UnknownHostException, IOException, CommunicationException {
		clientSocket = new Socket(ClientConfig.SERVER_URL, ClientConfig.SERVER_PORT);
		output = new DataOutputStream(clientSocket.getOutputStream());
	}

	public PlayerPropetrties getSongDetailsAndData() throws IOException {

		PlayerPropetrties playerPropetrties = null;
		InputStream is = null;
        long duration;
		AudioFormat format;

		// Ask for a new Song
		try {
			output.writeBytes(BytesUtil.clientMessage(ClientConfig.CsendMeNewSongString));
		} catch (IOException e) {
			System.out.println(this.getClass() + " Can't ask for a song");
			e.printStackTrace();
		}

		BufferedInputStream bis;
		long fileSize;
		int bufferSize = 0;
		String songName, albumName, artistName;
		float sampleRate;
		int sampleSizeInBits, channels = 0;
		boolean signed, bigEndian;
		try {
			is = clientSocket.getInputStream();
			bufferSize = clientSocket.getReceiveBufferSize();

			bis = new BufferedInputStream(is);

			int count = 0;
			int headerSize;
			byte[] sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];
			byte[] headerBytes;

			/************* Reading song properties headers *************/
			// Read song name size header (int - 32 bit / 4 bytes)
			count += bis.read(sizeBytes, count, ServerConfig.SONG_NAME_SIZE_HEADER_SIZE);
			headerSize = BytesUtil.byteArrayToLeInt(sizeBytes);
			headerBytes = new byte[headerSize];
			sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

			
			// Read song name header
			count += bis.read(headerBytes, 0, headerSize);
			songName = BytesUtil.byteArrayToString(headerBytes);
			headerBytes = null;

			// Read album name size header (int - 32 bit / 4 bytes)
			count += bis.read(sizeBytes, 0, ServerConfig.ALBUM_NAME_SIZE_HEADER_SIZE);
			headerSize = BytesUtil.byteArrayToLeInt(sizeBytes);
			headerBytes = new byte[headerSize];
			sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

			// Read album name header
			count += bis.read(headerBytes, 0, headerSize);
			albumName = BytesUtil.byteArrayToString(headerBytes);
			headerBytes = null;

			// Read artist name size header (int - 32 bit / 4 bytes)
			count += bis.read(sizeBytes, 0, ServerConfig.ARTIST_NAME_SIZE_HEADER_SIZE);
			headerSize = BytesUtil.byteArrayToLeInt(sizeBytes);
			headerBytes = new byte[headerSize];
			sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

			// Read artist name header
			count += bis.read(headerBytes, 0, headerSize);
			artistName = BytesUtil.byteArrayToString(headerBytes);
			headerBytes = null;

			/*************** Reading song file properties headers *****************/
			// Read song file size in bytes
			headerBytes = new byte[ServerConfig.FILE_SIZE_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.FILE_SIZE_HEADER_SIZE);
			fileSize = BytesUtil.byteArrayToLong(headerBytes);

			/************* Reading AudioFormat properties headers *************/
			// Read sample rate header (float - 32 bit / 4 bytes )
			headerBytes = new byte[ServerConfig.SAMPLE_RATE_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.SAMPLE_RATE_HEADER_SIZE);
			sampleRate = BytesUtil.byteArrayToFloat(headerBytes);

			// Read sample size in bits header (int - 32 bit / 4 bytes)
			headerBytes = new byte[ServerConfig.SAMPLE_SIZE_IN_BITS_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.SAMPLE_SIZE_IN_BITS_HEADER_SIZE);
			sampleSizeInBits = BytesUtil.byteArrayToLeInt(headerBytes);

			// Read channels header (int - 32 bit / 4 bytes)
			headerBytes = new byte[ServerConfig.CHANNELS_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.CHANNELS_HEADER_SIZE);
			channels = BytesUtil.byteArrayToLeInt(headerBytes);

			// Read signed header (boolean - 1 byte)
			headerBytes = new byte[ServerConfig.SIGNED_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.SIGNED_HEADER_SIZE);
			signed = BytesUtil.byteArrayToBoolean(headerBytes);

            // Read BigEndian header (boolean - 1 byte)
            headerBytes = new byte[ServerConfig.BIGENDIAN_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.BIGENDIAN_HEADER_SIZE);
            bigEndian = BytesUtil.byteArrayToBoolean(headerBytes);

            headerBytes = new byte[ServerConfig.LONG_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.LONG_HEADER_SIZE);
            duration = BytesUtil.byteArrayToLong(headerBytes);

			format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

			playerPropetrties = new PlayerPropetrties(clientSocket, bis, format, bufferSize, fileSize,
					new SongDescriptors(songName, albumName, artistName, duration));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return playerPropetrties;
	}

	public void getAdioData() {
		new GetAudioDataThread(output).start();
	}

	public static int byteArrayToLeInt(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	public void disconnect() throws IOException
	{
		try {
			output.writeBytes(BytesUtil.clientMessage(ClientConfig.CsendByeString));
		} catch (IOException e) {
			System.out.println(this.getClass() + " Can't send disconnection message because socket already closed");
		}
		finally {
			if (output != null)
				output.close();
			if (clientSocket != null)
				clientSocket.close();
		}
	}
}
