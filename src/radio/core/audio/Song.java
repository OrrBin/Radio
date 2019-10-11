package radio.core.audio;

import java.io.File;

/**
 * 
 *  
 *
 */
public abstract class Song 
{
	private String songName;
	private String albumName;
	private String artistName;
	private File songFile;
	private File songImage;
	private String genre;
	
	public Song(String songName, String albumName, String artistName,
			String songFileName, String imageFileName, String genre)
	{
		this.songName = songName;
		this.albumName = albumName;
		this.artistName = artistName;
		this.songFile = new File(songFileName);
		this.songImage = new File(imageFileName);
		this.setGenre(genre);
	}
	
	public Song(String songName, String albumName, String artistName,
			File songFile, File songImage, String genre)
	{
		this.songName = songName;
		this.albumName = albumName;
		this.artistName = artistName;
		this.songFile = songFile;
		this.songImage = songImage;
		this.setGenre(genre);
	}
	
	public Song(String songName, String albumName, String artistName)
	{
		this.songName = songName;
		this.albumName = albumName;
		this.artistName = artistName;
	}
	
	/**
	 * @return the songName
	 */
	public String getSongName() {
		return songName;
	}
	
	/**
	 * @param songName the songName to set
	 */
	public void setSongName(String songName) {
		this.songName = songName;
	}
	
	/**
	 * @return the albumName
	 */
	public String getAlbumName() {
		return albumName;
	}
	
	/**
	 * @param albumName the albumName to set
	 */
	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	
	/**
	 * @return the artistName
	 */
	public String getArtistName() {
		return artistName;
	}
	
	/**
	 * @param artistName the artistName to set
	 */
	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}
	
	/**
	 * @return the songFile
	 */
	public File getSongFile() {
		return songFile;
	}
	
	/**
	 * @param songFile the songFile to set
	 */
	public void setSongFile(File songFile) {
		this.songFile = songFile;
	}
	
	/**
	 * @return the songImage
	 */
	public File getSongImage() {
		return songImage;
	}
	
	/**
	 * @param songImage the songImage to set
	 */
	public void setSongImage(File songImage) {
		this.songImage = songImage;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
	

}
