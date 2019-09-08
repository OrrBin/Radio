package tit.audio;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;

import org.tritonus.share.sampled.file.TAudioFileFormat;
import tit.dbUtilities.AudioUtil;

public class songData {

	private static String DEFAULT_NAME = "unknown";

	private String songName = DEFAULT_NAME;
	private String albumName = DEFAULT_NAME;
	private String artistName = DEFAULT_NAME;
	private float sampleRate;
	private int fileSize;
	private int sampleSizeInBits;
	private int channels;
	private boolean isSigned;
	private boolean isBigEndian;
	private long duration;

	public songData(File songFile) throws IOException, UnsupportedAudioFileException {
		AudioFormat decodedFormat = AudioUtil.getFormat(songFile);
		MP3File mp3Song = null;

		this.sampleRate = decodedFormat.getSampleRate();
		this.sampleSizeInBits = decodedFormat.getSampleSizeInBits();
		this.channels = decodedFormat.getChannels();
		this.isSigned = true;
		this.isBigEndian = decodedFormat.isBigEndian();

		this.duration = getDuration(songFile);

		try
		{
			mp3Song = new MP3File(songFile);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(mp3Song != null && mp3Song.hasID3v2Tag())
		{
			AbstractID3v2 propertiesReader =mp3Song.getID3v2Tag();
			this.songName = propertiesReader.getSongTitle();
			this.artistName = propertiesReader.getLeadArtist();
			this.albumName = propertiesReader.getAlbumTitle();

		}


	}

	private long getDuration(File file) throws UnsupportedAudioFileException, IOException {

		AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
		if (fileFormat instanceof TAudioFileFormat) {
			Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
			String key = "duration";
			Long microseconds = (Long) properties.get(key);
			int mili = (int) (microseconds / 1000);
			return mili;
		} else {
			throw new UnsupportedAudioFileException();
		}

	}

		public String getSongName() {
		return songName;
	}

	public String getAlbumName() {
		return albumName;
	}

	public String getArtistName() {
		return artistName;
	}

	public float getSampleRate() {
		return sampleRate;
	}

	public int getFileSize() {
		return fileSize;
	}

	public int getSampleSizeInBits() {
		return sampleSizeInBits;
	}

	public int getChannels() {
		return channels;
	}

	public boolean isSigned() {
		return isSigned;
	}

	public boolean isBigEndian() {
		return isBigEndian;
	}

	public long getDuration() {
		return duration;
	}

	public void printSongProperties(){
		System.out.println("song : " + songName);
		System.out.println("album : " + albumName);
		System.out.println("artist : " + artistName);
		System.out.println("sample rate : " + sampleRate);
		System.out.println("sample size in bits : " + sampleSizeInBits);
		System.out.println("channels : " + channels);
		System.out.println("signed : " + isSigned);
		System.out.println("bigEndian : " + isBigEndian);
	}

	public void decode(File songFile){

	}}
