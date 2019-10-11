package radio.core.audio;

import java.io.File;

public class SongDescriptors
{
	private String songName;
	private String albumName;
	private String artistName;
	private File songImage;
	private long duration;
	
	public SongDescriptors(String songName, String albumName, String artistName, long duration)
	{
		this.setSongName(songName);
		this.setAlbumName(albumName);
		this.setArtistName(artistName);
		this.setSongImage(new File("RadioTit-server/images/"+songName));
		this.duration = duration;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	
	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	public String getArtistName() {
		return artistName;
	}

	public void setArtistName(String artistName) {
		this.artistName = artistName;
	}

	public long getDuration() {
		return this.duration;
	}

	public File getSongImage() {
		
		return songImage;
	}

	public void setSongImage(File imageFile) {
		this.songImage = imageFile;
	}

}
