package tit.audio;

import java.io.File;

public class SongDescriptors
{
	private String songName;
	private String albumName;
	private String artistName;
	private String genere;
	private File songImage;
	
	public SongDescriptors(String songName, String albumName, String artistName, String genere, File imageFile)
	{
		this.setSongName(songName);
		this.setAlbumName(albumName);
		this.setArtistName(artistName);
		this.setGenere(genere);
		this.setSongImage(imageFile);
	}
	
	public SongDescriptors(String songName, String albumName, String artistName, String genere)
	{
		this.setSongName(songName);
		this.setAlbumName(albumName);
		this.setArtistName(artistName);
		this.setGenere(genere);
		this.setSongImage(new File("RadioTit-server/images/"+songName));
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

	public String getGenere() {
		return genere;
	}

	public void setGenere(String genere) {
		this.genere = genere;
	}

	public File getSongImage() {
		
		return songImage;
	}

	public void setSongImage(File imageFile) {
		this.songImage = imageFile;
	}

	public File dirRandom(File dirPath)
	{
		File[] files = dirPath.listFiles();
		return files[(int)(files.length * Math.random())];
	}
}
