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
import tit.audio.PlayingThread;
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
	//private TCPClient tcpClient;

	DataManagmentUtilities dataManager;

	ExecutorService executor;

	private StreamingSongPanel songPanel;
	private ControlPanel controlPanel;

	private String[] categories;

	public StreamingMainPageUDP() throws UnknownHostException, CommunicationException, IOException, LineUnavailableException 
	{
		dataManager = new DataManagmentUtilities();
//		tcpClient = new TCPClient(ServerConfig.serverAddr, ServerConfig.serverPort, dataManager.getClientBaseFolder());
		streamingClient = new UDPStreamingClient(ServerConfig.serverAddr, ServerConfig.serverPort, dataManager.getClientBaseFolder());
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
		PlayingThreadUDP player = new PlayingThreadUDP(playerPropetrties, new TitLineListener());
		player.addLineListener(new TitLineListener());
//		streamingClient.getAudioData();
		

		this.setResizable(false);
		songPanel = new StreamingSongPanel(new String[] {"Shuffle"},player.getSongStream())  ;
		controlPanel = new ControlPanel(categories);

		JPanel pane = new JPanel();
		
		this.add(pane,BorderLayout.CENTER);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
		
		this.getContentPane().setLayout(gridBagLayout);
//		pane.setLayout();
//		pane.setBackground(Color.red);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridheight = 4;

		
		
		this.getContentPane().add(songPanel, gbc);
		this.getContentPane().add(controlPanel, gbc);
		
		GridBagConstraints gbc1 = new GridBagConstraints();
		gbc1.fill = GridBagConstraints.BOTH;
		gbc1.gridx = 0;
		gbc1.gridy = 4;
		
//		this.getContentPane().add(player.waveForm, gbc1);
		
		
//		this.add(songPanel,BorderLayout.CENTER);
//		player.waveForm.setSize(this.getWidth(), this.getHeight()/4);
//		this.add(player.waveForm, BorderLayout.SOUTH);
//		this.add(controlPanel,BorderLayout.SOUTH);
		this.setSize(UIConfig.frameSize);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setVisible(true);


		MediaPanelColors mpc = songPanel.setSong(player.getSongStream());
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
			if (le.getLine().equals(controlPanel.getPlayingThread().getLine()) && le.getType().equals(LineEvent.Type.CLOSE))
			{				
				PlayerPropetrties properties;
				PlayingThreadUDP player = null;
				try 
				{
					double d = Math.ceil(Math.random() * 2);
					if(d > 1)
						properties = streamingClient.getSongDetailsAndData("led zepplin");
					else properties = streamingClient.getSongDetailsAndData("asaf");
					player = new PlayingThreadUDP(properties, new TitLineListener());
				}
				catch (IOException | LineUnavailableException e) 
				{
					e.printStackTrace();
				}

				MediaPanelColors mpc = songPanel.setSong(player.getSongStream());

				try 
				{
					controlPanel.setPlayingThread(player, mpc);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				executor.execute(player);


			}
		}

	}




}
