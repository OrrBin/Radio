package tit.dataManagment;

import java.util.ArrayList;

import tit.audio.Song;

public class Category 
{
	private String categoryName;

	private ArrayList<Song> songs;
	
	public Category(String categoryName) 
	{
		this.categoryName = categoryName;
	}
	
	public Category(String categoryName, ArrayList<Song> songs) 
	{
		this.categoryName = categoryName;
		this.setSongs(songs);
	}
	
	
	public Song getSong(int index)
	{
		Song song = songs.get(index);
//		songs.remove(index);
		
		return song;
	}
	

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public ArrayList<Song> getSongs() {
		return songs;
	}

	public void setSongs(ArrayList<Song> songs) {
		this.songs = songs;
	}
}
