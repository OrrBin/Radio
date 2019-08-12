package tit.configuration;

public class ClientConfig
{
	public static final int ServerPort = 5801;
	public static final int UdpPort = 5802;
	public static final String ServerAddr = "localhost"; //TODO: change it!
	public static final String ClientDir = "C:\\Users\\RadioTit-client";
	public static final String DefaultClientFolderName = "\\RadioTit-clientSide";
	public static final String DefaultClientFolderPath = "C:\\";
	public static final String DefaultMusicFolder = "\\music";
	public static final String DefaultImagesFolder = "\\Images";

	//Client side requests
	public static final String CsendMeNewSongString = "sendSong";
	public static final String CsendMeAudioData = "sendAudioData";
	public static final String CsendMeNewImageString = "sendImage";
	public static final String CsendMeCategoriesString = "sendCategories";

	public static final String CsendHiThereString = "hi";
	public static final String CsendByeThereString = "bye";
	public static final String lineSperator = "\n";

	public static final Byte CsendHiThere = new Byte(CsendHiThereString);
	public static final Byte CsendByeThere = new Byte(CsendByeThereString);
	public static final String messageDivider = ";";
}
