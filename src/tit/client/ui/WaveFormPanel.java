package tit.client.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;

import tit.configuration.UIConfig;
import tit.objects.MediaPanelColors;

public class WaveFormPanel extends JPanel 
{
		private static final long serialVersionUID = -2742408715579486340L;
		
		public final int DEF_BUFFER_SAMPLE_SZ = 1024;
		public final Color LIGHT_BLUE = new Color(128, 192, 255);
	    public final Color DARK_BLUE = new Color(0, 0, 127);
		
	    MediaPanelColors mpc;
		
	    public WaveFormPanel (int width, int height) 
		{	
			setOpaque(false);
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

		public void setColors(MediaPanelColors mpc)
		{
			this. mpc = mpc;
			this.setBackground(mpc.getBackgroundColor());
		}  
        
}



