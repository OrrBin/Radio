package utilities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.TimeZone;

import tit.configuration.ClientConfig;
import tit.configuration.ServerConfig;

public class Util
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
	 * @param i
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
	 * @param i
	 * @return
	 */
	public static byte[] LongToByteArray(long l)
	{
		final ByteBuffer bb = ByteBuffer.allocate(ServerConfig.LONG_NUMBER_HEADER_SIZE);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putLong(l);
		return bb.array();
	}

	/**
	 * returns byte array with {ServerConfig.BOOLEAN_HEADER_SIZE} cells,
	 * that holds the boolean
	 * @param i
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

	/**
	 * returns byte array with{ServerConfig.NUMBER_HEADER_SIZE} cells,
	 * that holds the String bytes array size
	 * @param i
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
	 * @param i
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
		final ByteBuffer bb = ByteBuffer.wrap(b);
		bb.order(ByteOrder.LITTLE_ENDIAN);
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

	public static String convertSecondsToStringTime(int totalSeconds)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		
		cal.add(Calendar.SECOND, totalSeconds);
		
		int hours = cal.get(Calendar.HOUR);
		int minutes = cal.get(Calendar.MINUTE);
		int seconds = cal.get(Calendar.SECOND);
		StringBuffer sb = new StringBuffer();

		if(hours > 0)
		{
			if (hours < 10)
				sb.append("0");
			sb.append(String.valueOf(hours)).append(":");
		}

		if(minutes < 10)
			sb.append("0");
		sb.append(String.valueOf(minutes)).append(":");

		if(seconds < 10)
			sb.append("0");
		sb.append(String.valueOf(seconds));

		return sb.toString();

	}
	
	public static String clientMessage(String... parts) {
		return String.join(ClientConfig.messageDivider, parts).concat("\n");
	}
}
