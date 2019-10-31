package radio.core.testing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.naming.CommunicationException;
import javax.sound.sampled.LineUnavailableException;

import org.junit.Before;
import org.junit.Test;

import radio.client.ClientConfig;
import radio.client.audio.PlayerPropetrties;
import radio.client.audio.UDP_PORT;
import radio.client.communication.ServerConnector;
import radio.client.ui.App;
import radio.core.utilities.BytesUtil;
import radio.server.ServerConfig;

public class Testing {

	private ServerConnector client;
	private DatagramSocket socket;
	private PlayerPropetrties props;

	@Before
	public void init() throws IOException, CommunicationException {
		client = new ServerConnector(ClientConfig.SERVER_URL, ClientConfig.SERVER_PORT);
	}

	/**
	 * Test we get actual song details after asking from server
	 */
	@Test
	public void checkExistenceOfSongDetails() throws IOException {
		props = client.getSongDetailsAndData();
		assertNotNull(props.getFormat());
		assertNotNull(props.getSongDescriptors().getSongName());
		assertNotNull(props.getSongDescriptors().getArtistName());
		assertNotNull(props.getSongDescriptors().getAlbumName());
		assertTrue(props.getSongDescriptors().getDuration() > 0);
	}

	/**
	 * Test that data starts to stream after asking for a song
	 */
	@Test
	public void checkDataIsStreaming() throws IOException, LineUnavailableException, InterruptedException {
		props = client.getSongDetailsAndData();
		UDP_PORT player = new UDP_PORT(props, null);
		client.getAdioData();
		socket = player.getSocket();
		byte[] buf = new byte[ServerConfig.DATAGRAM_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buf, ServerConfig.DATAGRAM_PACKET_SIZE);

		int idx = 0;
		while (idx++ < 20) {
			socket.receive(packet);
			int count = packet.getLength();
			assertTrue(count > 0);
		}

		player.getSocket().close();

	}

	/**
	 * Checks that the client is playing the audio data coming from the server
	 */
	@Test
	public void checkIfplaying() throws IOException, LineUnavailableException, InterruptedException {
		props = client.getSongDetailsAndData();
		UDP_PORT player = new UDP_PORT(props, null);

		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.submit(player);

		Thread.sleep(1000);
		player.stop();
		executor.shutdown();

		client.disconnect();
		player.getSocket().close();
		Thread.sleep(200);

	}

	/**
	 * Test the pause functionality
	 */
	@Test
	public void checkPause() throws IOException, LineUnavailableException, InterruptedException {
		props = client.getSongDetailsAndData();
		UDP_PORT player = new UDP_PORT(props, null);
		client.getAdioData();

		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.submit(player);

		Thread.sleep(1000);
		player.pause();

		Thread.sleep(300);
		player.start();

		Thread.sleep(500);
		player.stop();

		executor.shutdown();

		client.disconnect();
		player.getSocket().close();

		Thread.sleep(200);

	}

	/**
	 * Test the skip song functionality
	 */
	@Test
	public void checkChangeSong()
			throws IOException, LineUnavailableException, InterruptedException, CommunicationException {
		props = client.getSongDetailsAndData();
		UDP_PORT player = new UDP_PORT(props, null);
		client.getAdioData();
		socket = player.getSocket();
		byte[] buf = new byte[ServerConfig.DATAGRAM_PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buf, ServerConfig.DATAGRAM_PACKET_SIZE);

		int idx = 0;
		while (idx++ < 20) {
			socket.receive(packet);
			int count = packet.getLength();
			assertTrue(count > 0);
		}

		client.disconnect();
		socket.close();

		client = new ServerConnector(ClientConfig.SERVER_URL, ClientConfig.SERVER_PORT);
		props = client.getSongDetailsAndData();
		player = new UDP_PORT(props, null);
		client.getAdioData();
		socket = player.getSocket();
		idx = 0;
		while (idx++ < 20) {
			socket.receive(packet);
			int count = packet.getLength();
			assertTrue(count > 0);
		}

		socket.close();

	}

	/**
	 * Test getting random song file from directory
	 */
	@Test
	public void checkRandomSongFile() {
		File songFile = BytesUtil.chooseRandomSong(ServerConfig.MUSIC_FOLDER);
		assertNotNull(songFile);

	}
}
