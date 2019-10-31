package radio.client;

public class ClientConfig
{
	public static String SERVER_URL = "localhost";
	public static int SERVER_PORT = 5801; 
	
	
	public static final int UdpPort = 5802;

	//Client side requests
	public static final String CsendMeNewSongString = "sendSong";
	public static final String CsendMeAudioData = "sendAudioData";
	public static final String CsendMeNewImageString = "sendImage";
	public static final String CsendMeCategoriesString = "sendCategories";

	public static final String CsendHiThereString = "hi";
	public static final String CsendByeString = "bye";
	public static final String lineSperator = "\n";

	public static final String messageDivider = ";";
}
