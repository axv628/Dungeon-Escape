package game.view;

import java.awt.image.BufferedImage;

public class Animation{
	
	private BufferedImage[] frames;
	private int currentFrame;
	
	private long startTime;
	private long delay;
	
	/**
	* Manages timing of flipping through the relevant player sprites to create animation and generates current sprite for the player
	*/
	
	public void Animation(){}
	
	/**
	* Sets sprite frames to correct 'action' to be animated. eg. running/jumping/climbing
	* @param frames array of BufferedImages of sprites which will make up the animation
	*/
	
	public void setFrames(BufferedImage[] frames){
		this.frames = frames;
		currentFrame = 0;
		startTime = System.nanoTime();
	}
	
	/**
	* Set delay before switching between each frame, determining 'speed' of animation
	* @param time of delay in miliseconds
	*/
	
	public void setDelay(long delay){
		this.delay = delay;
	}
	
	/**
	* Sets current frame in animation by referenced the relevant index in the BufferedImage array that has been set
	* @param n index of frame in 'frames'
	*/
	
	public void setFrame(int n){
		currentFrame = n;
	}
	
	/**
	* Called from player to update current frame in animation
	*/
	
	public void update(){
		if (delay == -1){
			return;
		}

		long elapsed = (System.nanoTime() - startTime)/1000000;
		if (elapsed > delay){
			currentFrame++;
			startTime = System.nanoTime();
		}
			
		if (currentFrame == frames.length){
			currentFrame = 0;
		}
	}
	
	/**
	* @return index of current frame in animation for player
	*/
	
	public int getFrame(){
		return currentFrame;
	}
	
	/**
	* @return current image in animation for player
	*/
	
	public BufferedImage getImage(){
		return frames[currentFrame];
	}
	
	
}