/**
 * 
 */
package radio.core.audio;

import java.io.File;

public class SongMp3 extends Song
{
	public SongMp3(String songName, String albumName, String artistName,
			String songFileName, String imageFileName, String genre)
	{
		super(songName, albumName, artistName, 
				songFileName, imageFileName);
	}
	
	public SongMp3(String songName, String albumName, String artistName,
			File songFile, File songImage, String genre)
	{
		super(songName, albumName, artistName,
				songFile, songImage);
	}
	
	public SongMp3(String songName, String albumName, String artistName)
	{
		super(songName, albumName, artistName);
	}
}
