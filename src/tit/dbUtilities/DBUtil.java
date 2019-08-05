package tit.dbUtilities;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import tit.audio.Song;
import tit.audio.SongMp3;
import tit.configuration.DBConfig;

public class DBUtil
{

	private Connection connection = null;


	private ArrayList<String> categories;
	private PreparedStatement pst;
	private StringBuffer query;

	public DBUtil() throws ClassNotFoundException
	{
		categories = new ArrayList<>();

		//Connect to the mysql database
		initializeConnection();

		try 
		{
			initializeCategories();
		} 
		catch (SQLException e) 
		{
			System.out.println("Can't initialize categories");
			e.printStackTrace();
		}
	}


	private void initializeConnection() throws ClassNotFoundException 
	{
		// This will load the MySQL driver, each DB has its own driver
		Class.forName("com.mysql.jdbc.Driver");

		try 
		{
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + DBConfig.DBName + "?"
					+ "user=" + DBConfig.DBUser + "&password=" + DBConfig.DBPass);
		} catch (SQLException e)
		{
			System.out.println(getClass() +": can not establish connection to database");
			e.printStackTrace();
		}

	}


	private void initializeCategories() throws SQLException 
	{ 
		query = new StringBuffer().append("SELECT DISTINCT category FROM ").append(DBConfig.tableName);
		pst = connection.prepareStatement(query.toString());
		ResultSet rs = pst.executeQuery();
		while(rs.next())
		{
			categories.add(rs.getString("category"));
		}
	}

	public void insertSongData(Song song) throws SQLException
	{
		//1.Song Name, 2.Album Name, 3.Artist Name, 4.Song File, 5.Image File, 6.category
		query = new StringBuffer().append("INSERT INTO ").append(DBConfig.tableName) 
				.append("(id,song_name,album_name,artist_name,song_file,image_file,category) VALUES ")
				.append("(DEFAULT,?,?,?,?,?,?)");

		pst = connection.prepareStatement(query.toString());
		pst.setString(1, song.getSongName());
		pst.setString(2, song.getAlbumName());
		pst.setString(3, song.getArtistName());
		pst.setString(4, song.getSongFile().getPath());
		pst.setString(5, song.getSongImage().getPath());
		pst.setString(6, song.getGenre());

		pst.executeUpdate();

	}

	public ArrayList<Song> getSongsListByCategory(String category) throws SQLException
	{
		ArrayList<Song >songs = new ArrayList<>(); 
		//1.Category
		query = new StringBuffer().append("SELECT * FROM ").append(DBConfig.tableName)
				.append(" WHERE category=").append("?");

		pst = connection.prepareStatement(query.toString());
		pst.setString(1, category);

		ResultSet rs = pst.executeQuery();
		Song currentSong;
		String title;
		String album;
		String artist;
		String songFile;
		String imageFile;
		String genre;
		while(rs.next())
		{
			title = rs.getString("song_name");
			album = rs.getString("album_name");
			artist = rs.getString("artist_name");
			songFile = rs.getString("song_file");
			imageFile = rs.getString("image_file");
			genre = rs.getString("category");

			currentSong = new SongMp3(title, album, artist, songFile, imageFile, genre);
			songs.add(currentSong);
		}

		return songs;
	}


	public ArrayList<String> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<String> categories) {
		this.categories = categories;
	}
}
