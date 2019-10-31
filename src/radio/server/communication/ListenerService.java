package radio.server.communication;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import radio.server.ServerConfig;

public class ListenerService extends Thread {

	static HashMap<InetAddress, ClientHandler> clients = new HashMap<>();

	public static void main (String args[])
	{

		if(args.length < 1) {
			printMissingMusicFolder();
			return;
		}

		ServerConfig.MUSIC_FOLDER = args[0];
		File musicFolder = new File(ServerConfig.MUSIC_FOLDER);
		if(!musicFolder.exists()) {
			printFolderDoesNotExist();
			return;
		}

		if(musicFolder.listFiles().length == 0) {
			printFolderContainsNoFiles();
			return;
		}

		for(File file : musicFolder.listFiles()) {
			String fileName = file.getName();
			int i = fileName.lastIndexOf('.');
			if (i > 0) {
				String extension = fileName.substring(i+1);
				if(!extension.equals("mp3")) {
					printFolderContainsBadFiles();
					return;
				}
			} else {
				printFolderContainsBadFiles();
				return;
			}



			if(args.length > 1) {
				try {
					ServerConfig.LISTENING_PORT = Integer.valueOf(args[1]);
				} catch(NumberFormatException e) {
					printWrongNumberFormat();
					return;
				}
			}
		}

		try(ServerSocket listenSocket = new ServerSocket(ServerConfig.LISTENING_PORT))
		{
			System.out.println("server start listening on port " + ServerConfig.LISTENING_PORT +  ", music folder is: " + ServerConfig.MUSIC_FOLDER + " ... ... ...");  
			while(true) {   
				Socket clientSocket = listenSocket.accept(); 
				InetAddress address = listenSocket.getInetAddress();
				clients.put(address, new ClientHandler(clientSocket));
			}   
		}   
		catch(IOException e) {  
			System.out.println("Listen :"+e.getMessage());
		}
	}

	private static void printExpectedFormat() {
		System.out.println("expected parameters : PORT (integer)	MUSIC_DIRECTORY (optinal) (String - path to folder containing .mp3 audio files)");
	}

	private static void printMissingMusicFolder() {
		System.out.println("missing music folder path");
	}
	
	private static void printWrongNumberFormat() {
		printExpectedFormat();
		System.out.println("Wrong format for parameter PORT - expected number");
	}

	private static void printFolderDoesNotExist() {
		printExpectedFormat();
		System.out.println("Specified music folder does not exist");
	}

	private static void printFolderContainsNoFiles() {
		printExpectedFormat();
		System.out.println("Specified music folder is empty");
	}

	private static void printFolderContainsBadFiles() {
		printExpectedFormat();
		System.out.println("Expected folder with only .mp3 files. Specified music folder contatins files that does not have .mp3 extension or folders");
	}
}
