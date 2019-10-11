package radio.client.ui;
//package tit.client.ui;
//
//import java.awt.BorderLayout;
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import javax.naming.CommunicationException;
//import javax.swing.JComboBox;
//import javax.swing.JFrame;
//import javax.swing.SwingUtilities;
//import javax.swing.WindowConstants;
//
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.embed.swing.JFXPanel;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListCell;
//import javafx.scene.control.ListView;
//import javafx.scene.control.ProgressBar;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.HBoxBuildeir;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBoxBuilder;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.scene.media.MediaView;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.util.Callback;
//import javafx.util.Duration;
//import tit.audio.Song;
//import tit.communication.TCPClient;
//import tit.configuration.GeneralConfig;
//import tit.server.ServerConfig;
//import tit.configuration.UIConfig;
//import tit.dataManagment.DataManagmentUtilities;
//import tit.objects.MediaPanelColors;
//import tit.time.TimeUtilities;
//
///** Example of playing all mp3 audio files in a given directory
// * using a JavaFX MediaView launched from Swing
// */
//public class MainPage {
//
//	//Objects
//	private SceneWrapper sg;
//	private TCPClient tcp;
//	private DataManagmentUtilities dataManager = new DataManagmentUtilities();
//
//
//	//UI objects
//	private SongPanel songPanel;
//	private JFXPanel fxPanel;
//
//
//
//	public MainPage() throws IOException
//	{
//
//		//Creating the tcp client
//		final File baseDir = dataManager.getClientBaseFolder();
//		try {
//			tcp = new TCPClient(ServerConfig.serverIP, ServerConfig.serverPort, baseDir);
//		} catch (CommunicationException | IOException e)
//		{
//			System.out.println("Can't create TCPClient");
//			e.printStackTrace();
//		}
//
//		songPanel = new SongPanel(tcp.getCategories());
//
//	}
//
//
//
//	public void startProgram(String args[])
//	{
//		//		initAndShowGUI(song);
//		//		tcp = new TCPClient();
//	}
//
//
//	public void initAndShowGUI(Song song) {
//		// This method is invoked on Swing thread
//		JFrame frame = new JFrame("RadioTit");
//		fxPanel = new JFXPanel();
//
//		//		songPanel = new SongPanel(categories);
//		MediaPanelColors mpc = songPanel.setSong(song);
//
//		sg = new SceneWrapper();
//
//
//
//
//		frame.add(songPanel,BorderLayout.CENTER);
//		frame.add(fxPanel,BorderLayout.SOUTH);
//		frame.setSize(UIConfig.frameSize);
//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		frame.setVisible(true);
//
//		//		Platform.runLater(new Runnable() {
//		//			@Override public void run() {
//		this.initFX(fxPanel,mpc);
//		//			}
//		//		});
//	}
//
//	public  void changeSong(Song song) {
//		// This method is invoked on Swing thread
//		//		JFrame frame = new JFrame("FX");
//		//		final JFXPanel fxPanel = new JFXPanel();
//		//
//		//
//		//		SongPanel songPanel = new SongPanel();
//		MediaPanelColors mpc = songPanel.setSong(song);
//		this.repaintMediaPanel(mpc);
//		//		frame.add(songPanel,BorderLayout.CENTER);
//		//		frame.add(fxPanel,BorderLayout.SOUTH);
//		//		frame.setSize(UIConfig.frameSize);
//		//		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		//		frame.setVisible(true);
//
//		//		Platform.runLater(new Runnable() {
//		//			@Override public void run() {
//		//TODO				this.initFX(fxPanel, mpc);
//		//			}
//		//		});
//	}
//
//
//	private void initFX(JFXPanel fxPanel, MediaPanelColors mpc) {
//		// This method is invoked on JavaFX thread
//		Scene scene = sg.createScene(mpc);
//		fxPanel.setScene(scene);
//	}
//
//	private void repaintMediaPanel(MediaPanelColors mpc)
//	{
//		sg.updateScene(fxPanel.getScene(), mpc);
//	}
//
//	public static void main(String[] args) throws IOException {
//		MainPage m = new MainPage();
//		//			try {
//		//				tcp = new TCPClient(ServerConfig.serverIP, ServerConfig.serverPort, new File("D:\\RadioTit-client"));
//		//			} catch (CommunicationException | IOException e) {
//		//				// TODO Auto-generated catch block
//		//				e.printStackTrace();
//		//			}
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override public void run() {
//
//				Song song = null;
//
//				try
//				{
//					song = m.tcp.getSongData((String )m.songPanel.categoriesBox.getSelectedItem());
//				} catch (IOException e)
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				m.initAndShowGUI(song);
//			}
//		});
//	}
//
//
//	class SceneWrapper
//	{
//		final Label currentlyPlaying = new Label();
//		final Label TimeLeft = new Label();
//		final ProgressBar progress = new ProgressBar();
//
//		//TODO		@SuppressWarnings("rawtypes")
//		//		final ComboBox categoriesBox = new ComboBox();
//
//		private ChangeListener<Duration> progressChangeListener;
//		private DataManagmentUtilities dataManager = new DataManagmentUtilities();
//		MediaPlayer player;
//		MediaView mediaView;
//		private StackPane layout;
//
//		public SceneWrapper()
//		{
//			System.out.println(dataManager.getMusicFolder());
//			System.out.println(songPanel.getSong());
//			String path = "file:///" + (dataManager.getMusicFolder() +
//					"\\" + songPanel.getSong().getSongFile().getName()).replace("\\", "/").replaceAll(" ", "%20");
//			player = createPlayer(path);
//			mediaView = new MediaView(player);
//		}
//
//
//
//
//		//TODO		@SuppressWarnings({ "rawtypes", "unchecked" })
//		public Scene createScene(MediaPanelColors mpc)
//		{
////			dataManager = new DataManagmentUtilities();
//			layout = new StackPane();
//
//			String path = "file:///" + (dataManager.getMusicFolder() +
//					"\\" + songPanel.getSong().getSongFile().getName()).replace("\\", "/").replaceAll(" ", "%20");
//
//			player = createPlayer(path);
//
//			mediaView = new MediaView(player);
//			//TODO			File mp3File = null;
//
//			// determine the source directory for the playlist
//			//			final File dir = new File("C:\\orr\\RadioTit-serverSide\\music"); //TODO
//			//			final File dir = dataManager.getMusicSourceDirecetory();
//			//			System.out.println(dir);
//			//			if (!dir.exists() || !dir.isDirectory()) {
//			//				System.out.println("Cannot find video source directory: " + dir);
//			//				Platform.exit();
//			//				return null;
//			//			}
//
//			//TODO : create some media players.
//			//			final List<MediaPlayer> players = new ArrayList<MediaPlayer>();
//			//			for (String file : dataManager.getMusicFolder().list(new FilenameFilter() {
//			//				@Override public boolean accept(File dir, String name) {
//			//					return name.endsWith(".mp3");
//			//				}
//			//			})) players.add(createPlayer("file:///" + (dataManager.getMusicFolder() + "\\" + file).replace("\\", "/").replaceAll(" ", "%20")));
//			//			if (players.isEmpty()) {
//			//				System.out.println("No audio found in " + dataManager.getMusicFolder());
//			//				Platform.exit();
//			//				return null;
//			//			}
//
////			String path = "file:///" + (dataManager.getMusicFolder() +
////					"\\" + songPanel.getSong().getSongFile().getName()).replace("\\", "/").replaceAll(" ", "%20");
//			//dataManager.getMusicFolder() +
//			//DataManagmenetConfig.pathSeparator + DataManagmenetConfig.currentSongFileName
////			final MediaPlayer player = createPlayer(path);
//
//			// create a view to show the mediaplayers.
//			//			final MediaView mediaView = new MediaView(players.get(0));
////			final MediaView mediaView = new MediaView(player);
//			player.setOnEndOfMedia(new SongEndRunnable());
//			final Button skip = new Button("Skip");
//			final Button play = new Button("Pause");
//
//			// allow the user to skip a track.
//			skip.setOnAction(new EventHandler<ActionEvent>() {
//				@Override public void handle(ActionEvent actionEvent) {
//
//					new SongEndRunnable().run();
//
//					//					new SongMp3 = tcp.getSongFromServer();
////					Song newSong = null;
////					try {
////						newSong = tcp.getSongData((String )songPanel.categoriesBox.getSelectedItem());
////					} catch (IOException e) {
////						System.out.println("skip was pressed : can't get song from server");
////						e.printStackTrace();
////					}
////					mediaView.getMediaPlayer().currentTimeProperty().removeListener(progressChangeListener);
////					mediaView.getMediaPlayer().stop();
////					//					player.dispose();
////					//					player.currentTimeProperty().removeListener(progressChangeListener);
////					//					player.stop();
////					//					player.dispose();
////					changeSong(newSong);
////					String path = "file:///" + (dataManager.getMusicFolder() + "\\" + songPanel.getSong().getSongFile().getName()).replace("\\", "/").replaceAll(" ", "%20");
////					System.out.println("Path = " + path);
////					MediaPlayer newPlayer = createPlayer(path);
////					mediaView.setMediaPlayer(newPlayer);
////					newPlayer.play();
//
//					//TODO									final MediaPlayer curPlayer = mediaView.getMediaPlayer();
//					//									MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1) % players.size());
//					//									mediaView.setMediaPlayer(nextPlayer);
//					//									curPlayer.currentTimeProperty().removeListener(progressChangeListener);
//					//									curPlayer.stop();
//					//									nextPlayer.play();
//				}
//			});
//
//			// allow the user to play or pause a track.
//			play.setOnAction(new EventHandler<ActionEvent>() {
//				@Override public void handle(ActionEvent actionEvent) {
//					if ("Pause".equals(play.getText())) {
//						mediaView.getMediaPlayer().pause();
//						play.setText("Play");
//					} else {
//						mediaView.getMediaPlayer().play();
//						play.setText("Pause");
//					}
//				}
//			});
//
//			// display the name of the currently playing track.
//			mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
//				@Override public void changed(ObservableValue<? extends MediaPlayer> observableValue, MediaPlayer oldPlayer, MediaPlayer newPlayer) {
//					setCurrentlyPlaying(newPlayer);
//				}
//			});
//
//			// start playing the first track.
//			mediaView.setMediaPlayer(player);
//			mediaView.getMediaPlayer().play();
//			setCurrentlyPlaying(mediaView.getMediaPlayer());
//
//			// silly invisible button used as a template to get the actual preferred size of the Pause button.
//			Button invisiblePause = new Button("Pause");
//			invisiblePause.setVisible(false);
//			play.prefHeightProperty().bind(invisiblePause.heightProperty());
//			play.prefWidthProperty().bind(invisiblePause.widthProperty());
//
//			//Set colors for media panel
//			String hex = setColors(mpc);
//
//			// layout the scene.    #fff8dc
//			layout.setStyle("-fx-background-color:" + hex + "; -fx-font-size: 20; -fx-padding: 20; -fx-alignment: center;");
//			layout.getChildren().addAll(
//					invisiblePause,
//					VBoxBuilder.create().spacing(10).alignment(Pos.CENTER).children(
//							currentlyPlaying,
//							mediaView,
//							HBoxBuilder.create().spacing(10).alignment(Pos.CENTER).children(skip, play, progress, TimeLeft).build()
//							).build()
//					);
//			progress.setMaxWidth(Double.MAX_VALUE);
//			HBox.setHgrow(progress, Priority.ALWAYS);
//
//			return new Scene(layout, 200, 150);
//		}
//
//		public void updateScene(Scene scene, MediaPanelColors mpc)
//		{
//			//Set colors for media panel
//			String hex = setColors(mpc);
//
//			// layout the scene.    #fff8dc
//			layout.setStyle("-fx-background-color:" + hex + "; -fx-font-size: 20; -fx-padding: 20; -fx-alignment: center;");
//		}
//
//
//		private String setColors(MediaPanelColors mpc)
//		{
//			java.awt.Color labelsColor = mpc.getLabelsColors();
//			String textHex = String.format("#%02x%02x%02x", labelsColor.getRed(),
//					labelsColor.getGreen(), labelsColor.getBlue());
//
//			//			System.out.println(this.getClass() + " hex = " + hex);
//
//			currentlyPlaying.setTextFill(Color.web(textHex));
//			TimeLeft.setTextFill(Color.web(textHex));
//			progress.setStyle("-fx-accent: " + textHex + ";");
//
//			java.awt.Color bgColor = mpc.getBackgroundColor();
//			String bgHex = String.format("#%02x%02x%02x", bgColor.getRed(),
//					bgColor.getGreen(), bgColor.getBlue());
//
//			//TODO			categoriesBox.setStyle("-fx-background-color:" + bgHex + "; -fx-font-fill:" + textHex +";");
//			return bgHex;
//		}
//
//		/** sets the currently playing label to the label of the new media player and updates the progress monitor. */
//		private void setCurrentlyPlaying(final MediaPlayer newPlayer) {
//			progress.setProgress(0);
//			progressChangeListener = new ChangeListener<Duration>() {
//				@Override public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
//					progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());
//					setTimeLeft(newPlayer.getTotalDuration().toSeconds() - newPlayer.getCurrentTime().toSeconds());
//
//				}
//			};
//
//			newPlayer.currentTimeProperty().addListener(progressChangeListener);
//
//			String source = newPlayer.getMedia().getSource();
//			source = source.substring(0, source.length() - ".mp4".length());
//			source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
//			currentlyPlaying.setText("Now Playing: " + songPanel.getSong().getSongName());
//		}
//		private void setTimeLeft(double seconds)
//		{
//			TimeLeft.setText(TimeUtilities.secondsToString(seconds));
//		}
//
//		/** @return a MediaPlayer for the given source which will report any errors it encounters */
//		private MediaPlayer createPlayer(String aMediaSrc) {
//			System.out.println("Creating player for: " + aMediaSrc);
//			final MediaPlayer player = new MediaPlayer(new Media(aMediaSrc));
//			player.setOnError(new Runnable() {
//				@Override public void run() {
//					System.out.println("Media error occurred: " + player.getError());
//				}
//			});
//			return player;
//		}
//
//		public class SongEndRunnable implements Runnable
//		{
//
//			@Override
//			public void run()
//			{
//				System.out.println("After song ended : " + "starting process");
//				Song newSong = null;
//				try
//				{
//					System.out.println("After song ended : " + "Getting song");
//					newSong = tcp.getSongData((String)songPanel.categoriesBox.getSelectedItem());
//					System.out.println("After song ended : " + "Got song");
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//				mediaView.getMediaPlayer().currentTimeProperty().removeListener(progressChangeListener);
//				mediaView.getMediaPlayer().stop();
//				System.out.println("After song ended : " + "Stopped player");
//				//					player.stop();
//				//					player.currentTimeProperty().removeListener(progressChangeListener);
//				//					String path = "file:///" + (dataManager.getMusicFolder() +
//				//							"\\" + songPanel.getSong().getSongFile().getName()).replace("\\", "/").replaceAll(" ", "%20");
//				//					MediaPlayer newPlayer = createPlayer(path);
//				//					mediaView.setMediaPlayer(newPlayer);
//				//					newPlayer.play();
//
//				changeSong(newSong);
//				System.out.println("After song ended : " + "Song Changed");
//				String path = "file:///" + (dataManager.getMusicFolder() + "\\" + songPanel.getSong().getSongFile().getName()).replace("\\", "/").replaceAll(" ", "%20");
//				System.out.println("After song ended : " + "Path Created: " + path);
//				MediaPlayer newPlayer = createPlayer(path);
//				newPlayer.setOnEndOfMedia(new SongEndRunnable());
//				System.out.println("After song ended : " + "Player Created");
//				mediaView.setMediaPlayer(newPlayer);
//				System.out.println("After song ended : " + "Media Player Created");
//				newPlayer.play();
//				System.out.println("After song ended : " + "New Player Started");
//			}
//
//		}
//
//	}
//
//
//
//
//}
//
