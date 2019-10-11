package radio.server;

/**
 * Server Side Co
 *  
 *
 */
public class ServerConfig
{
	//Folders
	public static final String baseFolder = "RadioTit-server\\music\\";
	public static final String musicFolder = baseFolder + "\\music";
	public static final String imagesFolder = baseFolder + "\\images";

	//Server side requests (Strings)
	public static final String SHiThereString = "hi";

	//Server side requests (bytes)
	public static final Byte CsendHiThere = new Byte(SHiThereString);
	
	//Size of the header that holds the file name length in bytes
	public static final int fileSizeHeader = Integer.SIZE / Byte.SIZE;

	//Server port number
	public static final int serverPort = 5801;

	//Server IP
	public static final String serverAddr = "localhost"; //TODO : change it!
	
	public static final int DATAGRAM_PACKET_SIZE = 4096;
	
	public static final int NUMBER_HEADER_SIZE = 4;
	public static final int LONG_NUMBER_HEADER_SIZE = 8;
	public static final int BOOLEAN_HEADER_SIZE = 4;
	public static final int LONG_HEADER_SIZE = 8;
	
	public static final int SONG_NAME_SIZE_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int ALBUM_NAME_SIZE_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int ARTIST_NAME_SIZE_HEADER_SIZE = NUMBER_HEADER_SIZE;
	
//	public static final int SONG_NAME_HEADER_SIZE = STRING_HEADER_SIZE;
//	public static final int ALBUM_NAME_HEADER_SIZE = STRING_HEADER_SIZE;
//	public static final int ARTIST_NAME_HEADER_SIZE = STRING_HEADER_SIZE;
	
	public static final int FILE_SIZE_HEADER_SIZE = LONG_NUMBER_HEADER_SIZE;
	
	public static final int SAMPLE_RATE_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int SAMPLE_SIZE_IN_BITS_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int CHANNELS_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int SIGNED_HEADER_SIZE = BOOLEAN_HEADER_SIZE;
	public static final int BIGENDIAN_HEADER_SIZE = BOOLEAN_HEADER_SIZE;
	public static final int DURATION_HEADER_SIZE = LONG_HEADER_SIZE;

	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
}
