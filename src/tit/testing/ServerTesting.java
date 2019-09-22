package tit.testing;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tit.client.audio.PlayerPropetrties;
import tit.client.audio.PlayingThreadUDP;
import tit.client.UDPStreamingClient;
import tit.server.ServerConfig;

import javax.naming.CommunicationException;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static org.junit.Assert.*;


public class ServerTesting {

    private UDPStreamingClient client;
    private DatagramSocket socket;
    private PlayerPropetrties props;


    @Before
    public void init() throws IOException, CommunicationException {
        client = new UDPStreamingClient(ServerConfig.serverAddr, ServerConfig.serverPort);
    }

    @Test
    public void checkExistenceOfSongDetails() throws IOException  {
        props = client.getSongDetailsAndData("");
        assertNotNull(props.getFormat());
        assertNotNull(props.getSongDescriptors().getSongName());
        assertNotNull(props.getSongDescriptors().getArtistName());
        assertNotNull(props.getSongDescriptors().getAlbumName());
        assertTrue(props.getSongDescriptors().getDuration() > 0);
    }

    @Test
    public void checkIfDataIsStreaming() throws IOException, LineUnavailableException, InterruptedException {
        props = client.getSongDetailsAndData("");
        PlayingThreadUDP player = new PlayingThreadUDP( props, null);
        client.getAdioData();
        socket =  player.getSocket();
        byte[] buf = new byte[ServerConfig.DATAGRAM_PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, ServerConfig.DATAGRAM_PACKET_SIZE);

        int idx = 0;
        while(idx > 20) {
            socket.receive(packet);
            int count = packet.getLength();
            assertTrue(count > 0);
        }
    }

    @After
    public void discard() throws IOException {
        client.disconnect();
        if(socket != null) {
            socket.close();
        }
    }
}
