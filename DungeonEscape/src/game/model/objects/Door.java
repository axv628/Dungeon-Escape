package game.model.objects;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Door extends GameObject{
	
	private BufferedImage sprite;
	
	private boolean doorReached;
	public static final int width = 75;
	public static final int height = 110;
	
	public Door(int x, int y, int width, int height, TileMap tm){
		super(x, y, width, height, tm);
		loadSprite("TheWildEscape/src/resources/door.png");
		setDoorReached(false);
	}
	
	private void loadSprite(String spriteFile){
		try {
			sprite= ImageIO.read(new File(spriteFile)); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BufferedImage getSprite(){
		return sprite;
	}
	
	public void setDoorReached(boolean doorReachedUpdate){
		doorReached=doorReachedUpdate;
	}
	
	public boolean getDoorReached(){
		return doorReached;
	}
	

}