package tit.communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import tit.dbUtilities.AudioUtil;
import utilities.Util;
import tit.audio.songData;

public class SendSongDetailsThreard extends Thread {

	private Socket clientSocket;
	private File songFile;

	public SendSongDetailsThreard(Socket clientSocket, File songFile) {
		this.songFile = songFile;
		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {
		try {
			sendSongData(songFile);
		} catch (IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}

	private void sendSongData(File songFile) throws IOException, UnsupportedAudioFileException {
		long fileSize = songFile.length();

		FileInputStream songFis = null;
		BufferedInputStream songBis = null;
		BufferedOutputStream out = null;
//		String songName = songFile.getName();
//		String albumName =  "album name";
//		String artistName = "artist name";
		//TODO : send song file and create songData or send songData??
		songData songData = new songData(songFile);

//		AudioFormat decodedFormat = AudioUtil.getFormat(songFile);

//		//TODO: Create songData object that have encode function
//
//		float sampleRate = decodedFormat.getSampleRate();
//		int sampleSizeInBits = decodedFormat.getSampleSizeInBits();
//		int channels = decodedFormat.getChannels();
//		boolean signed = true;
//		boolean bigEndian = decodedFormat.isBigEndian();

		songData.printSongProperties();

		try {
			// Create output stream
			out = new BufferedOutputStream(clientSocket.getOutputStream());

			byte[] songBytes = new byte[clientSocket.getSendBufferSize()];

			songFis = new FileInputStream(songFile);
			songBis = new BufferedInputStream(songFis);

			sendSongProperties(songData, out);

		}
		finally {
			//					songFis.close();
			//					songBis.close();
			out.flush();
			//					out.close();
			// TODO: clientSocket.close();
		}

	}
	private	void sendSongProperties(songData song, BufferedOutputStream out) throws IOException
	{
		sendWithSize(song.getSongName(), out);
		sendWithSize(song.getAlbumName(), out);
		sendWithSize(song.getArtistName(), out);

		/*************** Send song file properties headers *****************/
		// Send song file size in bytes
		out.write(Util.LongToByteArray(song.getFileSize()));

		/*************** Send AudioFormat properties headers *****************/
		out.write(Util.FloatToByteArray(song.getSampleRate()));
		out.write(Util.leIntToByteArray(song.getSampleSizeInBits()));
		out.write(Util.leIntToByteArray(song.getChannels()));
		out.write(Util.booleanToByteArray(song.isSigned()));
		out.write(Util.booleanToByteArray(song.isBigEndian()));
	}

	// will return a string and its size to send
	public void sendWithSize(String str, BufferedOutputStream out)
	{
		try {
			out.write(Util.getStringSizeInBytes(str));
			out.write(Util.StringToByteArray(str));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// will return a string and its size to send
	public void sendHeader(String str)
	{

	}

}
