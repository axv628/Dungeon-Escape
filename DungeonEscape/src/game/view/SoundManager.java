package game.view;

import java.io.File;


import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


/**
 * The class that contains the methods for playing audio
 */

public class SoundManager {
	
	private Clip fullsong;
	private AudioInputStream audioIn = null;
	public boolean menuSongPlaying;
	private Clip gameSong;
	private boolean gameSongPlaying;
	
	public SoundManager(){
	}
	
	public void playMenuSong(){
		try {
			if(!menuSongPlaying){
				audioIn = AudioSystem.getAudioInputStream(new File("TheWildEscape/src/resources/audio/FullSong.wav").getAbsoluteFile());
				fullsong = AudioSystem.getClip();
				fullsong.open(audioIn);
				fullsong.loop(10);
				menuSongPlaying = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	public void stopMenuSong(){
		fullsong.stop();
		menuSongPlaying = false;
	}
	/**
	 * called at the beginning of a game
	 */
	public void playGameSong(){
		try {
			if(!menuSongPlaying && !gameSongPlaying){
				audioIn = AudioSystem.getAudioInputStream(new File("TheWildEscape/src/resources/audio/Section3-4.wav").getAbsoluteFile());
				gameSong = AudioSystem.getClip();
				gameSong.open(audioIn);
				gameSong.loop(10);
				gameSongPlaying = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	public void playSoundEffect(){
		try{
			audioIn = AudioSystem.getAudioInputStream(new File("TheWildEscape/src/resources/audio/soundEffect.wav").getAbsoluteFile());
			Clip sound = AudioSystem.getClip();
			sound.open(audioIn);
			sound.start();
		}catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	/**
	 * plays the sound effect for getting hit by an enemy
	 */

	public void playOuch(){
		try{
			audioIn = AudioSystem.getAudioInputStream(new File("TheWildEscape/src/resources/audio/ouch.wav").getAbsoluteFile());
			Clip sound = AudioSystem.getClip();
			sound.open(audioIn);
			sound.start();
		}catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	/**
	 * Stops the game song if it's playing
	 */
	
	public void reset() {
		if(gameSongPlaying){
			gameSong.stop();
			gameSongPlaying = false;
		}
		
	}


}
