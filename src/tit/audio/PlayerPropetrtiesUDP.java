package tit.audio;

import javax.sound.sampled.AudioFormat;
import java.io.BufferedInputStream;
import java.net.DatagramSocket;

public class PlayerPropetrtiesUDP
{
	private DatagramSocket socket;
	private BufferedInputStream bis;
	private AudioFormat format;
	private int bufferSize;
	private long fileSize;
	private SongDescriptors songDescriptors;
	
	public PlayerPropetrtiesUDP(DatagramSocket socket, BufferedInputStream bis, AudioFormat format, int bufferSize, long fileSize, SongDescriptors songDescriptors)
	{
		this.socket = socket;
		this.bis = bis;
		this.format = format;
		this.bufferSize = bufferSize;
		this.songDescriptors = songDescriptors;
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

	public SongDescriptors getSongDescriptors() {
		return songDescriptors;
	}

	public void setSongDescriptors(SongDescriptors songDescriptors) {
		this.songDescriptors = songDescriptors;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	

}
