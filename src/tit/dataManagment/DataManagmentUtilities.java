/**
 * 
 */
package tit.dataManagment;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import tit.audio.Song;
import tit.audio.SongMp3;
import tit.configuration.ClientConfig;
import tit.configuration.DataManagmenetConfig;

/**
 * 
 *  
 *
 */
public class DataManagmentUtilities
{
	private File clientBaseFolder;
	private File musicFolder;
	private File imagesFolder;



	public DataManagmentUtilities()
	{
		String path = null;
		try
		{
			//Set the file path to the same file where the jar file is located
//			path = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getSchemeSpecificPart();
			path = "C:\\RadioTit";
		}
		catch(Exception e)
		{
			System.out.println("Can not create high scores file path, going to try default location. Path = " + path.toString());
			//Try default location
			path = (ClientConfig.DefaultClientFolderPath);
			//Notify if path creation has been successful
			System.out.println("High scors file path set to default location");
		}

		//Creating the client base directory
		clientBaseFolder = new File(path + ClientConfig.DefaultClientFolderName);
		if( ! clientBaseFolder.exists())
			clientBaseFolder.mkdirs();

		//Creating the music directory
		musicFolder = new File(clientBaseFolder.getPath() + ClientConfig.DefaultMusicFolder);
		if( ! musicFolder.exists())
			musicFolder.mkdirs();

		//Creating the images directory
		imagesFolder = new File(clientBaseFolder.getPath() + ClientConfig.DefaultImagesFolder);
		if( ! imagesFolder.exists())
			imagesFolder.mkdirs();

	}

	//	public File getMusicSourceDirecetory()
	//	{
	//		String path = clientBaseFolder + ClientConfig.DefaultMusicFolder;
	//		File musicFolder = new File(path);
	//		if(!musicFolder.exists())
	//			musicFolder.mkdirs();
	//		return musicFolder;
	//	}

	public static void writeImageToFile(BufferedImage image, File output) throws DataManagerException
	{
		String formatName = "";
		String fileName = output.getAbsoluteFile().getName();
		if(fileName.lastIndexOf(".") > 0)
			formatName =  fileName.substring(fileName.lastIndexOf(".")+1);

		try {
			ImageIO.write(image, formatName, output);
		} catch (IOException e) {
			e.printStackTrace(); //TODO : Remove
			throw new DataManagerException(DataManagmenetConfig.cantWriteImageTofile +
					output.getAbsolutePath());
		}
	}

	public static void createFolder(String path, String name) throws DataManagerException
	{
		File folder = new File(path + "\\" + name);
		boolean b = folder.mkdir();
		if(!b)
			throw new DataManagerException(DataManagmenetConfig.cantCreateDirectory + 
					folder.getAbsolutePath());

	}

	public static Song createSong(File songFile, File imageFile)
	{
		Mp3File song = null;
		String title = "";
		String album = "";
		String artist = "";
		String genre = "";
		
		try
		{
			song = new Mp3File(songFile.getPath());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			title = "error_reading";
			album = "error_reading";
			artist = "error_reading";
			genre = "error_reading";
			
			return new SongMp3(title, album, artist, songFile, imageFile, genre);
		}


		if(song.hasId3v2Tag())
		{
			ID3v2 propertiesReader =song.getId3v2Tag();
			title = propertiesReader.getTitle();
			System.out.println("title=" + title);
			album = propertiesReader.getAlbum();
			System.out.println("album=" + album);
			artist = propertiesReader.getArtist();
			System.out.println("artist=" + artist);
			genre = propertiesReader.getGenreDescription();
			
//TODO :			System.out.println("subString=" + title.substring(0,1));
//			
//			if(title.substring(0,1).equals("?"))
//				{
//					System.out.println("new title=" + title.substring(1, title.length()));
//					title.substring(1, title.length());
//					
//				}
		}
		
		return new SongMp3(title, album, artist, songFile, imageFile, genre);

	}
	
	
	public Song createSong(File songFile)
	{
		Mp3File song = null;
		String title = "";
		String album = "";
		String artist = "";
		String genre = "";
		File imageFile;
		
		try
		{
			song = new Mp3File(songFile.getPath());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			title = "error_reading";
			album = "error_reading";
			artist = "error_reading";
			genre = "error_reading";
			ClassLoader classLoader = DataManagmentUtilities.class.getClass().getClassLoader();
			imageFile = new File(classLoader.getResource("Error.jpg").getFile());
			
			return new SongMp3(title, album, artist, songFile, imageFile, genre);
		}


		if(song.hasId3v2Tag())
		{
			ID3v2 propertiesReader =song.getId3v2Tag();
			title = propertiesReader.getTitle();
			System.out.println("title=" + title);
			album = propertiesReader.getAlbum();
			System.out.println("album=" + album);
			artist = propertiesReader.getArtist();
			System.out.println("artist=" + artist);
			genre = propertiesReader.getGenreDescription();
			
			//Find image file
			File[] allImages = imagesFolder.listFiles();
			//Look for Album Art
			for(File file : allImages)
			{
				String name = file.getName();
				int pos = name.lastIndexOf(".");
				
				name = name.substring(0, pos);
				if(name.equals(album))
				 {
				 	imageFile = file;
				 	return new SongMp3(title, album, artist, songFile, imageFile, genre);
				 }		
			}
			
			//If album art not found looking for artist image
			for(File file : allImages)
			{
				String name = file.getName();
				int pos = name.lastIndexOf(".");
				
				name = name.substring(0, pos);
				if(name.equals(artist))
				 {
				 	imageFile = file;
				 	return new SongMp3(title, album, artist, songFile, imageFile, genre);
				 }		
			}
			
			//if no album art was found nor artist image looking for the specific song image
			for(File file : allImages)
			{
				String name = file.getName();
				int pos = name.lastIndexOf(".");
				
				name = name.substring(0, pos);
				if(name.equals(title))
				 {
					imageFile = file;
				 	return new SongMp3(title, album, artist, songFile, imageFile, genre);
				 }		
			}
			

		}
		
		//If no Image Was Found, returns default image
		ClassLoader classLoader = DataManagmentUtilities.class.getClass().getClassLoader();
		imageFile = new File(classLoader.getResource("Error.jpg").getFile());
		return new SongMp3(title, album, artist, songFile, imageFile, genre);

	}
	

	public File getClientBaseFolder() {
		return clientBaseFolder;
	}

	public void setClientBaseFolder(File clientBaseFolder) {
		this.clientBaseFolder = clientBaseFolder;
	}

	public File getMusicFolder() {
		return musicFolder;
	}

	public void setMusicFolder(File musicFolder) {
		this.musicFolder = musicFolder;
	}

	public File getImagesFolder() {
		return imagesFolder;
	}

	public void setImagesFolder(File imagesFolder) {
		this.imagesFolder = imagesFolder;
	}
}
