package tit.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.sound.sampled.AudioFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tit.audio.PlayingThread;
import tit.configuration.UIConfig;
import tit.objects.MediaPanelColors;

public class WaveFormPanel extends JPanel 
{
		private static final long serialVersionUID = -2742408715579486340L;
		private PlayingThread playingThread;
		private JLabel timeLabel;
		
		
		public final int DEF_BUFFER_SAMPLE_SZ = 1024;
		public final Color LIGHT_BLUE = new Color(128, 192, 255);
	    public final Color DARK_BLUE = new Color(0, 0, 127);
		
	    MediaPanelColors mpc;
		
	    public WaveFormPanel (int width, int height) 
		{	
			setOpaque(false);
			timeLabel = new JLabel("0");
			this.add(timeLabel);
			this.setVisible(true);

			repaint();
		}
	    
	    
	    public void drawDisplay(float[] samples, int svalid, AudioFormat format) 
	    {
            this.makePath(samples, svalid, format);
            this.repaint();
        }
		

        
        private final BufferedImage image;
        
        private final Path2D.Float[] paths = {
            new Path2D.Float(), new Path2D.Float(), new Path2D.Float()
        };
        
        private final Object pathLock = new Object();
        
        {
            Dimension pref = getPreferredSize();
            
            image = (
                GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .createCompatibleImage(
                    UIConfig.frameWidth, UIConfig.frameHeight/3, Transparency.OPAQUE
                )
            );
        }
        
        
        public void reset() {
            Graphics2D g2d = image.createGraphics();
            g2d.setBackground(Color.BLACK);
            g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
            g2d.dispose();
        }
        
        public void makePath(float[] samples, int svalid, AudioFormat audioFormat) 
        {	
//            AudioFormat audioFormat = playingThread.getLine().getFormat(); 
        	
        	if(audioFormat == null) {
                return;
            }
            
            /* shuffle */
            
            Path2D.Float current = paths[2];
            paths[2] = paths[1];
            paths[1] = paths[0];
            
            /* lots of ratios */
            
            float avg = 0f;
            float hd2 = getHeight() / 2f;
            
            final int channels = audioFormat.getChannels();
            
            /* 
             * have to do a special op for the
             * 0th samples because moveTo.
             * 
             */
            
            int i = 0;
            while(i < channels && i < svalid) {
                avg += samples[i++];
            }
            
            avg /= channels;
            
            current.reset();
            current.moveTo(0, hd2 - avg * hd2);
            
            int fvalid = svalid / channels;
            for(int ch, frame = 0; i < svalid; frame++) {
                avg = 0f;
                
                /* average the channels for each frame. */
                
                for(ch = 0; ch < channels; ch++) {
                    avg += samples[i++];
                }
                
                avg /= channels;
                
                current.lineTo(
                    (float)frame / fvalid * image.getWidth(), hd2 - avg * hd2
                );
            }
            
            paths[0] = current;
                
            Graphics2D g2d = image.createGraphics();
            
            synchronized(pathLock) {
                g2d.setBackground(this.mpc.getBackgroundColor());
                g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
                
                g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );
                g2d.setRenderingHint(
                    RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE
                );
                
                g2d.setPaint(this.mpc.getWaveColor1());
                g2d.draw(paths[2]);
                
                g2d.setPaint(this.mpc.getWaveColor2());
                g2d.draw(paths[1]);
                
                g2d.setPaint(this.mpc.getWaveColor3());
                g2d.draw(paths[0]);
            }
            
            g2d.dispose();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            synchronized(pathLock) {
                g.drawImage(image, 0, 0, null);
            }
        }
        
//        @Override
//        public Dimension getPreferredSize() {
//            return new Dimension(DEF_BUFFER_SAMPLE_SZ / 2, 128);
//        }
//        
//        @Override
//        public Dimension getMinimumSize() {
//            return getPreferredSize();
//        }
//        
//        @Override
//        public Dimension getMaximumSize() {
//            return getPreferredSize();
//        }

//		@Override
//		public void paint(Graphics arg0) 
//		{
//			super.paint(arg0);
//			
//			int audioPosition = (int) (playingThread.getLine().getMicrosecondPosition() / 1000);
//			timeLabel.setText(Util.convertSecondsToStringTime(audioPosition/1000));
//			repaint();
//		}
		
		public void setPlayingThread(PlayingThread thread, MediaPanelColors mpc)
		{
			
			
			long songFIleLength = this.playingThread.getFileSize(); 
			int frameSize = this.playingThread.getLine().getFormat().getFrameSize();
			float frameRate = this.playingThread.getLine().getFormat().getFrameRate();
			float lengthInSeconds = ((songFIleLength/frameSize)/frameRate);
//			progress.setMaximum((int)(lengthInSeconds * 1000));
			
			setColors(mpc);
			this.repaint();

		}

		public void setColors(MediaPanelColors mpc)
		{
			this. mpc = mpc;
			this.setBackground(mpc.getBackgroundColor());			
			timeLabel.setForeground(mpc.getLabelsColors());
			
			//		this.mpc = mpc;

		}

		public class PlayActionListener implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				playingThread.start();
			}

		}

		public class PauseActionListener implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				playingThread.pause();
			}

		}

		public class SkipActionListener implements ActionListener
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				System.out.println("stopping thread : " + playingThread);
				playingThread.stop();
			}

		}

		public PlayingThread getPlayingThread() {
			return playingThread;

		}
		
		public void setCurrentPlayTime(float seconds)
		{
			
		}
		


        
//        private final PlayerRef playerRef;
        
        
//        public class PlaybackLoop extends SwingWorker<Void, Void>
//        {
//        
//        @Override
//        public Void doInBackground() {
//            try {
//                AudioInputStream in = null;
//                SourceDataLine out = null;
//                
//                try {
//                    try {
//                        final AudioFormat audioFormat = playingThread.getLine().getFormat();
//                        
//                      in = AudioSystem.getAudioInputStream(playerRef.getFile());
//                        out = AudioSystem.getSourceDataLine(audioFormat);
//                        
//                        final int normalBytes = normalBytesFromBits(audioFormat.getSampleSizeInBits());
//                        
//                        float[] samples = new float[DEF_BUFFER_SAMPLE_SZ * audioFormat.getChannels()];
//                        long[] transfer = new long[samples.length];
//                        byte[] bytes = new byte[samples.length * normalBytes];
//                        
//                        out.open(audioFormat, bytes.length);
//                        out.start();
//                        
//                        /*
//                         * feed the output some zero samples
//                         * helps prevent the 'stutter' issue.
//                         * 
//                         */
//                        
//                        for(int feed = 0; feed < 6; feed++) {
//                            out.write(bytes, 0, bytes.length);
//                        }
//                        
//                        int bread;
//                        
//                        play_loop: do {
//                            while(playerRef.getStat() == PlayStat.PLAYING) {
//                                
//                                if((bread = in.read(bytes)) == -1) {
//                                    
//                                    break play_loop; // eof
//                                }
//                                
//                                samples = unpack(bytes, transfer, samples, bread, audioFormat);
//                                samples = window(samples, bread / normalBytes, audioFormat);
//                                
//                                playerRef.drawDisplay(samples, bread / normalBytes);
//                                
//                                out.write(bytes, 0, bread);
//                            }
//                            
//                            if(playerRef.getStat() == PlayStat.PAUSED) {
//                                out.flush();
//                                try {
//                                    synchronized(playerRef.getLock()) {
//                                        playerRef.getLock().wait(1000L);
//                                    }
//                                } catch(InterruptedException ie) {}
//                                continue;
//                            } else {
//                                break;
//                            }
//                        } while(true);
//                        
//                    } catch(UnsupportedAudioFileException uafe) {
//                        showError(uafe);
//                    } catch(LineUnavailableException lue) {
//                        showError(lue);
//                    }
//                } finally {
//                    if(in != null) {
//                        in.close();
//                    }
//                    if(out != null) {
//                        out.flush();
//                        out.close();
//                    }
//                }
//            } catch(IOException ioe) {
//                showError(ioe);
//            }
//            
//            return (Void)null;
//        }
//        
//        @Override
//        public void done() {
//            playerRef.playbackEnded();
//            
//            try {
//                get();
//            } catch(InterruptedException io) {
//            } catch(CancellationException ce) {
//            } catch(ExecutionException ee) {
//                showError(ee.getCause());
//            }
//        }
//    
//		
//	}
//        
//        public float[] unpack(byte[] bytes, long[] transfer, float[] samples, int bvalid, AudioFormat fmt)
//        {
//                if(fmt.getEncoding() != AudioFormat.Encoding.PCM_SIGNED
//                        && fmt.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED) {
//                    
//                    return samples;
//                }
//                
//                final int bitsPerSample = fmt.getSampleSizeInBits();
//                final int bytesPerSample = bitsPerSample / 8;
//                final int normalBytes = normalBytesFromBits(bitsPerSample);
//                
//                /*
//                 * not the most DRY way to do this but it's a bit more efficient.
//                 * otherwise there would either have to be 4 separate methods for
//                 * each combination of endianness/signedness or do it all in one
//                 * loop and check the format for each sample.
//                 * 
//                 * a helper array (transfer) allows the logic to be split up
//                 * but without being too repetetive.
//                 * 
//                 * here there are two loops converting bytes to raw long samples.
//                 * integral primitives in Java get sign extended when they are
//                 * promoted to a larger type so the & 0xffL mask keeps them intact.
//                 * 
//                 */
//                
//                if(fmt.isBigEndian()) {
//                    for(int i = 0, k = 0, b; i < bvalid; i += normalBytes, k++) {
//                        transfer[k] = 0L;
//                        
//                        int least = i + normalBytes - 1;
//                        for(b = 0; b < normalBytes; b++) {
//                            transfer[k] |= (bytes[least - b] & 0xffL) << (8 * b);
//                        }
//                    }
//                } else {
//                    for(int i = 0, k = 0, b; i < bvalid; i += normalBytes, k++) {
//                        transfer[k] = 0L;
//                        
//                        for(b = 0; b < normalBytes; b++) {
//                            transfer[k] |= (bytes[i + b] & 0xffL) << (8 * b);
//                        }
//                    }
//                }
//                
//                final long fullScale = (long)Math.pow(2.0, bitsPerSample - 1);
//                
//                /*
//                 * the OR is not quite enough to convert,
//                 * the signage needs to be corrected.
//                 * 
//                 */
//                
//                if(fmt.getEncoding() == AudioFormat.Encoding.PCM_SIGNED) {
//                    
//                    /*
//                     * if the samples were signed, they must be
//                     * extended to the 64-bit long.
//                     * 
//                     * the arithmetic right shift in Java  will fill
//                     * the left bits with 1's if the MSB is set.
//                     * 
//                     * so sign extend by first shifting left so that
//                     * if the sample is supposed to be negative,
//                     * it will shift the sign bit in to the 64-bit MSB
//                     * then shift back and fill with 1's.
//                     * 
//                     * as an example, imagining these were 4-bit samples originally
//                     * and the destination is 8-bit, if we have a hypothetical
//                     * sample -5 that ought to be negative, the left shift looks
//                     * like this:
//                     * 
//                     *     00001011
//                     *  <<  (8 - 4)
//                     *  ===========
//                     *     10110000
//                     * 
//                     * (except the destination is 64-bit and the original
//                     * bit depth from the file could be anything.)
//                     * 
//                     * and the right shift now fills with 1's:
//                     * 
//                     *     10110000
//                     *  >>  (8 - 4)
//                     *  ===========
//                     *     11111011
//                     * 
//                     */
//                    
//                    final long signShift = 64L - bitsPerSample;
//                    
//                    for(int i = 0; i < transfer.length; i++) {
//                        transfer[i] = (
//                            (transfer[i] << signShift) >> signShift
//                        );
//                    }
//                } else {
//                    
//                    /*
//                     * unsigned samples are easier since they
//                     * will be read correctly in to the long.
//                     * 
//                     * so just sign them:
//                     * subtract 2^(bits - 1) so the center is 0.
//                     * 
//                     */
//                    
//                    for(int i = 0; i < transfer.length; i++) {
//                        transfer[i] -= fullScale;
//                    }
//                }
//                
//                /* finally normalize to range of -1.0f to 1.0f */
//                
//                for(int i = 0; i < transfer.length; i++) {
//                    samples[i] = (float)transfer[i] / (float)fullScale;
//                }
//                
//                return samples;
//            }
//            
//            public float[] window(float[] samples, int svalid, AudioFormat fmt) 
//            {
//                /*
//                 * most basic window function
//                 * multiply the window against a sine curve, tapers ends
//                 * 
//                 * nested loops here show a paradigm for processing multi-channel formats
//                 * the interleaved samples can be processed "in place"
//                 * inner loop processes individual channels using an offset
//                 * 
//                 */
//                
//                int channels = fmt.getChannels();
//                int slen = svalid / channels;
//                
//                for(int ch = 0, k, i; ch < channels; ch++) {
//                    for(i = ch, k = 0; i < svalid; i += channels) {
//                        samples[i] *= Math.sin(Math.PI * k++ / (slen - 1));
//                    }
//                }
//                
//                return samples;
//            }
//            
//            public int normalBytesFromBits(int bitsPerSample) 
//            {
//                
//                /*
//                 * some formats allow for bit depths in non-multiples of 8.
//                 * they will, however, typically pad so the samples are stored
//                 * that way. AIFF is one of these formats.
//                 * 
//                 * so the expression:
//                 * 
//                 *  bitsPerSample + 7 >> 3
//                 * 
//                 * computes a division of 8 rounding up (for positive numbers).
//                 * 
//                 * this is basically equivalent to:
//                 * 
//                 *  (int)Math.ceil(bitsPerSample / 8.0)
//                 * 
//                 */
//                
//                return bitsPerSample + 7 >> 3;
//            }
//        
        
}



