package game.controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics2D;

import game.model.*;
import game.view.*;
import game.model.objects.Player;

/**
*Handles keypresses, updates and renders the game world.
*/
public class GameEngine{

	private World gameMap;
	
	private GameController controller;
	private GameRenderer renderer;
	
	private GameKeyListener keyListener;
	
	private WindowManager manager;
	
	/**
	*Creates a game engine with the level file path of the world to be rendered and updated
	*/
	public GameEngine(String levelFilePath){
		gameMap = new World(levelFilePath);
		controller = new GameController(gameMap, this);
	}
	
	/**
	*Creates a game engine with the world which will be updated and rendered
	*/
	public GameEngine(World world){
		gameMap = world;
		controller = new GameController(gameMap, this);
	}
	
	/**
	*Creates a game engine with the world which will be updated and rendered, and the window manager that controls the view
	*/
	public GameEngine(World world, WindowManager manager){
		this.manager = manager;
		
		gameMap = world;
		controller = new GameController(gameMap, this);
		renderer = new GameRenderer(gameMap);
	}
	
	/**
	*Creates a game engine with the level file path of the world to be created for the game, and the manager that controls the view
	*/
	public GameEngine(String levelFilePath, WindowManager manager){
		this.manager = manager;
		
		gameMap = new World(levelFilePath);
		
		controller = new GameController(gameMap, this);
		renderer = new GameRenderer(gameMap);
		
		keyListener = new GameKeyListener();//adds own implementation of keylistener interface
	}
	
	/**
	*Gets the keyListener for registering user input
	*@return A key listener that sends commands to the GameController stored here
	*/
	public GameKeyListener getKeyListener(){//get method for game panel
		return keyListener;
	}
	
	/**
	*Handles key presses for the game
	*/
	public class GameKeyListener implements KeyListener{
		
		/**
		*Called on a keypress and handles
		*/
		@Override
		public void keyPressed(KeyEvent e) {
			int id = e.getKeyCode();
			if (id == KeyEvent.VK_SPACE){
				controller.jump();
			}
			if (id == KeyEvent.VK_A){
				controller.left();
			}
			if (id == KeyEvent.VK_D){
				controller.right();
			}
			if (id == KeyEvent.VK_W){
				controller.upClimb();
			}
			if (id == KeyEvent.VK_S){
				controller.downLadder();
			}
			if (id == KeyEvent.VK_U){
				
			}
			if (id == KeyEvent.VK_P){
				manager.pause();
			}
		}

		/**
		*Called when a key is released
		*/
		@Override
		public void keyReleased(KeyEvent e) {
			int id = e.getKeyCode();
		
			if (id == KeyEvent.VK_A || id == KeyEvent.VK_D || id == KeyEvent.VK_W || id == KeyEvent.VK_S){
				controller.stop();
			}
		}

		/**
		*Placeholder
		*/
		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
	}
	
	/**
	*Tells the controller to update the game state
	*/
	public void update(){
		//updates all the game logic and positions.
		controller.update();
	}
	
	/**
	*Updates the timer in the game
	*@param time The new time to be discplayed
	*/
	public void timeUpdate(int time){//called from gameloop in singleplayer
		String t = Integer.toString(time);
		controller.timeUpdate(t);
		if (time == 0){
			if (gameMap.getAlivePlayers().size() > 1){
				if (gameMap.getLeader().equals(gameMap.getThisClientID())){
					gameOver("WON");
				}
				else gameOver("LOST "+gameMap.getLeader());
			}
			else gameOver("LOST");
		}
	}
	
	/**
	*Tells the renderer to render the game
	*@param g The current graphics context
	*@param interpolation The time passed since the last time update() was called
	*/
	public void render(Graphics g, float interpolation){
		//renders the world
		renderer.render(g, interpolation);
	}
	
	/**
	*Returns the game state controller
	*@return The game state controller
	*/
	public GameController getController(){
		return controller;
	}
	
	/**
	*Tells the manager that the single-player level has been complete
	*/
	public void levelComplete(){
		manager.levelComplete();
	}
	
	/**
	*Called when the game is over
	*/
	public void gameOver(String info){//info = LOST/winnerID
		String id = gameMap.getThisClientID();
		if (info.equals("LOST")){//loses/dies in sp
			manager.gameOver(info);
		}
		else if (info.equals("DIED")){
			if (gameMap.getAlivePlayers().size()>1 || id.equals("SinglePlayer"))
				manager.gameOver(info);
		}
		else if (id.equals(info)){
			manager.gameOver("WON");
		}
		else manager.gameOver("LOST " + info);
	}
}