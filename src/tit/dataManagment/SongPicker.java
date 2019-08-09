package tit.dataManagment;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;


import javafx.print.Collation;
import tit.audio.Song;
import tit.audio.SongMp3;
import tit.configuration.GeneralConfig;
import tit.configuration.ServerConfig;
import tit.dbUtilities.DBUtil;

public class SongPicker
{
	HashMap<String, Category> categories;
	HashMap<String,Integer> indexes;
	DBUtil dbUtil;

	public SongPicker() throws ClassNotFoundException, SQLException
	{
		categories = new HashMap<>();
		indexes = new HashMap<>();

		DBUtil dbUtil = new DBUtil();
		ArrayList<String>categoriesNames = dbUtil.getCategories();

		ArrayList<Song> songs;
		for(String categoryName : categoriesNames)
		{
			System.out.println(categoryName);
			songs = dbUtil.getSongsListByCategory(categoryName);
			Collections.shuffle(songs);
			Category c = new Category(categoryName, songs);
			categories.put(categoryName, c);
			indexes.put(categoryName, 0);

		}




		//		categories = new HashMap<>();
		//
		//
		//		File musicDir = new File(ServerConfig.musicFolder);
		//		File imagesDir = new File(ServerConfig.imagesFolder);
		//
		//		//		FilenameFilter mp3Filter = new FilenameFilter() {
		//		//			public boolean accept(File file, String name) {
		//		//				if (name.endsWith(".mp3")) {
		//		//					// filters files whose extension is .mp3
		//		//					return true;
		//		//				} else {
		//		//					return false;
		//		//				}
		//		//			}
		//		//		};
		//
		//		MP3Filter mp3Filter = new MP3Filter();
		//		File[] songFiles = musicDir.listFiles(mp3Filter);
		//
		//		FilenameFilter jpgFilter = new FilenameFilter() {
		//			public boolean accept(File file, String name) {
		//				if (name.endsWith(".jpg")) {
		//					// filters files whose extension is .mp3
		//					return true;
		//				} else {
		//					return false;
		//				}
		//			}
		//		};		
		//		File[] imageFiles = imagesDir.listFiles(jpgFilter);
		//
		//		LinkedList<Song> songsList = new LinkedList<>();
		//		for(int i = 0; i < songFiles.length; i++)
		//		{	
		//			songsList.add(new  SongMp3("Tmp Name", "Tmp Album", "Tmp Artist",
		//					songFiles[i], imageFiles[(int) (Math.random() * (imageFiles.length - 1))],"Tmp Category"));
		//		}
		//		Category category = new Category("random", songsList);
		//		categories.put("random", category);
	}

	/**
	 * Pick random song from the specific category
	 * and returns the song file and an image file
	 * @param category
	 * @return {song file, image file}
	 */
	public Song shuffle(String category) 
	{
		Song song;
		switch(category)
		{
		case GeneralConfig.randomCategory: song = random();	
		break;
		default : song = pickByCategory(category);
		break;
		}

		return song;

		//		//TODO: create song picker mechanism
		//				switch(category)
		//				{
		//				case "redHot": return new  SongMp3("Can't Stop", "By The Way", "Red Hot Chilli Peppers",
		//						new File("D:\\RadioTit-server\\music\\Can't Stop.mp3"), new File("D:\\RadioTit-server\\images\\Sultans Of Swing.jpg"));
		//				
		//				case "asaf": return new  SongMp3("Different Pulses", "Different Pulses", "Asaf Avidan",
		//						new File("D:\\RadioTit-server\\music\\Different Pulses.mp3"), new File("D:\\RadioTit-server\\images\\Sultans Of Swing.jpg"));
		//				
		//				default: return new  SongMp3("Different Pulses", "Different Pulses", "Asaf Avidan",
		//						new File("D:\\RadioTit-server\\music\\Different Pulses.mp3"), new File("D:\\RadioTit-server\\images\\DifferentPulses.jpg"));
		//				}
	}

	private Song pickByCategory(String category) 
	{

		int index = indexes.get(category);

		File songFile = categories.get(category).getSong(index).getSongFile();
		File imageFile = categories.get(category).getSong(index).getSongImage();

		//Next time new song
		if(index < categories.get(category).getSongs().size() - 1)
			index++;
		else 
			index = 0;
		
		indexes.put(category, index);


		System.out.println("Category : " + category + " index = " + index);

		return new  SongMp3("Tmp Name", "Tmp Album", "Tmp Artist", songFile, imageFile, "Tmp category");
		//		int rnd = (int) (Math.random() * (categories.get(category).getSongs().size() ));
		//
		//		File songFile = categories.get(category).getSong(rnd).getSongFile();
		//		File imageFile = categories.get(category).getSong(rnd).getSongImage();
		//		
		//
		//		return new  SongMp3("Tmp Name", "Tmp Album", "Tmp Artist", songFile, imageFile, "Tmp category");
	}

	private Song random() 
	{
		Set<String> categoriesNames = categories.keySet();
		String[] categoriesArray = null;
		categoriesArray = categoriesNames.toArray(new String[0]);
		for(String s : categoriesArray)
		{
			System.out.println(s);
		}
		int Crnd = (int) (Math.random() * (categoriesArray.length));
		String category = categoriesArray[Crnd];
		System.out.println("choosen : " + category);

		return pickByCategory(category);

		//		int Srnd = (int) (Math.random() * (categories.get(category).getSongs().size()));
		//		
		//		File songFile = categories.get(category).getSong(Srnd).getSongFile();
		//		File imageFile = categories.get(category).getSong(Srnd).getSongImage();
		//		
		//		return new  SongMp3("Tmp Name", "Tmp Album", "Tmp Artist", songFile, imageFile, "Tmp category");
	}
}