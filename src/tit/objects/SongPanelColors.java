package tit.objects;

import java.awt.Color;

public class SongPanelColors
{
	private Color songColor;
	private Color albumColor;
	private Color artistColor;
	private Color backgroundColor;

	public SongPanelColors(Color song,Color album, Color artist,Color backgroundColor)
	{
		this.songColor = song;
		this.albumColor = album;
		this.artistColor = artist;
		this.backgroundColor = backgroundColor;
	}


	/**
	 * @return the songColor
	 */
	public Color getSongColor() {
		return songColor;
	}

	/**
	 * @param songColor the songColor to set
	 */
	public void setSongColor(Color songColor) {
		this.songColor = songColor;
	}

	/**
	 * @return the albumColor
	 */
	public Color getAlbumColor() {
		return albumColor;
	}

	/**
	 * @param albumColor the albumColor to set
	 */
	public void setAlbumColor(Color albumColor) {
		this.albumColor = albumColor;
	}

	/**
	 * @return the artistColor
	 */
	public Color getArtistColor() {
		return artistColor;
	}

	/**
	 * @param artistColor the artistColor to set
	 */
	public void setArtistColor(Color artistColor) {
		this.artistColor = artistColor;
	}
	
	/**
	 * @return the backgroundColor
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

		
}
