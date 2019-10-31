package radio.client.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.*;

import radio.client.ClientConfig;
import radio.client.ui.WaveFormPanel;
import radio.core.audio.SongDescriptors;
import radio.client.ui.UIConfig;
import radio.server.ServerConfig;
import radio.server.WriteToLineThread;

public class UDP_PORT implements Runnable {
	private DatagramSocket socket;

	private SongDescriptors songDescriptors;
	private BufferedInputStream bis;
	private long fileSize;
	private SourceDataLine line;
	private AudioFormat format;
	private boolean isPlaying;
	private boolean isTerminated;
	private boolean isDone;

	public WaveFormPanel waveForm;
	
	private ExecutorService exec = Executors.newFixedThreadPool(1);

	public UDP_PORT(PlayerPropetrties p, LineListener l) throws LineUnavailableException, SocketException {
		socket = new DatagramSocket(ClientConfig.UdpPort);
		this.songDescriptors = p.getSongDescriptors();
		this.bis = p.getBis();
		this.setFileSize(p.getFileSize());
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, p.getFormat());
		this.line = (SourceDataLine) AudioSystem.getLine(info);
		this.line.addLineListener(l);
		this.format = p.getFormat();
		isPlaying = true;
		isTerminated = false;
		isDone = false;
		waveForm = new WaveFormPanel(UIConfig.frameWidth, 128);

	}

	@Override
	public void run() {

		int count = 0;
		AudioFormat audioFormat = line.getFormat();

		final int DEF_BUFFER_SAMPLE_SZ = 1024;

		final int normalBytes = normalBytesFromBits(audioFormat.getSampleSizeInBits());

		float[] samples = new float[DEF_BUFFER_SAMPLE_SZ * audioFormat.getChannels()];
		long[] transfer = new long[samples.length];

		try {
			line.open(format);
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}

		line.start();

		try {
			socket.setSoTimeout(3000);
			
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		// Each time a packet is read (by recieve) its added to a list of packets that needed to be written to line by order
		do {		
			byte[] buf = new byte[ServerConfig.DATAGRAM_PACKET_SIZE];
			DatagramPacket packet = new DatagramPacket(buf, ServerConfig.DATAGRAM_PACKET_SIZE);
			try {
				socket.receive(packet);
				count = packet.getLength();

				Runnable runnable = new WriteToLineThread(line, audioFormat, buf, count, transfer, waveForm,
						samples, normalBytes);
				exec.execute(runnable);


			} catch (IOException e) {
				System.out.println("could not get any more data, assuming sending done");
				break;
			}


		} while (count >0 && !isTerminated);

		exec.shutdown();
		while (!isTerminated) {
			try {
				boolean isFinished = exec.awaitTermination(100, TimeUnit.MILLISECONDS);
				if(isFinished) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
		}
		
		exec.shutdownNow();
		
		socket.close();
		line.stop();
		line.close();
		try {
			bis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		line = null;
		isDone = true;
		
	}

	public void start() {
		if (!isPlaying && !isTerminated) {
			isPlaying = true;
			line.start();
		} else
			System.out.println(getClass() + " trying to start but song already playing.\n" + "isPlaying = " + isPlaying
					+ "\n" + "isTerminated = " + isTerminated);
	}

	public void pause() throws InterruptedException {
		if (isPlaying && !isTerminated) {
			isPlaying = false;
			line.stop();
		} else
			System.out.println(getClass() + " trying to pause but song already paused or stopped.\n" + "isPlaying = "
					+ isPlaying + "\n" + "isTerminated = " + isTerminated);
	}

	public void stop() {
		isPlaying = false;
		isTerminated = true;
	}

	public DatagramSocket getSocket() {
		return this.socket;
	}

	public SongDescriptors getSongDescriptors() {
		return songDescriptors;
	}

	public void setSongDescriptors(SongDescriptors songDescriptors) {
		this.songDescriptors = songDescriptors;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void addLineListener(LineListener l) {
		this.line.addLineListener(l);
	}

	public SourceDataLine getLine() {
		return line;
	}

	public boolean isDone() {
		return isDone;
	}

	public void setDone(boolean isDone) {
		this.isDone = isDone;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public BufferedInputStream getBis() {
		return this.bis;
	}

	
	/*
	 * some formats allow for bit depths in non-multiples of 8. they will, however,
	 * typically pad so the samples are stored that way. AIFF is one of these
	 * formats.
	 * 
	 * so the expression:
	 * 
	 * bitsPerSample + 7 >> 3
	 * 
	 * computes a division of 8 rounding up (for positive numbers).
	 * 
	 * this is basically equivalent to:
	 * 
	 * (int)Math.ceil(bitsPerSample / 8.0)
	 * 
	 */
	private static int normalBytesFromBits(int bitsPerSample) {
		return bitsPerSample + 7 >> 3;
	}

}