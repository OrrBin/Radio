package tit.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.CommunicationException;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import tit.audio.PlayerPropetrties;
import tit.audio.PlayingThreadUDP;
import tit.communication.UDPStreamingClient;
import tit.configuration.ServerConfig;
import tit.configuration.UIConfig;
import tit.dataManagment.DataManagmentUtilities;
import tit.objects.MediaPanelColors;

public class StreamingMainPageUDP extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TitLineListener titLineListener;
	private UDPStreamingClient streamingClient;
	DataManagmentUtilities dataManager;
	PlayingThreadUDP player ;
	ExecutorService executor;

	private StreamingSongPanel songPanel;
	private ControlPanel controlPanel;

	private String[] categories;

	public StreamingMainPageUDP() throws UnknownHostException, CommunicationException, IOException, LineUnavailableException 
	{
		dataManager = new DataManagmentUtilities();
//		tcpClient = new TCPClient(ServerConfig.serverAddr, ServerConfig.serverPort, dataManager.getClientBaseFolder());
		streamingClient = new UDPStreamingClient(ServerConfig.serverAddr, ServerConfig.serverPort,
				dataManager.getClientBaseFolder());
		executor = Executors.newFixedThreadPool(1);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				try {
					streamingClient.disconnect();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		System.out.println("initiated streaming client");

//		categories = tcpClient.getCategories();

		PlayerPropetrties playerPropetrties = streamingClient.getSongDetailsAndData("led zepplin");
		System.out.println("After all shit");
		player = new PlayingThreadUDP(playerPropetrties, new TitLineListener());
		player.addLineListener(new TitLineListener());
//		streamingClient.getAudioData();
		

		this.setResizable(false);
		songPanel = new StreamingSongPanel(new String[] {"Shuffle"},player.getSongDescriptors())  ;
		controlPanel = new ControlPanel(categories);

		JPanel pane = new JPanel();
		
		this.add(pane,BorderLayout.CENTER);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{5.0, 1.0, 1.0};
		
		this.getContentPane().setLayout(gridBagLayout);
//		pane.setLayout();
//		pane.setBackground(Color.red);
		
		GridBagConstraints songPanelGbc = new GridBagConstraints();
		songPanelGbc.fill = GridBagConstraints.BOTH;
		songPanelGbc.gridx = 0;
		songPanelGbc.gridy = 0;
		
		GridBagConstraints controlPanelGbc = new GridBagConstraints();
		controlPanelGbc.fill = GridBagConstraints.BOTH;
		controlPanelGbc.gridx = 0;
		controlPanelGbc.gridy = 1;

		GridBagConstraints waveFormGbc = new GridBagConstraints();
		waveFormGbc.fill = GridBagConstraints.BOTH;
		waveFormGbc.gridx = 0;
		waveFormGbc.gridy = 2;
		

		this.getContentPane().add(songPanel, songPanelGbc);
		this.getContentPane().add(controlPanel, controlPanelGbc);
		this.getContentPane().add(player.waveForm, waveFormGbc);

	
		this.setSize(UIConfig.frameSize);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);


		MediaPanelColors mpc = songPanel.setSong(player.getSongDescriptors());
		player.waveForm.setColors(mpc);
		controlPanel.setPlayingThread(player, mpc);
		executor.submit(player);

		System.out.println("finished constructing Main Page");
	}


	public static void main(String[] args) throws IOException, CommunicationException, LineUnavailableException 
	{
		StreamingMainPageUDP mainPage = new StreamingMainPageUDP();
	}	

	public class TitLineListener implements LineListener
	{

		@Override
		public void update(LineEvent le) 
		{			
			if (le.getLine().equals(controlPanel.getPlayingThread().getLine()) && le.getType().equals(LineEvent.Type.CLOSE)) {
				try {
					streamingClient.disconnect();
					System.out.println("streamingClient.disconnect();");

				} catch (IOException e) {
					e.printStackTrace();
				}
//				PlayerPropetrties properties;
//				PlayingThreadUDP player = null;
//				try
//				{
//					double d = Math.ceil(Math.random() * 2);
//					if(d > 1)
//						properties = streamingClient.getSongDetailsAndData("led zepplin");
//					else properties = streamingClient.getSongDetailsAndData("asaf");
//					player = new PlayingThreadUDP(properties, new TitLineListener());
//				}
//				catch (IOException | LineUnavailableException e)
//				{
//					e.printStackTrace();
//				}
//
//				MediaPanelColors mpc = songPanel.setSong(player.getSongDescriptors());
//
//				try
//				{
//					controlPanel.setPlayingThread(player, mpc);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//				executor.execute(player);
//			}
			}
		}
	}
}
