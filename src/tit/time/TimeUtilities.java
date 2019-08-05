package tit.time;

import tit.configuration.TimeConfig;

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
			display.append(String.valueOf(hours)).append(TimeConfig.displayTimeRegexp);
		if(minutes < 10)
			display.append("0");
		display.append(String.valueOf(minutes)).append(TimeConfig.displayTimeRegexp);
		if(secondsNumber < 10)
			display.append("0");
		display.append(String.valueOf(secondsNumber));
		
		return display.toString();
		
		
		
				
	}
}
