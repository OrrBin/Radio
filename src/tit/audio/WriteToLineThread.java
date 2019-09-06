package tit.audio;

import java.net.DatagramPacket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

import tit.ui.WaveFormPanel;

public class WriteToLineThread implements Runnable {

	private SourceDataLine line;
	AudioFormat audioFormat;
	private byte[] data;
	private int count;
	private long[] transfer;
	private WaveFormPanel waveForm;
	private float[] samples;
	private int normalBytes ;	
	
	public WriteToLineThread(SourceDataLine line, AudioFormat audioFormat, byte[] data, int count, long[] transfer,
			WaveFormPanel waveForm, float[] samples, int normalBytes) {
		super();
		this.line = line;
		this.audioFormat = audioFormat;
		this.data = data;
		this.count = count;
		this.transfer = transfer;
		this.waveForm = waveForm;
		this.samples = samples;
		this.normalBytes = normalBytes;	
	}

	@Override
	public void run() {
		
		while(line.available() < count) {
		// Just for making line.write be blocking
		}
		line.write(data, 0, count);

		samples = unpack(data, transfer, samples, count, audioFormat);
		samples = window(samples, count / normalBytes, audioFormat);

		waveForm.drawDisplay(samples, count / normalBytes, line.getFormat());

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
}
