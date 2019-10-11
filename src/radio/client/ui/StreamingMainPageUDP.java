package radio.client.ui;

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

import radio.client.audio.PlayerPropetrties;
import radio.client.audio.PlayingThreadUDP;
import radio.client.communication.UDPStreamingClient;
import radio.client.objects.MediaPanelColors;
import radio.core.configuration.UIConfig;
import radio.server.ServerConfig;

public class StreamingMainPageUDP extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	SongEndLineListener titLineListener;
	private UDPStreamingClient streamingClient;
	PlayingThreadUDP player ;
	ExecutorService executor;

	private StreamingSongPanel songPanel;
	private ControlPanel controlPanel;

	private MediaPanelColors mpc;
	
	private String[] categories;

	public StreamingMainPageUDP() throws UnknownHostException, CommunicationException, IOException, LineUnavailableException 
	{
//		tcpClient = new TCPClient(ServerConfig.serverAddr, ServerConfig.serverPort, dataManager.getClientBaseFolder());
		streamingClient = new UDPStreamingClient(ServerConfig.serverAddr, ServerConfig.serverPort);
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

		PlayerPropetrties playerPropetrties = streamingClient.getSongDetailsAndData();
		streamingClient.getAdioData();
		player = new PlayingThreadUDP(playerPropetrties, new SongEndLineListener());

		songPanel = new StreamingSongPanel(new String[] {"Shuffle"},player.getSongDescriptors())  ;
		controlPanel = new ControlPanel(categories);
		controlPanel.setPlayingThread(player);

		this.setResizable(false);
		JPanel pane = new JPanel();
		
		this.add(pane,BorderLayout.CENTER);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWeights = new double[]{1.0};
		gridBagLayout.rowWeights = new double[]{5.0, 1.0, 1.0};
		
		this.getContentPane().setLayout(gridBagLayout);
		
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


		mpc = songPanel.setSong(player.getSongDescriptors());
		controlPanel.setColors(mpc);
		player.waveForm.setColors(mpc);
		executor.submit(player);

	}


	public static void main(String[] args) throws IOException, CommunicationException, LineUnavailableException 
	{
		StreamingMainPageUDP mainPage = new StreamingMainPageUDP();
	}	

	public class SongEndLineListener implements LineListener
	{

		@Override
		public void update(LineEvent le) 
		{			
			if (le.getLine().equals(controlPanel.getPlayingThread().getLine()) && le.getType().equals(LineEvent.Type.CLOSE)) {
					
				
				try {
					System.out.println("streamingClient.disconnect();");
					streamingClient.disconnect();

				} catch (IOException e) {
					e.printStackTrace();
				}
				
				PlayerPropetrties playerPropetrties = null;
				try {
					streamingClient = new UDPStreamingClient(ServerConfig.serverAddr, ServerConfig.serverPort);
					playerPropetrties = streamingClient.getSongDetailsAndData();
					streamingClient.getAdioData();
					player = new PlayingThreadUDP(playerPropetrties, new SongEndLineListener());
				} catch (CommunicationException | IOException | LineUnavailableException e) {
					e.printStackTrace();
				}
				
				StreamingMainPageUDP.this.getContentPane().removeAll();

				
				songPanel.setSong(player.getSongDescriptors());
				
				controlPanel.setPlayingThread(player);
				
				StreamingMainPageUDP.this.songPanel.setSong(playerPropetrties.getSongDescriptors());
				
				controlPanel.setColors(mpc);
				player.waveForm.setColors(mpc);
				

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
				

				StreamingMainPageUDP.this.getContentPane().add(songPanel, songPanelGbc);
				StreamingMainPageUDP.this.getContentPane().add(controlPanel, controlPanelGbc);
				StreamingMainPageUDP.this.getContentPane().add(player.waveForm, waveFormGbc);
				
				StreamingMainPageUDP.this.getContentPane().repaint();

				
				executor.submit(player);
			}
		}
	}
}
