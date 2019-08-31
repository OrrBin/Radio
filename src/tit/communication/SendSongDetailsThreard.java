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
		String songName = "song name";
		String albumName = "album name";
		String artistName = "artist name";

		AudioFormat decodedFormat = AudioUtil.getFormat(songFile);

		//TODO: Create songData object that have encode function

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

		}
		finally {
			//					songFis.close();
			//					songBis.close();
			out.flush();
			//					out.close();
			// TODO: clientSocket.close();
		}

	}
}
