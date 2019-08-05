package tit.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import tit.audio.Song;
import tit.configuration.UIConfig;
import tit.imageProcessing.FontProcessor;
import tit.imageProcessing.ImageProcessor;
import tit.objects.MediaPanelColors;
import tit.objects.SongPanelColors;

/**
 * 
 *  
 *
 */
public class SongPanel extends JPanel 
{
	private Song song;
	private SongPanelColors songPanelColors;
	private JLabel songNameLabel;
	private JLabel albumNameLabel;
	private JLabel artistNameLabel;
	private BufferedImage backgroundImage;
	
	private String[] categories;
	
	
	JComboBox<String> categoriesBox;
	GridBagConstraints gc;

	public SongPanel(String[] categories)
	{
		super();

		this.categories = categories;
		categoriesBox = new JComboBox<>(this.categories);
		categoriesBox.setSelectedIndex(0);
		categoriesBox.setFont(new Font("Univers", Font.PLAIN, 20));
		categoriesBox.setVisible(true);

//		this.setLayout(new GridBagLayout());
		gc = new GridBagConstraints();
		gc.gridx = 0;
        gc.gridy = 1;
        gc.insets = new Insets(2, 0, 0, 2);
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.weightx = 1;
        gc.weighty = 1;


		
		this.setLayout(new BorderLayout(0, 0));

		songNameLabel = new JLabel();
		//		this.add(songNameLabel, BorderLayout.NORTH);
		songNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		songNameLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		songNameLabel.setFont(UIConfig.songNameFont);


		albumNameLabel = new JLabel();
		albumNameLabel.setVerticalAlignment(SwingConstants.CENTER);
		//		this.add(albumNameLabel, BorderLayout.WEST);
		albumNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
		albumNameLabel.setFont(UIConfig.albumNameFont);

		artistNameLabel = new JLabel();
		artistNameLabel.setVerticalAlignment(SwingConstants.CENTER);
		//		this.add(artistNameLabel, BorderLayout.EAST);
		artistNameLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		artistNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		artistNameLabel.setFont(UIConfig.artistNameFont);

	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);

		Rectangle panelSize = this.getBounds();

		//Drawing the background image
		g.drawImage(backgroundImage, 0, 0, (int)panelSize.getWidth(), (int)panelSize.getHeight(), null);

		// get metrics from the graphics
		FontMetrics metrics = g.getFontMetrics(UIConfig.songNameFont);
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int nameWidth = metrics.stringWidth(song.getSongName());
		int albumWidth = metrics.stringWidth(song.getAlbumName());
		int artistWidth = metrics.stringWidth(song.getArtistName());
		// calculate the size of a box to hold the
		// text with some padding.
		Dimension nameSize = new Dimension(nameWidth+2, hgt+2);
		Dimension albumSize = new Dimension(albumWidth+2, hgt+2);
		Dimension artistSize = new Dimension(artistWidth+2, hgt+2);

		//Song name position
		int nameX = (int) ((panelSize.getWidth() - nameSize.getWidth())/2);
		int nameY = (int) (panelSize.getHeight() / 4);

		//Album name position
		int albumX = (int) ((panelSize.getWidth() - albumSize.getWidth())/2);
		int albumY = (int) (2 * panelSize.getHeight() / 4);

		//Album name position
		int artistX = (int) ((panelSize.getWidth() - artistSize.getWidth())/2);
		int artistY = (int) (3 * panelSize.getHeight() / 4);

		//Draw song name
		FontProcessor.outLine(g, song.getSongName(), songPanelColors.getBackgroundColor(), songPanelColors.getSongColor(), nameX, nameY, UIConfig.songNameFont);
		//Draw album Name
		FontProcessor.outLine(g, song.getAlbumName(), songPanelColors.getBackgroundColor(), songPanelColors.getAlbumColor(), albumX, albumY, UIConfig.albumNameFont);
		//Draw artist name
		FontProcessor.outLine(g, song.getArtistName(), songPanelColors.getBackgroundColor(), songPanelColors.getArtistColor(), artistX, artistY, UIConfig.artistNameFont);
		
		categoriesBox.setBackground(songPanelColors.getBackgroundColor());
		categoriesBox.setForeground(songPanelColors.getSongColor());
		
		this.add(categoriesBox, BorderLayout.PAGE_END);



		//Add labels
		//		this.setVisible(true);
		//		setVisible(true);

		//		this.repaint();
		//		this.setVisible(true);
		//		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{songNameLabel, albumNameLabel, artistNameLabel}));
	}

	public Song getSong() {
		return song;
	}
	
	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}


	public MediaPanelColors setSong(Song song) 
	{
		this.song = song;
		//Update UI to the new song
		MediaPanelColors mpc = updateSongData();

		return mpc;
	}

	public SongPanelColors getLabelsColors() {
		return songPanelColors;
	}

	public void setLabelsColors(SongPanelColors labelsColors) {
		this.songPanelColors = labelsColors;
	}

	/**
	 * Used after changing song / update song data
	 */
	private MediaPanelColors updateSongData()
	{
		SongPanelColors spc = null;
		MediaPanelColors mpc = null;
		try {
			spc = ImageProcessor.getSongPanelColors(song.getSongImage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mpc = ImageProcessor.getMediaPanelColors(song.getSongImage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		songNameLabel.setText(song.getSongName());
//		songNameLabel.setForeground(spc.getSongColor());
//
//		albumNameLabel.setText(song.getAlbumName());
//		albumNameLabel.setForeground(spc.getAlbumColor());
//
//		artistNameLabel.setText(song.getArtistName());
//		artistNameLabel.setForeground(spc.getArtistColor());

		songPanelColors = spc;

		//Processing the image for the background
		try {
			backgroundImage = ImageProcessor.processImage(song.getSongImage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.repaint();
		
		return mpc;
		//		this.setVisible(false);
		//		this.setVisible(true);

	}
}
