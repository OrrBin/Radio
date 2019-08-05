package tit.audio;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.application.Application;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;


public class AudioUtilities extends Application 
{

	private MediaPlayer mediaPlayer;
	private Media media;
	private URL resource;

	public static void main(String[] args) {
		launch(args);
	}

public MediaPlayer getMediaPlayer() {
	return mediaPlayer;
}

	@Override
	public void start(Stage primaryStage) throws MalformedURLException {
		resource = new File("C:\\orr\\songs\\SmallChangeGirl.mp3").toURI().toURL();
		media = new Media(resource.toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//	    primaryStage.setTitle("Audio Player 1");
		//	    primaryStage.setWidth(200);
		//	    primaryStage.setHeight(200);
		//	    primaryStage.show();
	}

	
	
//	public void startPlaying()
//	{
//		launch(null);
//	}
	
	public void stopPlaying()
	{
		System.out.println(mediaPlayer == null ? "mediaPlayer is null" : mediaPlayer.getMedia().getDuration());
		mediaPlayer.stop();
	}



}

