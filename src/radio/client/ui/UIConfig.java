package radio.client.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

public class UIConfig 
{
	//Labels Fonts
	final static Font songNameFont = new Font("Arial", Font.PLAIN, 45);
	final static Font albumNameFont = new Font("Arial", Font.PLAIN, 40);
	final static Font artistNameFont = new Font("Arial", Font.PLAIN, 35);
	public final static Font categoryFont = new Font("Arial", Font.PLAIN, 20);
	
	//Sizes
	public final static int frameWidth = 700;
	final static int frameHeight = 700;
	final static Dimension frameSize = new Dimension(frameWidth, frameHeight);

	//Images
	public final static String defaultImage = "the_dude.png";
	
	//labels height
	public final static int baseSongNameHeight = frameHeight / 4;
	public final static int baseAlbumNameHeight = 2 * frameHeight / 4;
	public final static int baseArtistNameHeight = 3 * frameHeight / 4;
	
	
	public static final int LIGHTEN_COLORS_SUM = 255;
	public static final int LIGHTEN_AND_DARKEN_COLORS_SUM = 510;
	public static final int DARKEN_COLORS_SUM = 765;
	
	
	//How much to lighten
	public final static double LIGHTEN_1_BY = 0.3;
	public final static double LIGHTEN_2_BY = 0.5;
	
	//How much to Darken
	public final static double DARKEN_1_BY = 0.3;
	public final static double DARKEN_2_BY = 0.5;

}
