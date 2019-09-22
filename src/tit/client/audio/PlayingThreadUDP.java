package tit.client.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.*;

import tit.audio.SongDescriptors;
import tit.client.ClientConfig;
import tit.server.ServerConfig;
import tit.configuration.UIConfig;
import tit.server.WriteToLineThread;
import tit.client.ui.WaveFormPanel;

public class PlayingThreadUDP implements Runnable {
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

	public PlayingThreadUDP(PlayerPropetrties p, LineListener l) throws LineUnavailableException, SocketException {
		socket = new DatagramSocket(ClientConfig.UdpPort);
		this.songDescriptors = p.getSongDescriptors();
		this.bis = p.getBis();
		this.setFileSize(p.getFileSize());
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, p.getFormat());
		this.line = (SourceDataLine) AudioSystem.getLine(info);
		System.out.println("info " + info.toString());
		this.line.addLineListener(l);
		// this.line.open(p.getFormat());
		this.format = p.getFormat();
		isPlaying = true;
		isTerminated = false;
		isDone = false;

		//		din = new AudioInputStream(bis, format, -1L);

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
			// TODO Auto-generated catch block
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

//		if(!isTerminated) {
//			System.out.println("starting to drain");
//			line.drain();
//			System.out.println("finish drain");
//		}
		line.stop();
		line.close();
		System.out.println("Line closed");
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

	public static int normalBytesFromBits(int bitsPerSample) {

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

		return bitsPerSample + 7 >> 3;
	}

	public float[] unpack(byte[] bytes, long[] transfer, float[] samples, int bvalid, AudioFormat fmt) {
		if (fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED
				&& fmt.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {

			return samples;
		}

		final int bitsPerSample = fmt.getSampleSizeInBits();
		final int bytesPerSample = bitsPerSample / 8;
		final int normalBytes = normalBytesFromBits(bitsPerSample);

		/*
		 * not the most DRY way to do this but it's a bit more efficient. otherwise
		 * there would either have to be 4 separate methods for each combination of
		 * endianness/signedness or do it all in one loop and check the format for each
		 * sample.
		 * 
		 * a helper array (transfer) allows the logic to be split up but without being
		 * too repetetive.
		 * 
		 * here there are two loops converting bytes to raw long samples. integral
		 * primitives in Java get sign extended when they are promoted to a larger type
		 * so the & 0xffL mask keeps them intact.
		 * 
		 */

		if (fmt.isBigEndian()) {
			for (int i = 0, k = 0, b; i < bvalid; i += normalBytes, k++) {
				transfer[k] = 0L;

				int least = i + normalBytes - 1;
				for (b = 0; b < normalBytes; b++) {
					transfer[k] |= (bytes[least - b] & 0xffL) << (8 * b);
				}
			}
		} else {
			for (int i = 0, k = 0, b; i < bvalid; i += normalBytes, k++) {
				transfer[k] = 0L;

				for (b = 0; b < normalBytes; b++) {
					transfer[k] |= (bytes[i + b] & 0xffL) << (8 * b);
				}
			}
		}

		final long fullScale = (long) Math.pow(2.0, bitsPerSample - 1);

		/*
		 * the OR is not quite enough to convert, the signage needs to be corrected.
		 * 
		 */

		if (fmt.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {

			/*
			 * if the samples were signed, they must be extended to the 64-bit long.
			 * 
			 * the arithmetic right shift in Java will fill the left bits with 1's if the
			 * MSB is set.
			 * 
			 * so sign extend by first shifting left so that if the sample is supposed to be
			 * negative, it will shift the sign bit in to the 64-bit MSB then shift back and
			 * fill with 1's.
			 * 
			 * as an example, imagining these were 4-bit samples originally and the
			 * destination is 8-bit, if we have a hypothetical sample -5 that ought to be
			 * negative, the left shift looks like this:
			 * 
			 * 00001011 << (8 - 4) =========== 10110000
			 * 
			 * (except the destination is 64-bit and the original bit depth from the file
			 * could be anything.)
			 * 
			 * and the right shift now fills with 1's:
			 * 
			 * 10110000 >> (8 - 4) =========== 11111011
			 * 
			 */

			final long signShift = 64L - bitsPerSample;

			for (int i = 0; i < transfer.length; i++) {
				transfer[i] = ((transfer[i] << signShift) >> signShift);
			}
		} else {

			/*
			 * unsigned samples are easier since they will be read correctly in to the long.
			 * 
			 * so just sign them: subtract 2^(bits - 1) so the center is 0.
			 * 
			 */

			for (int i = 0; i < transfer.length; i++) {
				transfer[i] -= fullScale;
			}
		}

		/* finally normalize to range of -1.0f to 1.0f */

		for (int i = 0; i < transfer.length; i++) {
			samples[i] = (float) transfer[i] / (float) fullScale;
		}

		return samples;
	}

	public float[] window(float[] samples, int svalid, AudioFormat fmt) {
		/*
		 * most basic window function multiply the window against a sine curve, tapers
		 * ends
		 * 
		 * nested loops here show a paradigm for processing multi-channel formats the
		 * interleaved samples can be processed "in place" inner loop processes
		 * individual channels using an offset
		 * 
		 */

		int channels = fmt.getChannels();
		int slen = svalid / channels;

		for (int ch = 0, k, i; ch < channels; ch++) {
			for (i = ch, k = 0; i < svalid; i += channels) {
				samples[i] *= Math.sin(Math.PI * k++ / (slen - 1));
			}
		}

		return samples;
	}

}