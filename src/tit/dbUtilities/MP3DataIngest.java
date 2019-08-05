package tit.dbUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;

import javafx.scene.shape.Path;
import tit.audio.Song;
import tit.configuration.DataManagmenetConfig;
import tit.configuration.ServerConfig;
import tit.dataManagment.DataManagmentUtilities;
import tit.dataManagment.MP3Filter;

public class MP3DataIngest 
{
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException
	{

		DBUtil dbUtil = new DBUtil();

		if(args.length < 2 || args.length > 2)
		{
			System.out.println("Usage : java dataDir");
			return;
		}
		File songsDirectory = new File(args[0]);
		File imagesDirectory = new File(args[1]);
		if(!songsDirectory.isDirectory())
		{
			System.out.println("Inupt songs directory :" + songsDirectory.getPath() + " Is Not A Directory!   Aborted...");
			return;
		}

		if(!imagesDirectory.isDirectory())
		{
			System.out.println("Inupt images directory :" + imagesDirectory.getPath() + " Is Not A Directory!   Aborted...");
			return;
		}

		File[] songsToIngest = songsDirectory.listFiles(new MP3Filter());

		for(File songFile : songsToIngest)
		{
			String songFileName = songFile.getName();
			Mp3File MP3Song = null;
			String title = "";
			String album = "";
			String artist = "";
			String genre = "";
			File imageFile = null;

			try
			{
				MP3Song = new Mp3File(songFile.getPath());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


			if(MP3Song.hasId3v2Tag())
			{
				int dotPos = songFileName.lastIndexOf(".");
				String nameWithoutExt;

				if(dotPos == -1)
					nameWithoutExt = songFileName;
				else
					nameWithoutExt = songFileName.substring(0,dotPos); 
				
				String[] parts = nameWithoutExt.split("-");
				
//				artist = parts[0].trim();
//				title = parts[1].trim();
//				album = "-Single-";
				
				ID3v2 propertiesReader =MP3Song.getId3v2Tag();
				genre = propertiesReader.getGenreDescription();
				title = propertiesReader.getTitle();
				artist = propertiesReader.getArtist();
				album = propertiesReader.getAlbum();
				
//				propertiesReader.setTitle(title);
//				propertiesReader.setAlbum(album);
//				propertiesReader.setArtist(artist);
				
				File tmpFile = new File(songFile.getParent() + "\\tmp.mp3");
				try {
					MP3Song.save(songFile.getParent() + "\\tmp.mp3");
				} catch (NotSupportedException e1) {
					System.out.println(" blabaavasffddf");
					e1.printStackTrace();
				}


				
				
				boolean imageFound = false;

//				ID3v2 propertiesReader =MP3Song.getId3v2Tag();
				//Use when all properties are set
//				title = propertiesReader.getTitle();
//				System.out.println("title=" + title);
//				album = propertiesReader.getAlbum();
//				System.out.println("album=" + album);
//				artist = propertiesReader.getArtist();
//				System.out.println("artist=" + artist);
//				genre = propertiesReader.getGenreDescription();
				
				Song song;
				//Find image file
				File[] allImages = imagesDirectory.listFiles();

				File newSongFile;
				File newImageFile;

				//Look for Album Art
				for(File file : allImages)
				{
					String name = file.getName();
					int pos = name.lastIndexOf(".");

					name = name.substring(0, pos);
					if(name.equals(album))
					{
						imageFile = file;
						imageFound = true;
						break;

						//						String imageFileName = imageFile.getName();
						//
						//						newSongFile = new File(ServerConfig.musicFolder + DataManagmenetConfig.pathSeparator + songFileName);
						//						newImageFile = new File(ServerConfig.imagesFolder + DataManagmenetConfig.pathSeparator + imageFileName);
						//
						//						song = DataManagmentUtilities.createSong(newSongFile,newImageFile);
						//						try
						//						{
						//							dbUtil.insertSongData(song);
						//						}
						//						catch(Exception e)
						//						{
						//							e.printStackTrace();
						//							//Could not insert data t db
						//							newSongFile.delete();
						//							newImageFile.delete();
						//							break;
						//						}
						//
						//						//Data ingest finished successfully - Removing input files
						//						songFile.delete();
						//						imageFile.delete();
						//
						//						break;

					}		
				}

				//if no album art was found looking for the specific song image
				if( ! imageFound)
				{
					for(File file : allImages)
					{
						String name = file.getName();
						int pos = name.lastIndexOf(".");

						name = name.substring(0, pos);
						if(name.equals(title))
						{
							imageFile = file;
							imageFound = true;
							break;
							//							song = DataManagmentUtilities.createSong(songFile,imageFile);
							//							dbUtil.insertSongData(song);
						}		
					}
				}

				//If album art not found nor song image looking for artist image
				if( ! imageFound)
				{
					for(File file : allImages)
					{
						String name = file.getName();
						int pos = name.lastIndexOf(".");

						name = name.substring(0, pos);
						if(name.equals(artist))
						{
							imageFile = file;
							imageFound = true;
							break;
							//							song = DataManagmentUtilities.createSong(songFile,imageFile);
							//							dbUtil.insertSongData(song);
						}		
					}
				}



				if( ! imageFound)
				{
					System.out.println("Could not find matcing image to song file : " + songFileName + "\n"
							+ "Properties :" + "\nAlbum: " + album + "\nArtist: " + artist + "\ntitle: " + title +"\n"
							+ "Skipping this file");	
				}
				else
				{
					String imageFileName = imageFile.getName();

					newSongFile = new File(ServerConfig.musicFolder + DataManagmenetConfig.pathSeparator + title + ".mp3");
					newImageFile = new File(ServerConfig.imagesFolder + DataManagmenetConfig.pathSeparator + imageFileName);

					if(newSongFile.exists())
						newSongFile.delete();
					if(newImageFile.exists())
						newImageFile.delete();

					Files.copy(tmpFile.toPath(), newSongFile.toPath());
					Files.copy(imageFile.toPath(), newImageFile.toPath());
					
					tmpFile.delete();


					song = DataManagmentUtilities.createSong(newSongFile,newImageFile);
					boolean isSuccess = true;
					try
					{
						dbUtil.insertSongData(song);
					}
					catch(Exception e)
					{

						e.printStackTrace();
						//Could not insert data t db
						newSongFile.delete();
						newImageFile.delete();
						isSuccess = false;
					}

					if(isSuccess)
					{
						//Data ingest finished successfully - Removing input files
						songFile.delete();
						//						imageFile.delete();
					}
					else
						System.out.println("Could not ingest song file : " + songFile + " and image file : " + imageFile);
				}
			}
		}
	}
}
