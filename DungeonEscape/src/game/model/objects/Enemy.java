package game.model.objects;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import game.view.SoundManager;

public class Enemy extends GameObject{

	private BufferedImage[] spriteSet = new BufferedImage[2];
	private BufferedImage sprite;
	
	private int speed = 3;
	private float xSpawn;
	
	/**
	 * the number of tiles the enemy is programmed to move
	 */
	private int tilesMoved = 4;
	private Player player;
	private boolean invincible;
	
	private SoundManager soundManager;
	/**
	 * The time in which an enemy cannot damage a player
	 * it is in seconds
	 */
	private static final int INVINCIBILITY_TIME = 2;
	
	/**
	 * represents one enemy
	 * called in World
	 * coordinates read from tileMap
	 */
	public Enemy(float x, float y, int width, int height, TileMap tm, Player player) {
		super(x, y, width, height, tm);
		xSpawn = x;
		loadSprites("TheWildEscape/src/resources/Enemy.png");
		this.player = player;
		soundManager = new SoundManager();
	}
	/**
	 * method is looped to constantly update the enemy position
	 * checks for player collisions
	 * checks the enemy's velocity to update it based on how many tiles it has moved
	 */
	public void move() {
		setXPos(getXPos()+speed);
		checkSpeed();
		checkPlayerCollision();
	}
	/**
	 * check if the player's position matches the enemy's position
	 * handles the effects if it is the case
	 */
	private void checkPlayerCollision() {
		if (!invincible && (int)getXPos() < (int)player.getXPos() + player.getWidth() && (int)getXPos()+getWidth() > (int)player.getXPos() 
				&& (int)getYPos() < (int)player.getYPos() + player.getHeight() && (int)getYPos() + getHeight() >= (int)player.getYPos()){
			soundManager.playOuch();
			player.deductLife();
			invincible = true;
			resetInvincible();
			System.out.println(player.getLives());
		}
	}
	/**
	 * handles the time in which the enemy cannot damage the player
	 */
	public void resetInvincible(){
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask() {
			@Override
			  public void run() {
				  invincible = false;
			  }
			}, INVINCIBILITY_TIME *1000); 
	}

	private void checkSpeed() {
		if (speed>0 && getXPos() > xSpawn + 64*tilesMoved ){
			speed = -3;
		} else if (speed<0 && getXPos() < xSpawn){
			speed = 3;
		}
		
	}

	private void loadSprites(String spriteFile){
		try {
			BufferedImage image = ImageIO.read(new File(spriteFile));
			BufferedImage sprite1 = image.getSubimage(0, 0, image.getWidth()/2, image.getHeight());
			spriteSet[0] = sprite1;
			BufferedImage sprite2 = image.getSubimage(image.getWidth()/2, 0, image.getWidth()/2, image.getHeight());
			spriteSet[1] = sprite2;
			height = sprite1.getHeight();
			width = sprite1.getWidth();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getSprite(){
		if (speed >= 0){
			sprite = spriteSet[0];
		}
		if (speed < 0){
			sprite = spriteSet[1];
		}
		return sprite;
	}

}
