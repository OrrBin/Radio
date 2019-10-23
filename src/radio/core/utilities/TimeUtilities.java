package radio.core.utilities;

import java.util.Calendar;
import java.util.TimeZone;

public class TimeUtilities
{
	public static String secondsToString(double seconds)
	{
		int secondsInt = (int) seconds;
		int hours = secondsInt / 3600;
		int minutes = (secondsInt - hours*3600) / 60;
		int secondsNumber = secondsInt - hours*3600 - minutes*60;
		StringBuffer display = new StringBuffer();
		if(hours > 0)
			display.append(String.valueOf(hours)).append(":");
		if(minutes < 10)
			display.append("0");
		display.append(String.valueOf(minutes)).append(":");
		if(secondsNumber < 10)
			display.append("0");
		display.append(String.valueOf(secondsNumber));

		return display.toString();
				
	}

	// TODO: whats the difference ?!?!
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
}
