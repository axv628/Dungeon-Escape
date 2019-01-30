package game.view;

import game.controller.*;
import java.awt.Graphics2D;
import java.awt.image.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamePanel extends JPanel{
	public static final int width = 960;
	public static final int height = 720;
	
	private GameEngine engine;
	private GameLoop gameLoop;
	
	private GameClient client;
	private GameServer server;
	
	private boolean running;
	private int frameCount;
	private int fps = 60;
	private String levelFileName;
	private KeyListener keyListener;
	private PauseMenu pause;
	private EndMenu endMenu;
	private AtomicBoolean paused;
	
	private WindowManager manager;
	private float interpolation;
	
	
	
	public GamePanel(String levelFileName, WindowManager manager, GameServer server, GameClient client){
		this.levelFileName = levelFileName;
		this.manager = manager;
		
		this.server = server;
		this.client = client;
		
		
		
		paused = new AtomicBoolean(false);
		manager.getGame().setSize(width,height);
		manager.getGame().setResizable(false);
		this.setVisible(true);
		running=true;
		
		frameCount = 0;
		setFocusTraversalKeysEnabled(false);

		engine = new GameEngine(client.getWorld(), manager);
		client.addController(engine.getController());
		addKeyListener(new ClientKeyListener(client));
		
		server.startGameLoop();
		runGameLoop();
	}
	
	public GamePanel(String levelFileName, WindowManager manager, GameClient client){
		this.levelFileName = levelFileName;
		this.manager = manager;
		this.client = client;
		
		paused = new AtomicBoolean(false);
		manager.getGame().setSize(width, height);
		manager.getGame().setResizable(false);
		this.setVisible(true);
		running = true;
		setFocusTraversalKeysEnabled(false);

		frameCount = 0;
		engine = new GameEngine(client.getWorld(), manager);
		client.addController(engine.getController());
		addKeyListener(new ClientKeyListener(client));
		
		runGameLoop();
	}
	
	public GamePanel(String levelFileName, WindowManager manager){
		this.levelFileName = levelFileName;
		this.manager = manager;
		
		paused = new AtomicBoolean(false);
		manager.getGame().setSize(width, height);
		manager.getGame().setResizable(false);
		this.setVisible(true);
		running=true;
		
		frameCount = 0;
		engine = new GameEngine(levelFileName, manager);
		
		setFocusTraversalKeysEnabled(false);
		
		this.addKeyListener(engine.getKeyListener());
		
		runGameLoop();
	}
	
	/**
	* Runs game loop.
	*/
	
	public void runGameLoop(){
		gameLoop = new GameLoop(this, engine, paused);	
	}
	
	/**
	* Renders game.
	*/
	
	public void paintComponent(Graphics g){
		engine.render(g, interpolation);
	}
	
	/**
	* @return Level chosen.
	*/
	
	public int getLevel(){
		if (levelFileName.contains("1") && !levelFileName.contains("1M")){
			return 1;
		} else if (levelFileName.contains("2")&& !levelFileName.contains("2M")){
			return 2;
		} else if (levelFileName.contains("3")&& !levelFileName.contains("3M")){
			return 3;
		} return 0;
	}
	
	/**
	* @return Time left in game.
	*/
	
	public int getGameTime(){
		int level = getLevel();
		if(level == 1){
			return 120;
		} else if (level == 2){
			return 140;
		} else if (level == 3){ 
			return 220;
		} return 120;
	}
	
	/**
	* Updates interpolation and repaints panel.
	*/
	
	public void drawGame(float interpolation){
		this.interpolation = interpolation;
		repaint();
	}
	
	/**
	* Sets whether game loop is running.
	* @param tf boolean whether game is running.
	*/
	
	public void setRunning(boolean tf){
		running=tf;
	}
	
	/**
	* Paints pause menu over game panel.
	* @param type Title for pause menu.
	*/
	
	public void addPausePanel(String type){
		pause = new PauseMenu(manager.getGame(), type, this);
		repaint();
		revalidate();
	}
	
	/**
	* Generate game end menu.
	* @param info Information on outcome of game.
	* @param mode Game mode.
	*/
	
	public void gameOver(String info, int mode){
		boolean playersLeft = false;
		endMenu = new EndMenu(manager.getGame(), info, mode, this);
		running = false;
	}
	
	/**
	* Used in pausing the gameloop for the pause menu. Sets thread to sleep.
	*/
	
	public void threadPause(){
		while(paused.get()){
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e) {
				continue;
			}
		}
	}
	
	/**
	* Sets whether the game is paused.
	* @param tf boolean whether paused
	*/

	public void setPaused(boolean tf){
		paused.set(tf);
	}
	
	/**
	* For interrupting thread from sleep.
	*/
	
	public void threadInterrupt(){
		gameLoop.interrupt();
	}
}