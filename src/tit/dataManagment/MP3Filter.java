package tit.dataManagment;

import java.io.File;
import java.io.FilenameFilter;

public class MP3Filter implements FilenameFilter
{

	@Override
	public boolean accept(File dir, String name) 
	{
		if (name.endsWith(".mp3")) 
		{
			// filters files whose extension is .mp3
			return true;
		}
		else 
		{
			return false;
		}
	}

}
