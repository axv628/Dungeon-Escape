package game.view;

import game.model.*;
import game.model.objects.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.RadialGradientPaint;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;

public class GameRenderer{
	
	private World world;
	private boolean singleplayer;
	private TileMap tileMap;
	private BufferedImage imagePlayer;
	private BufferedImage imageDoor;
	private BufferedImage bg;
	private Graphics g;
	private static final int width = 960;
	private static final int height = 720;
	private ConcurrentHashMap<String, Player> playerList;
	private ArrayList<String> otherPlayers;
	private Player p;
	private float xp;
	private float yp;
	private float xv;
	private float yv;
	private float interpolation;
	
	/**
	* Retrieves all objects to be drawn from world.
	* Checks whether the game is in singleplayer or multiplayer mode.
	* @param world World object.
	*/
	
	public GameRenderer(World world){
		this.world = world;
		
		if (world.getThisClientID().equals("SinglePlayer")){
			singleplayer = true;
		}
		
		this.tileMap = world.getTileMap();
		this.p = world.getPlayer();
		this.playerList = world.getPlayerList();
		
		otherPlayers = new ArrayList<String>();
		
		for (String s : playerList.keySet()){
			if (s != null){
			otherPlayers.add(s);}
		}
		otherPlayers.remove(p.getClientID());
	}
	
	/**
	* Called from game loop, renders everything on the screen.
	* @param g Graphics
	* @param interpolation
	*/
	
	public void render(Graphics g, float interpolation){
		this.g = g;
		this.interpolation = interpolation;
		getPositions();
		renderMap();
		if (singleplayer){
			renderDoor();
		}
		if (singleplayer){
			renderEnemy();
		}
		renderPlayer();
		if (!singleplayer){
			renderplayerList();
		}
		renderFeedback();
		if(world.getLeaderBoard()){
			renderLeaderBoard();
		}
		
	}
	
	/**
	* Renders the background.
	* Renders the map by calling the 'draw' method in TileMap.
	*/
	
	private void renderMap(){
		bg = tileMap.getBackground();
		bg = bg.getSubimage(0,0, width, height);
		g.drawImage(bg, 0,0, null);
		tileMap.draw(g, xp, yp, xv, yv, interpolation);
	}
	
	/**
	* Renders the world's player.
	* Also renders the player's ID if in multiplayer.
	*/
	
	private void renderPlayer(){
		int x = (int)xp + (int)tileMap.getx() + (int)(xv*interpolation);
		int y = (int)yp + (int)tileMap.gety() + (int)(yv*interpolation);
		imagePlayer = p.getImage();
		g.drawImage(imagePlayer, x, y, null);
		if(!singleplayer){
			Color color= g.getColor();
			g.setColor(Color.WHITE);
			g.drawString(p.getClientID(),(x+16)-(3*p.getClientID().length()), y-10);
			g.setColor(color);
		}
	}
	
	/**
	* Renders other players in world and their IDs.
	*/
	
	private void renderplayerList(){
		for (int i=0; i<otherPlayers.size(); i++){
			Player op = world.getPlayer(otherPlayers.get(i).toString());
			if(world.isPlayerAlive(otherPlayers.get(i).toString())){
				int x = (int)op.getXPos() + (int)tileMap.getx() + (int)(op.getXVel()*interpolation);
				int y = (int)op.getYPos() + (int)tileMap.gety() + (int)(op.getYVel()*interpolation);
				BufferedImage im = op.getImage();
				g.drawImage(im, x, y, null);
				if(!singleplayer){
					Color color= g.getColor();
					g.setColor(Color.WHITE);
					g.drawString(op.getClientID(), (x+16)-(3*op.getClientID().length()), y-10);
					g.setColor(color);
				}
			}
		}
	}

	/**
	* Renders door, only called in singleplayer mode.
	*/

	private void renderDoor(){	
		Door door = world.getDoor();
		int x = (int)door.getXPos();
		int y = (int)door.getYPos();
		imageDoor = door.getSprite();
		g.drawImage(imageDoor, x+(int)tileMap.getx(), y+(int)tileMap.gety(), null);
			
	}
	
	/**
	* Renders enemies in singleplayer mode.
	*/
	
	private void renderEnemy(){
		ArrayList<Enemy> enemyList = world.getEnemyList();
		BufferedImage image = enemyList.get(0).getSprite();
		for (Enemy e : enemyList){
			int x = (int)e.getXPos();
			int y = (int)e.getYPos();
			g.drawImage(image, x+(int)tileMap.getx(), y+(int)tileMap.gety(), null);
		}
	}
	
	/**
	* Renders player feedback: time, lives, points and indicators when the player is in possession of an advantage item.
	*/
	
	public void renderFeedback(){
		
		if (p.getLives()>=1){
			g.drawImage(p.getLifeSprite(p.getLives()), 830, 20, null);
		}
		
		if (p.getHasAttack()){
			g.drawImage(p.getAttackImage(), 500, 20, null);
		}
		
		g.fillRect((int)(width* 0.7), (int)(height*0.015),100 ,40 );
		g.setColor(Color.yellow);
		g.fillOval((int)(width* 0.7), (int)(height*0.027), 20, 20);
		g.setFont(new Font("Arial", Font.ROMAN_BASELINE, 20));
		g.drawString(""+p.getScore(),(int)(width* 0.75), (int)(height*0.051));
		
		
		g.drawString(world.getTime(), 20, 30);
		
		
	}
	
	/**
	* Renders radial shadow around the player.
	*/
	
	private void doLighting(){
		int r = 500;
		float x = xp + (float)tileMap.getx() + xv + (float)p.getWidth()/2;
		float y = yp + (float)tileMap.gety() + yv + (float)p.getHeight()/2;
		Graphics2D g2d = (Graphics2D)g;
		
		Point2D center = new Point2D.Float(x, y);
		float[] dist = {0.0f, 1.0f};
		Color[] colors = {new Color(0.0f, 0.0f, 0.0f, 0.0f), Color.black};
		
		RadialGradientPaint p = new RadialGradientPaint(center, r, dist, colors);
		g2d.setPaint(p);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .95f));
		g2d.fillRect(0, 0, width, height);
		g2d.dispose();
	}
	
	/**
	* Renders leaderboard when TAB key is pressed.
	*/
	
	private void renderLeaderBoard(){
		ConcurrentHashMap<String, Player> playerMap = world.getPlayerList();
		List<Player> rankedPlayers;
		Collection<Player> players = playerMap.values();
		rankedPlayers = players.stream()
			.sorted((x, y) -> Integer.compare(x.getScore(), y.getScore()))
			.collect(Collectors.toList());
		
		g.setColor(new Color(0.3f, 0.3f, 0.3f));
		g.fillRect((int)(width* 0.02), (int)(height*0.05),200 ,200 );
		g.setColor(Color.white);
		for(int i=0;i< rankedPlayers.size();i++){
			g.drawString(""+rankedPlayers.get(i).getClientID()+"      "+rankedPlayers.get(i).getScore(), (int)(width* 0.02),
					(int)(height*0.04)+((200/(rankedPlayers.size()+1))*(i+1))
					 );
		}
	}
	
	/**
	* Gets positions and velocities of world's player for use in rendering.
	*/
	
	private void getPositions(){
		xp = p.getXPos();
		yp = p.getYPos();
		xv = p.getXVel();
		yv = p.getYVel();
	}
}