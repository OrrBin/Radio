package tit.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.CommunicationException;
import javax.security.auth.login.Configuration;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.xml.transform.Source;

import com.sun.media.jfxmedia.track.Track.Encoding;

import tit.audio.PlayerPropetrties;
import tit.audio.PlayingThread;
import tit.audio.Song;
import tit.audio.SongStream;
import tit.configuration.ClientConfig;
import tit.configuration.DataManagmenetConfig;
import tit.configuration.ServerConfig;
import tit.dataManagment.DataManagmentUtilities;
import tit.ui.StreamingMainPage.TitLineListener;
import utilities.Util;

/**
 * 
 *  
 *
 */
public class UDPStreamingClient {

	Socket clientSocket;
	DataOutputStream output;
	DataInputStream input;

	DataOutputStream outToServer;
	InputStreamReader inFromServer;
	BufferedReader stringInFromServer;

	private File musicDirectory;
	private File imagesDirectory;

	public UDPStreamingClient(String server, int port, File baseDirectory)
			throws UnknownHostException, IOException, CommunicationException {
		this.musicDirectory = new File(baseDirectory.getPath() + ClientConfig.DefaultMusicFolder);
		this.imagesDirectory = new File(baseDirectory.getPath() + ClientConfig.DefaultImagesFolder);
	}

	public PlayerPropetrties getSongDetailsAndData(String category) throws IOException {
		PlayerPropetrties playerPropetrties = null;

		InputStream is = null;
		FileOutputStream songFos = null;
		FileOutputStream imageFos = null;
		BufferedOutputStream songBos = null;
		BufferedOutputStream imageBos = null;

		ByteArrayOutputStream baos = null;

		SourceDataLine line;
		AudioFormat format;

		clientSocket = new Socket(ServerConfig.serverAddr, ServerConfig.serverPort);

		output = new DataOutputStream(clientSocket.getOutputStream());
		// Ask for a new Song
		try {
			output.writeBytes(ClientConfig.CsendMeNewSongString + ClientConfig.messageDivider + category
					+ System.lineSeparator());
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
			
			System.out.println("doing shit");
			
			is = clientSocket.getInputStream();
			bufferSize = clientSocket.getReceiveBufferSize();
			byte[] bytes = new byte[bufferSize];

			bis = new BufferedInputStream(is);

			int count = 0;
			int headerSize;
			byte[] sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];
			byte[] headerBytes;

			/************* Reading song properties headers *************/
			// Read song name size header (int - 32 bit / 4 bytes)
			count += bis.read(sizeBytes, count, ServerConfig.SONG_NAME_SIZE_HEADER_SIZE);
			headerSize = Util.byteArrayToLeInt(sizeBytes);
			headerBytes = new byte[headerSize];
			sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

			System.out.println("read first header");
			
			// Read song name header
			count += bis.read(headerBytes, 0, headerSize);
			songName = Util.byteArrayToString(headerBytes);
			headerBytes = null;

			// Read album name size header (int - 32 bit / 4 bytes)
			count += bis.read(sizeBytes, 0, ServerConfig.ALBUM_NAME_SIZE_HEADER_SIZE);
			headerSize = Util.byteArrayToLeInt(sizeBytes);
			headerBytes = new byte[headerSize];
			sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

			// Read album name header
			count += bis.read(headerBytes, 0, headerSize);
			albumName = Util.byteArrayToString(headerBytes);
			headerBytes = null;

			// Read artist name size header (int - 32 bit / 4 bytes)
			count += bis.read(sizeBytes, 0, ServerConfig.ARTIST_NAME_SIZE_HEADER_SIZE);
			headerSize = Util.byteArrayToLeInt(sizeBytes);
			headerBytes = new byte[headerSize];
			sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

			// Read artist name header
			count += bis.read(headerBytes, 0, headerSize);
			artistName = Util.byteArrayToString(headerBytes);
			headerBytes = null;

			/*************** Reading song file properties headers *****************/
			// Read song file size in bytes
			headerBytes = new byte[ServerConfig.FILE_SIZE_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.FILE_SIZE_HEADER_SIZE);
			fileSize = Util.byteArrayToLong(headerBytes);

			/************* Reading AudioFormat properties headers *************/
			// Read sample rate header (float - 32 bit / 4 bytes )
			headerBytes = new byte[ServerConfig.SAMPLE_RATE_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.SAMPLE_RATE_HEADER_SIZE);
			sampleRate = Util.byteArrayToFloat(headerBytes);

			// Read sample size in bits header (int - 32 bit / 4 bytes)
			headerBytes = new byte[ServerConfig.SAMPLE_SIZE_IN_BITS_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.SAMPLE_SIZE_IN_BITS_HEADER_SIZE);
			sampleSizeInBits = Util.byteArrayToLeInt(headerBytes);

			// Read channels header (int - 32 bit / 4 bytes)
			headerBytes = new byte[ServerConfig.CHANNELS_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.CHANNELS_HEADER_SIZE);
			channels = Util.byteArrayToLeInt(headerBytes);

			// Read signed header (boolean - 1 byte)
			headerBytes = new byte[ServerConfig.SIGNED_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.SIGNED_HEADER_SIZE);
			signed = Util.byteArrayToBoolean(headerBytes);

			// Read BigEndian header (boolean - 1 byte)
			headerBytes = new byte[ServerConfig.BIGENDIAN_HEADER_SIZE];
			count += bis.read(headerBytes, 0, ServerConfig.BIGENDIAN_HEADER_SIZE);
			bigEndian = Util.byteArrayToBoolean(headerBytes);

			format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

			System.out.println("song : " + songName);
			System.out.println("album : " + albumName);
			System.out.println("artist : " + artistName);
			System.out.println("sample rate : " + sampleRate);
			System.out.println("sample size in bits : " + sampleSizeInBits);
			System.out.println("channels : " + channels);
			System.out.println("signed : " + signed);
			System.out.println("bigEndian : " + bigEndian);

			// TODO : add genere and image
			playerPropetrties = new PlayerPropetrties(clientSocket, bis, format, bufferSize, fileSize,
					new SongStream(songName, albumName, artistName, category));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("getting audio data");
//		getAudioData(clientSocket);
		new GetAudioDataThread(output).start();

		return playerPropetrties;

	}
	
	public void getAudioData(Socket clientSocket) throws IOException {

//		clientSocket = new Socket(ServerConfig.serverAddr, ServerConfig.serverPort);

		output = new DataOutputStream(clientSocket.getOutputStream());
		// Ask for a new Song
		try {
			output.writeBytes(ClientConfig.CsendMeAudioData + ClientConfig.messageDivider + System.lineSeparator());
		} catch (IOException e) {
			System.out.println(this.getClass() + " Can't ask for a song");
			e.printStackTrace();
		}
	}

	public static int byteArrayToLeInt(byte[] b) {
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	public static void main(String argv[]) throws Exception {

		TCPStreamingClient streamingClient = new TCPStreamingClient(ServerConfig.serverAddr, ServerConfig.serverPort,
				new File("D:\\RadioTit-client"));
	}

}
