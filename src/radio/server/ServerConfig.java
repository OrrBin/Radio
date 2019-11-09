package radio.server;

/**
 * Server Side Co
 *
 *
 */
public class ServerConfig
{
	//Server port number
	public static int LISTENING_PORT = 5801;

	//Folders
	public static String MUSIC_FOLDER = ".\\music\\";

	public static final int DATAGRAM_PACKET_SIZE = 4096;
	
	public static final int NUMBER_HEADER_SIZE = 4;
	public static final int LONG_NUMBER_HEADER_SIZE = 8;
	public static final int BOOLEAN_HEADER_SIZE = 4;
	public static final int LONG_HEADER_SIZE = 8;
	
	public static final int SONG_NAME_SIZE_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int ALBUM_NAME_SIZE_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int ARTIST_NAME_SIZE_HEADER_SIZE = NUMBER_HEADER_SIZE;

	
	public static final int FILE_SIZE_HEADER_SIZE = LONG_NUMBER_HEADER_SIZE;
	
	public static final int SAMPLE_RATE_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int SAMPLE_SIZE_IN_BITS_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int CHANNELS_HEADER_SIZE = NUMBER_HEADER_SIZE;
	public static final int SIGNED_HEADER_SIZE = BOOLEAN_HEADER_SIZE;
	public static final int BIGENDIAN_HEADER_SIZE = BOOLEAN_HEADER_SIZE;


	public static final String DEFAULT_CHARSET_NAME = "UTF-8";
}
