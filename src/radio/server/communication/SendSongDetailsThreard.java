package radio.server.communication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.sound.sampled.UnsupportedAudioFileException;

import radio.core.audio.songData;
import radio.core.utilities.Util;

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
		BufferedOutputStream out = null;
		songData songData = new songData(songFile);
		
		try {
			// Create output stream
			out = new BufferedOutputStream(clientSocket.getOutputStream());
			sendSongProperties(songData, out);

		} finally {
			out.flush();
		}

	}

	private void sendSongProperties(songData song, BufferedOutputStream out) throws IOException {
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
		out.write(Util.longToByteArray(song.getDurationInMili()));
	}

	// will return a string and its size to send
	public void sendWithSize(String str, BufferedOutputStream out) {
		try {
			out.write(Util.getStringSizeInBytes(str));
			out.write(Util.StringToByteArray(str));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
