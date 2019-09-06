package tit.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import tit.audio.PlayingThread;
import tit.audio.PlayingThreadUDP;
import tit.objects.MediaPanelColors;
import utilities.Util;

public class ControlPanel extends JPanel 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2742408715579486340L;
	private PlayingThreadUDP playingThread;
	private JButton play;
	private JButton pause;
	private JButton skip;
	private JSlider progress;
	private JLabel timeLabel;
	//	MediaPanelColors mpc;
	
	public ControlPanel(String[] categories) 
	{	
		play = new JButton("play");
		play.addActionListener(new PlayActionListener());
		play.setVisible(false);
		play.updateUI();

		pause = new JButton("pause");
		pause.addActionListener(new PauseActionListener());
		pause.setVisible(true);

		skip = new JButton("skip");
		skip.addActionListener(new SkipActionListener());
		skip.setVisible(true);
		
		progress = new JSlider(); // (0, 1000, 0);
		progress.setEnabled(true);
		timeLabel = new JLabel("0");
		
//		JPanel buttonsPanel = new JPanel();
//		buttonsPanel.add(play);
//		buttonsPanel.add(pause);
//		buttonsPanel.add(skip);
//		
//		JPanel timePanel = new JPanel();
//		timePanel.add(progress);
//		timePanel.add(timeLabel);
//		
//		this.add(buttonsPanel, BorderLayout.NORTH);
//		this.add(timePanel, BorderLayout.EAST);
		
		this.add(play);
		this.add(pause);
		this.add(skip);
		this.add(progress);
		this.add(timeLabel);
		this.setVisible(true);

		repaint();
	}

	@Override
	public void paint(Graphics arg0) 
	{
		super.paint(arg0);

		if(playingThread != null) {
			int audioPosition = (int) (playingThread.getLine().getMicrosecondPosition() / 1000);
			progress.setValue(audioPosition);
			timeLabel.setText(Util.convertSecondsToStringTime(audioPosition / 1000));
		}
		repaint();
	}
	
	public void setPlayingThread(PlayingThreadUDP thread, MediaPanelColors mpc)
	{
		//		if(this.playingThread != null)
		//		{
		//			if(this.playingThread.isTerminated())
		//			{
		play.setVisible(false);
		pause.setVisible(true);
		this.playingThread = thread;
		
		long songFIleLength = this.playingThread.getFileSize(); 
		int frameSize = this.playingThread.getLine().getFormat().getFrameSize();
		float frameRate = this.playingThread.getLine().getFormat().getFrameRate();
		float lengthInSeconds = ((songFIleLength/frameSize)/frameRate);
		progress.setMaximum((int)(lengthInSeconds * 1000));
		
		setColors(mpc);
		this.repaint();
		//			}
		//			else System.out.println(getClass() + " Setting new playing thread while the current thread isnt terminated");
		//		}
		//		else System.out.println(getClass() + " Setting new playing thread while the current is null");

	}

	private void setColors(MediaPanelColors mpc)
	{
		this.setBackground(mpc.getBackgroundColor());
		play.setBackground(mpc.getBackgroundColor());
		play.setForeground(mpc.getLabelsColors());

		pause.setBackground(mpc.getBackgroundColor());
		pause.setForeground(mpc.getLabelsColors());

		skip.setBackground(mpc.getBackgroundColor());
		skip.setForeground(mpc.getLabelsColors());
		
		progress.setBackground(mpc.getBackgroundColor());
		progress.setForeground(mpc.getLabelsColors());
		
		timeLabel.setForeground(mpc.getLabelsColors());
		
		//		this.mpc = mpc;

	}

	public class PlayActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			playingThread.start();
			play.setVisible(false);
			pause.setVisible(true);
		}
	}

	public class PauseActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			try {
				playingThread.pause();
				pause.setVisible(false);
				play.setVisible(true);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public class SkipActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			System.out.println("stopping thread : " + playingThread);
			playingThread.stop();
			//TODO: adding the skip action - playing the next song

		}
	}

	public PlayingThreadUDP getPlayingThread() {
		return playingThread;

	}
	
	public void setCurrentPlayTime(float seconds)
	{
		
	}
	

}
