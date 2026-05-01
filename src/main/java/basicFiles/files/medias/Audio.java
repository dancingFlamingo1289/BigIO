package basicFiles.files.medias;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import basicFiles.files.BaseFile;

/**
 * Class representing an audio file (specifically in .wav format).
 * This class provides functionality to synthesize sinusoidal sounds, 
 * manage an audio buffer, and handle playback using the Java Sound API.
 * <p>
 * <b>Note:</b> This component is currently in development.
 * </p>
 * 
 * @author Elias Kassas
 */
public class Audio extends BaseFile {
    /** The runtime {@link Clip} used for audio playback. */
	private Clip clip;

	/** In-memory buffer storing the accumulated raw byte data of the audio. */
	private ByteArrayOutputStream bufferGlobal = new ByteArrayOutputStream();

	/** The sampling rate for the audio, set to 44,100 Hz (CD quality). */
	private final int SAMPLE_RATE = 44100;

	/** The {@link AudioFormat} defining the encoding (8-bit, mono, signed, little-endian). */
	private final AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);

	/**
	 * Constructs an audio file within a specific folder.
     * 
	 * @param folder   The parent directory.
	 * @param fileName The name of the file (extension .wav is applied automatically).
	 */
	public Audio(File folder, String fileName) {
		super(folder, fileName, ".wav");
	}

	/**
	 * Constructs an audio file in the current working directory.
     * 
	 * @param fileName The name of the file.
	 */
	public Audio(String fileName) {
		super(fileName, ".wav");
	}

	/**
	 * Synthesizes a sinusoidal wave at a specific frequency and adds it to the buffer.
	 * 
	 * @param freqHz     The frequency in Hertz (Hz).
	 * @param durationMs The duration of the sound in milliseconds (ms).
	 */
	public void addFrequency(int freqHz, int durationMs) {
		int nbSamples = (int)((durationMs / 1000.0) * SAMPLE_RATE);
		for (int i = 0; i < nbSamples; i++) {
			byte val = (byte)(Math.sin(2 * Math.PI * freqHz * i / SAMPLE_RATE) * 127);
			getBufferGlobal().write(val);
		}
	}

	/**
	 * Adds a period of silence (zero amplitude) to the audio buffer.
	 * 
	 * @param durationMs The duration of the silence in milliseconds (ms).
	 */
	public void addSilence(int durationMs) {
		int nbSamples = (int)((durationMs / 1000.0) * SAMPLE_RATE);
		for (int i = 0; i < nbSamples; i++) {
			getBufferGlobal().write(0);
		}
	}

	/**
	 * Exports the accumulated buffer data into a physical WAVE file on disk.
	 * 
	 * @param args Ignored.
	 */
	@Override
	public void write(Object... args) {
		byte[] audioData = getBufferGlobal().toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
		AudioInputStream stream = new AudioInputStream(bais, format, audioData.length);

		try {
			AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
		} catch (IOException e) {
			System.err.println("Audio writing error: " + e.getMessage());
		}
	}

	/**
	 * Reads the audio file from disk and initializes the playback {@link Clip}.
	 */
	@Override
	public void read() {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
			clip = AudioSystem.getClip();
			clip.open(audioIn);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			System.err.println("Audio reading error: " + e.getMessage());
		}
	}

	/**
	 * Starts playing the loaded audio from the beginning.
	 */
	public void play() {
		if (clip != null) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	/**
	 * Stops the current playback if the audio is running.
	 */
	public void stop() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}

	/**
	 * Reads the file, starts playback, and blocks the current thread until the audio finishes.
	 */
	public void readPlayWait() {
		read();
		play();
		try {
			while (clip != null && clip.isRunning()) {
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Physically deletes the file from the filesystem and flushes the playback clip.
	 * 
	 * @return {@code true} if successfully deleted; {@code false} otherwise.
	 */
	public boolean deletePhysicalFile() {
		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				System.out.println("File " + file.getName() + " was physically deleted.");
				if (clip != null) clip.flush();
			}
			return deleted;
		}
		return false;
	}

	/**
	 * Retrieves the global audio buffer.
	 * 
	 * @return The current {@link ByteArrayOutputStream}.
	 */
	public ByteArrayOutputStream getBufferGlobal() {
		return bufferGlobal;
	}

	/**
	 * Replaces the current audio buffer with a new one.
	 * 
	 * @param bufferGlobal The new audio buffer.
	 */
	public void setBufferGlobal(ByteArrayOutputStream bufferGlobal) {
		this.bufferGlobal = bufferGlobal;
	}

	/**
     * Appending data directly to the end of a WAVE file is not supported 
     * due to header requirements.
     * 
	 * @throws IllegalArgumentException Always thrown.
	 */
	@Override
	public void append(Object... args) throws Exception {
		throw new IllegalArgumentException("Impossible to call append on an audio.");
	}
}