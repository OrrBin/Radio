package radio.core.utilities;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.TimeZone;

import radio.client.ClientConfig;
import radio.server.ServerConfig;

// Making types to byte arrays for sending or the opposite way
public class BytesUtil
{
	/**
	 * returns byte array with {ServerConfig.NUMBER_HEADER_SIZE} cells,
	 * that holds the int
	 * @param i
	 * @return
	 */
	public static byte[] leIntToByteArray(int i)
	{
		final ByteBuffer bb = ByteBuffer.allocate(ServerConfig.NUMBER_HEADER_SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(i);
		return bb.array();
	}	

	/**
	 * returns byte array with {ServerConfig.NUMBER_HEADER_SIZE} cells,
	 * that holds the float
	 * @return
	 */
	public static byte[] FloatToByteArray(float f)
	{
		final ByteBuffer bb = ByteBuffer.allocate(ServerConfig.NUMBER_HEADER_SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putFloat(f);
		return bb.array();
	}

	/**
	 * returns byte array with {ServerConfig.LONG_NUMBER_HEADER_SIZE} cells,
	 * that holds the float
	 * @return
	 */
	public static byte[] LongToByteArray(long l)
	{
		final ByteBuffer bb = ByteBuffer.allocate(ServerConfig.LONG_NUMBER_HEADER_SIZE);
		bb.putLong(l);
		return bb.array();
	}

	/**
	 * returns byte array with {ServerConfig.BOOLEAN_HEADER_SIZE} cells,
	 * that holds the boolean
	 * @return
	 */
	public static byte[] booleanToByteArray(boolean b)
	{
		final ByteBuffer bb = ByteBuffer.allocate(ServerConfig.BOOLEAN_HEADER_SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int bolInt;
		if(b)
		{
			bolInt = 1; 
		}
		else
		{
			bolInt = 0; 
		}

		bb.putInt(bolInt);
		return bb.array();
	}

	public static byte[] longToByteArray(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

	/**
	 * returns byte array with{ServerConfig.NUMBER_HEADER_SIZE} cells,
	 * that holds the String bytes array size
	 * @return
	 */
	public static byte[] getStringSizeInBytes(String s)
	{
		int length =  s.getBytes().length;
		final ByteBuffer bb = ByteBuffer.allocate(ServerConfig.NUMBER_HEADER_SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(length);
		return bb.array();
	}	

	/**
	 * returns byte array with dynamic number of cells,
	 * that holds the String
	 * @return
	 */
	public static byte[] StringToByteArray(String s)
	{
		return s.getBytes();
	}	

	public static int byteArrayToLeInt(byte[] b) 
	{
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	public static float byteArrayToFloat(byte[] b) 
	{
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}

	public static long byteArrayToLong(byte[] b) 
	{
		final ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
		bb.put(b);
		bb.flip();
		return bb.getLong();
	}

	public static boolean byteArrayToBoolean(byte b[]) 
	{
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		int bolInt = bb.getInt();

		switch (bolInt)
		{
		case 1: return true;
		case 0: return false;
		default: System.out.println("In utiliteis.Util : byte value isnt 1 or 0 : returning default value : true"); return true;
		}
	}

	public static String byteArrayToString(byte[] b) 
	{
		return new String(b, Charset.forName(ServerConfig.DEFAULT_CHARSET_NAME));
	}




	public static String clientMessage(String... parts) {
		return String.join(ClientConfig.messageDivider, parts).concat("\n");
	}

	public static File chooseRandomSong(String dirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		if (files.length != 0) {
			return files[(int) ((files.length - 1) * Math.random())];
		}
		else return null;
	}
}
