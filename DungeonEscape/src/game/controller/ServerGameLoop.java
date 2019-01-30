package game.controller;

import java.util.concurrent.atomic.AtomicBoolean;

/**
*A Thread that loops round updating and rendering the server's game state with a Fixed Timestep approach.
*Also periodically tells the server to update the client's game state
*/
public class ServerGameLoop extends Thread{
	
	private GameEngine engine;
	private GameServer server;
	
	private boolean running;
	private int frameCount;
	private int fps = 60;
	
	/**
	*Creates a new game loop for the server, with access to the game's engine and the server itself
	*@param engine This controls the game's state
	*@param server This communicates the game state with the clients
	*/
	public ServerGameLoop(GameEngine engine, GameServer server){
		this.engine = engine;
		this.server = server;
		running = true;
		start();
	}
	
	/**
	*Sets running to false, ending the game loop
	*/
	public void end(){
		running = false;
	}
	
	/**
	*Called when the Thread starts.
	*Loops until the game is no longer running, updating the server machine's internal game state, and seding updates to the clients
	*/
	public void run(){
		final double GAME_HERTZ = 30.0;
		
		final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
		
		final int 	MAX_UPDATES_BEFORE_MESSAGE = 15;
		
		double lastUpdateTime = System.nanoTime();
		double lastRenderTime = System.nanoTime();
		
		final double TARGET_SERVER_UPDATES_PER_SECOND = 15;
		final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_SERVER_UPDATES_PER_SECOND;
		
		int lastSecondTime = (int)(lastUpdateTime / 1000000000);
		int gameTime = 120;
		
		while(running){
			double now = System.nanoTime();
			int updateCount = 0;
			
			while(now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_MESSAGE){
				//updates the game
				engine.update();
				lastUpdateTime += TIME_BETWEEN_UPDATES;
				updateCount++;
			}
			
			//does something
			if(now - lastUpdateTime > TIME_BETWEEN_UPDATES){
				lastUpdateTime = now - TIME_BETWEEN_UPDATES;
			}
			
			if(now - lastRenderTime > TARGET_TIME_BETWEEN_RENDERS){
				lastRenderTime = now;
				server.update();
				frameCount++;
			}
			
			//frame counting
			int thisSecond = (int) (lastUpdateTime / 1000000000);
			if(thisSecond > lastSecondTime){
				lastSecondTime = thisSecond;
				gameTime--;
				//System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
				fps = frameCount;
				frameCount = 0;
				
				//Send time update.
				server.timeUpdate(gameTime);
			}
			
			//yield for some time.
			while(now -lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES){
				Thread.yield();
				try{
					Thread.sleep(1);
				}catch(Exception e){}
				now = System.nanoTime();
			}
		}
	}
}