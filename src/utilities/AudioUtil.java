package utilities;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioUtil {

	static public AudioFormat getFormat(File songFile) throws UnsupportedAudioFileException, IOException {
		AudioInputStream in= AudioSystem.getAudioInputStream(songFile);
		AudioFormat baseFormat = in.getFormat();
		AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				baseFormat.getSampleRate(),
				16,
				baseFormat.getChannels(),
				baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(),
				false);
		return decodedFormat;
	}
	
	static public AudioInputStream getFAudioInputStream(File songFile) throws UnsupportedAudioFileException, IOException {
		AudioInputStream in= AudioSystem.getAudioInputStream(songFile);
		AudioFormat baseFormat = in.getFormat();
		AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				baseFormat.getSampleRate(),
				16,
				baseFormat.getChannels(),
				baseFormat.getChannels() * 2,
				baseFormat.getSampleRate(),
				false);
		return  AudioSystem.getAudioInputStream(decodedFormat, in);
	}
}
