package tit.audio;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import tit.configuration.DataManagmenetConfig;
import tit.configuration.ServerConfig;
import tit.dataManagment.DataManagmentUtilities;
import tit.dataManagment.MP3Filter;
import tit.dbUtilities.AudioUtil;
import utilities.Util;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class songData {

    private String songName;
    private String albumName;
    private String artistName;
    private float sampleRate;
    private int fileSize;
    private int sampleSizeInBits;
    private int channels;
    private boolean isSigned;
    private boolean isBigEndian;
    private File imageFile = null;

    public songData(File songFile) throws IOException, UnsupportedAudioFileException {
        AudioFormat decodedFormat = AudioUtil.getFormat(songFile);

        String songFileName = songFile.getName();
        Mp3File MP3Song = null;
        String genre = "";

        this.sampleRate = decodedFormat.getSampleRate();
        this.sampleSizeInBits = decodedFormat.getSampleSizeInBits();
        this.channels = decodedFormat.getChannels();
        this.isSigned = true;
        this.isBigEndian = decodedFormat.isBigEndian();

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

                ID3v2 propertiesReader =MP3Song.getId3v2Tag();
                genre = propertiesReader.getGenreDescription();
                this.artistName = propertiesReader.getArtist();
                this.albumName = propertiesReader.getAlbum();

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
