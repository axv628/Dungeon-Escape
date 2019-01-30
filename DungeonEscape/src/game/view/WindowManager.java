package game.view;

import game.controller.GameServer;
import game.controller.GameClient;

import javax.swing.*;
import java.awt.*;

public class WindowManager{
	
	private Game game;
	
	private Log log;
	private JPanel currentPanel;
	private JPanel newPanel;
	private String gameState;
	private SoundManager soundManager;
	
	private boolean multiplayer = false;
	
	public boolean muted;
	
	public WindowManager(Game game){
		log = new Log(3);
		this.game = game;
		gameState = "Menu";
		currentPanel = new MenuPanel(this);
		game.add(currentPanel);
		game.repaint();
		soundManager = new SoundManager();
		soundManager.playMenuSong();
	}
	
	public void muteSong(){
		if (soundManager.menuSongPlaying){
			soundManager.stopMenuSong();
			muted = true;
		}
	}
	
	public void unmuteSong(){
		soundManager.playMenuSong();
	}
	
	public void changeState(String newState){
		if(newState == gameState){
			//Do nothing
		}else{
			if(newState.equals("MenuPanel")){
				game.resize();
				currentPanel = new MenuPanel(this);
				setMultiplayer(false);
				soundManager.reset();
				if(!soundManager.menuSongPlaying){
					soundManager.playMenuSong();
				}
			}
			else if(newState.equals("Instructions")){
				game.resize();
				currentPanel = new Instructions(this);
			}
			else if(newState.equals("level map")){
				currentPanel = new LevelMap(this, log);	
			}
			else if(newState.equals ("1")){		//////////////RESET SIZE
				if(!muted){
					soundManager.stopMenuSong();
					soundManager.playGameSong();
				}
				currentPanel = new GamePanel("TheWildEscape/src/resources/L1.txt", this);	//for now we're going straight to level 1
			}
			else if(newState.equals ("2")){	
				if(!muted){
					soundManager.stopMenuSong();
					soundManager.playGameSong();
				}
				currentPanel = new GamePanel("TheWildEscape/src/resources/L2.txt", this);	//for now we're going straight to level 1
			}
			else if(newState.equals ("3")){
				if(!muted){
					soundManager.stopMenuSong();
					soundManager.playGameSong();
				}
				currentPanel = new GamePanel("TheWildEscape/src/resources/L3.txt", this);	//for now we're going straight to level 1
			}
			else if(newState.equals("Multiplayer")){
				currentPanel = new MultiplayerMenu(this);
				setMultiplayer(true);
			}
			else if(newState.equals("Join Server")){
				game.resize();
				currentPanel = new JoiningMenu(this);
			}
		}
		game.setContentPane(currentPanel);
		currentPanel.setFocusable(true);
		currentPanel.requestFocus();
		game.repaint();
		game.revalidate();
		
		gameState = newState;
	}
	
	public Game getGame(){
		return game;
	}
	
	public int getWidth(){
		return game.getWidth();
	}
	
	public int getHeight(){
		return game.getHeight();
	}
	
	public void setMultiplayer(boolean multi){
		multiplayer = multi;
	}
	
	public String getGameState(){
		return gameState;
	}
	
	public void levelComplete(){
		int level = Integer.parseInt(getGameState())-1;
		log.levelCompleted(level);
		GamePanel p = (GamePanel)currentPanel;
		p.gameOver("WON", 0);
	}
	
	public void pause(){
		GamePanel p = (GamePanel)currentPanel;
		p.addPausePanel("pause");
	}
	
	public void gameOver(String info){//info = LOST(sp)/WON(mp)/LOST winnerID(mp)
		GamePanel p = (GamePanel)currentPanel;
		int mode=0;
		
		if (multiplayer){
			mode=1;
		}
		p.gameOver(info, mode);
	}
	
	public void hostServer(String newState, String username){
		game.resize();
		currentPanel = new HostingMenu(this, username);
		game.setContentPane(currentPanel);
		currentPanel.setFocusable(true);
		currentPanel.requestFocus();
		game.repaint();
		game.revalidate();
		gameState = newState;
	}
	
	public void goToHostingMenu(String username){
		game.getContentPane().remove(currentPanel);
		game.resize();
		currentPanel = new HostingMenu(this, username);
		game.setContentPane(currentPanel);
		currentPanel.setFocusable(true);
		currentPanel.requestFocus();
		game.repaint();
		game.revalidate();
		
		gameState = "Hosting Menu";
	}
	
	public void startGameWithServer(GameServer server, GameClient client){
		while(!client.isGameStarted()){
			try{
				Thread.sleep(200);
			}catch(InterruptedException e){
				
			}
		}
		game.getContentPane().remove(currentPanel);
		currentPanel = new GamePanel("TheWildEscape/src/resources/L2M.txt", this, server, client);
		game.setContentPane(currentPanel);
		currentPanel.setFocusable(true);
		currentPanel.requestFocus();
		game.repaint();
		game.revalidate();
		if(!muted){
			soundManager.stopMenuSong();
			soundManager.playGameSong();
		}
		gameState = "Hosting Server";
	}
	
	
	public void startGameAsClient(GameClient client){
		while(!client.isGameStarted()){
			try{
				Thread.sleep(200);
			}catch(InterruptedException e){
				
			}
		}
		game.getContentPane().remove(currentPanel);
		currentPanel = new GamePanel("TheWildEscape/src/resources/L2M.txt", this, client);
		game.setContentPane(currentPanel);
		currentPanel.setFocusable(true);
		currentPanel.requestFocus();
		game.repaint();
		game.revalidate();
		if(!muted){
			soundManager.stopMenuSong();
			soundManager.playGameSong();
		}
	}
	 
	public void lobbyRepaint(){
		if(gameState.equals("Hosting Menu") || gameState.equals("Join Server")){
			game.invalidate();
			game.validate();
			game.repaint();
			System.out.println("Lobby repaint");
		}
	}
}