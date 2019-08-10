package tit.communication;

import com.mysql.fabric.xmlrpc.Client;
import tit.audio.PlayerPropetrtiesUDP;
import tit.audio.SongStream;
import tit.configuration.ClientConfig;
import tit.configuration.ServerConfig;
import utilities.Util;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class UDPStreamingClient {
    private DatagramSocket clientSocket;
    private InetAddress address;

    private DataOutputStream output;
    private DataInputStream input;

    private DataOutputStream outToServer;
    private InputStreamReader inFromServer;
    private BufferedReader stringInFromServer;

    private File musicDirectory;
    private File imagesDirectory;

    private byte[] buf;

    public UDPStreamingClient(File ClientDirectory) throws SocketException, UnknownHostException {
        clientSocket = new DatagramSocket();
        address = InetAddress.getByName (ClientConfig.ServerAddr);
        this.musicDirectory = new File(ClientDirectory.getPath() + ClientConfig.DefaultMusicFolder);
        this.imagesDirectory = new File(ClientDirectory.getPath() + ClientConfig.DefaultImagesFolder);

    }
// TOOD : delete after changing Orr's code to handle UDP send(?)&receive
    public String sendEcho(String msg) {
        buf = msg.getBytes();
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, address, ClientConfig.ServerPort);
        clientSocket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        clientSocket.receive(packet);
        String received = new String(
                packet.getData(), 0, packet.getLength());
        return received;
    }

    public void close() {
        clientSocket.close();
    }


    public PlayerPropetrtiesUDP getSongPlayer(String category) throws IOException
    {
        PlayerPropetrtiesUDP playerPropetrties = null;

        InputStream is = null;
        FileOutputStream songFos = null;
        FileOutputStream imageFos = null;
        BufferedOutputStream songBos = null;
        BufferedOutputStream imageBos = null;

        ByteArrayOutputStream baos = null;

        SourceDataLine line;
        AudioFormat format;

        clientSocket = new DatagramSocket();

        output = new DataOutputStream(clientSocket.getOutputStream());
        //Ask for a new Song
        try
        {
            output.writeBytes(ClientConfig.CsendMeNewSongString +
                    ClientConfig.messageDivider + category + System.lineSeparator() );
        }
        catch (IOException e)
        {
            System.out.println(this.getClass() + " Can't ask for a song");
            e.printStackTrace();
        }



        BufferedInputStream bis;
        long fileSize;
        int bufferSize = 0;
        String songName, albumName, artistName;
        float sampleRate;
        int sampleSizeInBits, channels = 0;
        boolean signed, bigEndian;
        try
        {
            is = clientSocket.getInputStream();
            bufferSize = clientSocket.getReceiveBufferSize();
            byte[] bytes = new byte[bufferSize];

            bis = new BufferedInputStream(is);

            int count = 0;
            int headerSize;
            byte[] sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];
            byte[] headerBytes;

            /************* Reading song properties headers *************/
            //Read song name size header (int - 32 bit / 4 bytes)
            count += bis.read(sizeBytes, count, ServerConfig.SONG_NAME_SIZE_HEADER_SIZE);
            headerSize = Util.byteArrayToLeInt(sizeBytes);
            headerBytes = new byte[headerSize];
            sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

            //Read song name header
            count += bis.read(headerBytes, 0, headerSize);
            songName = Util.byteArrayToString(headerBytes);
            headerBytes = null;

            //Read album name size header (int - 32 bit / 4 bytes)
            count += bis.read(sizeBytes, 0, ServerConfig.ALBUM_NAME_SIZE_HEADER_SIZE);
            headerSize = Util.byteArrayToLeInt(sizeBytes);
            headerBytes = new byte[headerSize];
            sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

            //Read album name header
            count += bis.read(headerBytes, 0, headerSize);
            albumName = Util.byteArrayToString(headerBytes);
            headerBytes = null;

            //Read artist name size header (int - 32 bit / 4 bytes)
            count += bis.read(sizeBytes, 0, ServerConfig.ARTIST_NAME_SIZE_HEADER_SIZE);
            headerSize = Util.byteArrayToLeInt(sizeBytes);
            headerBytes = new byte[headerSize];
            sizeBytes = new byte[ServerConfig.NUMBER_HEADER_SIZE];

            //Read artist name header
            count += bis.read(headerBytes, 0, headerSize);
            artistName = Util.byteArrayToString(headerBytes);
            headerBytes = null;

            /*************** Reading song file properties headers *****************/
            //Read song file size in bytes
            headerBytes = new byte[ServerConfig.FILE_SIZE_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.FILE_SIZE_HEADER_SIZE);
            fileSize = Util.byteArrayToLong(headerBytes);

            /************* Reading AudioFormat properties headers *************/
            //Read sample rate header (float - 32 bit / 4 bytes )
            headerBytes = new byte[ServerConfig.SAMPLE_RATE_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.SAMPLE_RATE_HEADER_SIZE);
            sampleRate = Util.byteArrayToFloat(headerBytes);

            //Read sample size in bits header (int - 32 bit / 4 bytes)
            headerBytes = new byte[ServerConfig.SAMPLE_SIZE_IN_BITS_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.SAMPLE_SIZE_IN_BITS_HEADER_SIZE);
            sampleSizeInBits = Util.byteArrayToLeInt(headerBytes);

            //Read channels header (int - 32 bit / 4 bytes)
            headerBytes = new byte[ServerConfig.CHANNELS_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.CHANNELS_HEADER_SIZE);
            channels = Util.byteArrayToLeInt(headerBytes);

            //Read signed header (boolean - 1 byte)
            headerBytes = new byte[ServerConfig.SIGNED_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.SIGNED_HEADER_SIZE);
            signed = Util.byteArrayToBoolean(headerBytes);

            //Read BigEndian header (boolean - 1 byte)
            headerBytes = new byte[ServerConfig.BIGENDIAN_HEADER_SIZE];
            count += bis.read(headerBytes, 0, ServerConfig.BIGENDIAN_HEADER_SIZE);
            bigEndian = Util.byteArrayToBoolean(headerBytes);

            format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

            System.out.println("song : "+ songName);
            System.out.println("album : "+ albumName);
            System.out.println("artist : "+ artistName);
            System.out.println("sample rate : "+ sampleRate);
            System.out.println("sample size in bits : "+ sampleSizeInBits);
            System.out.println("channels : "+ channels);
            System.out.println("signed : "+ signed);
            System.out.println("bigEndian : "+ bigEndian);

            //TODO : add genere and image
            playerPropetrties = new PlayerPropetrtiesUDP(clientSocket, bis, format, bufferSize, fileSize, new SongStream(songName, albumName, artistName, category));

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return playerPropetrties;
    }


    public String[] getCategories() throws IOException
    {
        clientSocket = new DatagramSocket();
        //Socket(ServerConfig.serverAddr, ServerConfig.serverPort);

        output = new DataOutputStream(clientSocket.getOutputStream());

        //Ask for a new Song
        try	{
            output.writeBytes(ClientConfig.CsendMeCategoriesString + ClientConfig.messageDivider + System.lineSeparator());
        } catch (IOException e)	{
            System.out.println(this.getClass() + " Can't ask for a song");
            e.printStackTrace();
        }

        InputStream is = null;
        FileOutputStream songFos = null;
        FileOutputStream imageFos = null;
        BufferedOutputStream songBos = null;
        BufferedOutputStream imageBos = null;

        ByteArrayOutputStream baos = null;

        int bufferSize = 0;
        try
        {
            is = clientSocket.getInputStream();
            bufferSize = clientSocket.getReceiveBufferSize();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[bufferSize];
        int count = 0;
        while((count = is.read(bytes)) > 0)
        {
            baos.write(bytes,0,count);
        }

        String categoriesList = new String(baos.toByteArray());

        String[] categoriesArr = categoriesList.split(",");

//		ArrayList<String> categories = new ArrayList<>();
//		for(String s : categoriesArr)
//			categories.add(s);
//
//		return categories;
        return categoriesArr;

    }



    public static int byteArrayToLeInt(byte[] b)
    {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }


    public static void main(String argv[]) throws Exception {
        UDPStreamingClient streamingClient =
                new UDPStreamingClient(new File(ClientConfig.ClientDir));
    }

}