package radio.core.audio;
//package tit.audio;
//
//import java.awt.BorderLayout;
//
//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//
//import javax.swing.JFrame;
//import javax.swing.SwingUtilities;
//
//import tit.configuration.UIConfig;
//import tit.client.ui.SongPanel;
//
//public class Test {
//
//	private SongPanel songPanel;
//
//	private  void initAndShowGUI() {
//		// This method is invoked on the EDT thread
//		JFrame frame = new JFrame("Swing and JavaFX");
////		songPanel = new SongPanel();
//		frame.add(songPanel, BorderLayout.CENTER);
//		
//		final JFXPanel fxPanel = new JFXPanel();
//		
//		frame.add(fxPanel, BorderLayout.SOUTH);
//		frame.setSize(UIConfig.frameSize);
//		frame.setVisible(true);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//		Platform.runLater(new Runnable() {
//			@Override
//			public void run() {
//				initFX(fxPanel);
//			}
//		});
//	}
//
//	private static void initFX(JFXPanel fxPanel) {
//		// This method is invoked on the JavaFX thread
//		Scene scene = createScene();
//		fxPanel.setScene(scene);
//	}
//
//	private static Scene createScene() {
//		Group  root  =  new  Group();
//		Scene  scene  =  new  Scene(root, Color.ALICEBLUE);
//		Text  text  =  new  Text();
//		Button startButton = new Button();
//
//		text.setX(40);
//		text.setY(100);
//		text.setFont(new Font(25));
//		text.setText("Welcome JavaFX!");
//		
//		startButton.setText("Play");
//
//		root.getChildren().add(text);
//		root.getChildren().add(startButton);
//
//		return (scene);
//	}
//
//	public void runApp()
//	{
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				initAndShowGUI();
////				songPanel.setSong(new SongMp3("Small Change Girl", "Poor Boy / Lucky Man", "Asaf Avidan",
////						new File("C:\\orr\\songs\\SmallChangeGirl.mp3"), new File("C:\\orr\\songs\\dude.png")));
//			}
//		});
//	}
//
//	public static void main(String[] args)
//	{
//		Test test = new Test();
//		test.runApp();
//	}
//}
//
