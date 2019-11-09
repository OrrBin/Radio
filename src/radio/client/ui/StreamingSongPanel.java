package radio.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import radio.client.ui.objects.MediaPanelColors;
import radio.client.ui.objects.SongPanelColors;
import radio.core.audio.SongDescriptors;

/**
 * 
 *  
 *
 */
public class StreamingSongPanel extends JPanel 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6716699269429005928L;

	private static Color textColor = new Color(249,211,66);
	private static Color backgroundColor = new Color(41, 40, 38);

	public static SongPanelColors spc = new SongPanelColors(textColor, textColor, textColor, backgroundColor);
	public static MediaPanelColors mpc = new MediaPanelColors(backgroundColor, textColor, textColor);

	private SongDescriptors songDesc;
	private SongPanelColors songPanelColors = spc;
	private JLabel songNameLabel;
	private JLabel albumNameLabel;
	private JLabel artistNameLabel;

	private String[] categories;


	JComboBox<String> categoriesBox;
	GridBagConstraints gc;

	public StreamingSongPanel(String[] categories, SongDescriptors song)
	{
		super();

		setSong(song);

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

	public void setDetails(SongDescriptors desc) {
		this.setSong(desc);
	}

	@Override
	protected void paintComponent(Graphics g) 
	{
		super.paintComponent(g);

		Rectangle panelSize = this.getBounds();
		this.setBackground(songPanelColors.getBackgroundColor());

		//Drawing the background image
		//		g.drawImage(backgroundImage, 0, 0, (int)panelSize.getWidth(), (int)panelSize.getHeight(), null);

		// get metrics from the graphics
		FontMetrics metrics = g.getFontMetrics(UIConfig.songNameFont);
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int nameWidth = metrics.stringWidth(songDesc.getSongName());
		int albumWidth = metrics.stringWidth(songDesc.getAlbumName());
		int artistWidth = metrics.stringWidth(songDesc.getArtistName());
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
		FontProcessor.outLine(g, songDesc.getSongName(), songPanelColors.getBackgroundColor(), songPanelColors.getSongColor(), nameX, nameY, UIConfig.songNameFont);
		//Draw album Name
		FontProcessor.outLine(g, songDesc.getAlbumName(), songPanelColors.getBackgroundColor(), songPanelColors.getAlbumColor(), albumX, albumY, UIConfig.albumNameFont);
		//Draw artist name
		FontProcessor.outLine(g, songDesc.getArtistName(), songPanelColors.getBackgroundColor(), songPanelColors.getArtistColor(), artistX, artistY, UIConfig.artistNameFont);
	}

	public SongDescriptors getSong() {
		return songDesc;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}


	public MediaPanelColors setSong(SongDescriptors song)
	{
		this.songDesc = song;

		if(songNameLabel != null) {
			songNameLabel.setText(songDesc.getSongName());
			songNameLabel.setForeground(spc.getSongColor());
		}

		if(albumNameLabel != null) {
			albumNameLabel.setText(songDesc.getAlbumName());
			albumNameLabel.setForeground(spc.getAlbumColor());
		}

		if( artistNameLabel != null) {
			artistNameLabel.setText(songDesc.getArtistName());
			artistNameLabel.setForeground(spc.getArtistColor());
		}

		repaint();
		return mpc;


	}

	public SongPanelColors getLabelsColors() {
		return songPanelColors;
	}

	public void setLabelsColors(SongPanelColors labelsColors) {
		this.songPanelColors = labelsColors;
	}
}
