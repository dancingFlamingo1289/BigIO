package bigFiles.files.medias;

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

import bigFiles.files.BigFile;

/**
 * Class representing an audio file (.wav).
 * Allows creating, reading, writing, and playing an audio file composed of sinusoidal sounds. <br>
 * STILL IN DEVELOPMENT...
 */
public class BigAudio extends BigFile {
	/** The audio clip. */
	private Clip clip;
	/** The list of bytes composing the audio. */
	private ByteArrayOutputStream bufferGlobal = new ByteArrayOutputStream();
	/** This constant is used for something. */
	private final int SAMPLE_RATE = 44100;
	/** The audio format. */
	private final AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);

	/**
	 * Constructor for an audio file.
	 * @param folder The folder where the file is located.
	 * @param fileName The file name (without extension).
	 */
	// Elias K.
	public BigAudio(File folder, String fileName) {
		super(folder, fileName, ".wav");
	}

	/**
	 * Constructor for an audio file.
	 * @param fileName The file name (without extension).
	 */
	// Elias K.
	public BigAudio(String fileName) {
		super(fileName, ".wav");
	}

	/**
	 * Adds a sinusoidal frequency to the audio file.
	 * @param freqHz Frequency in hertz
	 * @param durationMs Duration in milliseconds
	 */
	// Elias K.
	public void addFrequency(int freqHz, int durationMs) {
		int nbSamples = (int)((durationMs / 1000.0) * SAMPLE_RATE);
		for (int i = 0; i < nbSamples; i++) {
			byte val = (byte)(Math.sin(2 * Math.PI * freqHz * i / SAMPLE_RATE) * 127);
			getBufferGlobal().write(val);
		}
	}

	/**
	 * Adds silence (zero amplitude) to the audio file.
	 * @param durationMs Duration in milliseconds
	 */
	// Elias K.
	public void addSilence(int durationMs) {
		int nbSamples = (int)((durationMs / 1000.0) * SAMPLE_RATE);
		for (int i = 0; i < nbSamples; i++) {
			getBufferGlobal().write(0);
		}
	}

	/**
	 * Writes the audio file from the accumulated buffer content.
	 * @param args Not used here.
	 */
	// Elias K.
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
	 * Reads the audio file and prepares the Clip.
	 */
	// Elias K.
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
	 * Plays the loaded sound.
	 */
	// Elias K.
	public void play() {
		if (clip != null) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	/**
	 * Stops audio playback.
	 */
	// Elias K.
	public void stop() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}

	/**
	 * Reads, plays, and waits until playback finishes.
	 */
	// Elias K.
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
	 * Physically deletes the file from the file system.
	 *
	 * @return true if the file was successfully deleted, false otherwise.
	 */
	// Elias K.
	public boolean deletePhysicalFile() {
		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				System.out.println("File " + file.getName() + " was physically deleted.");
				// Optionally clear in-memory audio data
				clip.flush();
			} else {
				System.err.println("Failed to physically delete file: " + file.getName());
			}
			return deleted;
		} else {
			System.out.println("File does not exist: " + file.getName());
			return false;
		}
	}

	/**
	 * Returns the global audio buffer.
	 * @return The global audio buffer.
	 */
	// Elias K.
	public ByteArrayOutputStream getBufferGlobal() {
		return bufferGlobal;
	}

	/**
	 * Sets a new global audio buffer.
	 * @param bufferGlobal The new global buffer.
	 */
	// Elias K.
	public void setBufferGlobal(ByteArrayOutputStream bufferGlobal) {
		this.bufferGlobal = bufferGlobal;
	}

	/**
	 * Main method for testing.
	 * @param args Arguments.
	 */
	// Elias K.
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		BigAudio hymn = new BigAudio(new File("Example"), "Banana");

		// Note frequencies (middle octave)
		final int C = 261, D = 293, E = 329, F = 349;
		final int G = 392, A = 440, B = 493;
		final int C2 = 523;

		final int d = 300; // duration of each note in ms

		// Ode to Joy (beginning)
		hymn.addFrequency(E, d);
		hymn.addFrequency(E, d);
		hymn.addFrequency(F, d);
		hymn.addFrequency(G, d);
		hymn.addFrequency(G, d);
		hymn.addFrequency(F, d);
		hymn.addFrequency(E, d);
		hymn.addFrequency(D, d);
		hymn.addFrequency(C, d);
		hymn.addFrequency(C, d);
		hymn.addFrequency(D, d);
		hymn.addFrequency(E, d);
		hymn.addFrequency(E, d);
		hymn.addFrequency(D, d);
		hymn.addFrequency(D, 2 * d);

		for (int i = 0; i < 400; i++) {
			hymn.addFrequency(i, d);
		}

		hymn.write();
		hymn.play();
	}

	@Override
	public void buildIndex() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void append(Object... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
