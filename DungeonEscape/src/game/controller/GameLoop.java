package game.controller;

import game.view.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
*A Thread that loops round updating and rendering the game state with a Fixed Timestep approach.
*/
public class GameLoop extends Thread{
	
	private GamePanel gamePanel;
	private GameEngine engine;
	private AtomicBoolean paused;
	
	private boolean running;
	private int frameCount;
	private int fps = 60;
	private int gameTime;
	
	/**
	*Creates a game loop, with access to the game panel, game engine and whether the game is paused.
	*@param gamePanel The current game window thing
	*@param engine This commands the game state and renderer
	*@param paused Indicates whether the game is paused
	*/
	public GameLoop(GamePanel gamePanel, GameEngine engine, AtomicBoolean paused){
		this.gamePanel = gamePanel;
		this.engine = engine;
		this.paused=paused;
		running = true;
		start();
	}
	
	/**
	*Sets whether the game is paused.
	*@param tf Boolean indicating whether to pause the game or unpause it.
	*/
	public void setPaused(boolean tf){
		paused.set(tf);
	}
	
	/**
	*Gets the current time in the game.
	*@return The time in the game.
	*/
	public int getTime(){
		return gameTime;
	}
	
	/**
	*Called when the Thread starts.
	*Loops until the game is no longer running, updating and rendering the game with a fixed timestep approach
	*/
	public void run(){
		final double GAME_HERTZ = 30.0;
		
		final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
		
		final int 	MAX_UPDATES_BEFORE_RENDER = 5;
		
		double lastUpdateTime = System.nanoTime();
		double lastRenderTime = System.nanoTime();
		
		final double TARGET_FPS = 60;
		final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;
		
		int lastSecondTime = (int)(lastUpdateTime / 1000000000);
		gameTime = gamePanel.getGameTime();
		
		while(running){
			double now = System.nanoTime();
			int updateCount = 0;
			
			while(now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER){
				//updates the game
				engine.update();
				lastUpdateTime += TIME_BETWEEN_UPDATES;
				updateCount++;
			}
			
			//does something
			if(now - lastUpdateTime > TIME_BETWEEN_UPDATES){
				lastUpdateTime = now - TIME_BETWEEN_UPDATES;
			}
			
			//rendering
			float interpolation = Math.min(1.0f, (float)((now - lastUpdateTime)/TIME_BETWEEN_UPDATES) );
			lastRenderTime = now;
			gamePanel.drawGame(interpolation);
			frameCount++;
			
			//frame counting
			int thisSecond = (int) (lastUpdateTime / 1000000000);
			if(thisSecond > lastSecondTime){
				gameTime--;
				System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
				fps = frameCount;
				frameCount = 0;
				lastSecondTime = thisSecond;
				engine.timeUpdate(gameTime);
			}
			
			//yield for some time.
			while(now -lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES){
				Thread.yield();
				try{
					Thread.sleep(1);
				}catch(Exception e){}
				now = System.nanoTime();
			}
			
			while(paused.get()){
				try{
					Thread.sleep(500);
				}catch(InterruptedException e) {
					continue;
				}
			}
		}
	}
}