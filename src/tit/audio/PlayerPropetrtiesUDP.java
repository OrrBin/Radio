package tit.audio;

import javax.sound.sampled.AudioFormat;
import java.io.BufferedInputStream;
import java.net.DatagramSocket;
import java.net.Socket;

public class PlayerPropetrtiesUDP
{
	private DatagramSocket socket;
	private BufferedInputStream bis;
	private AudioFormat format;
	private int bufferSize;
	private long fileSize;
	private SongStream songStream;
	
	public PlayerPropetrtiesUDP(DatagramSocket socket, BufferedInputStream bis, AudioFormat format, int bufferSize, long fileSize, SongStream songStream)
	{
		this.socket = socket;
		this.bis = bis;
		this.format = format;
		this.bufferSize = bufferSize;
		this.songStream = songStream;
		this.setFileSize(fileSize);
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(DatagramSocket socket) {
		this.socket = socket;
	}

	public BufferedInputStream getBis() {
		return bis;
	}

	public void setBis(BufferedInputStream bis) {
		this.bis = bis;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public void setFormat(AudioFormat format) {
		this.format = format;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public SongStream getSongStream() {
		return songStream;
	}

	public void setSongStream(SongStream songStream) {
		this.songStream = songStream;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	

}
