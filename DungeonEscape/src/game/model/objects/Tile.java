package game.model.objects;

import java.awt.image.BufferedImage;

public class Tile{
	private BufferedImage image;
	private int type;
	
	public static final int NORMAL = 0;		//types 
	public static final int SOLID = 1;
	public static final int CLIMBABLE = 2;
	
	public Tile(BufferedImage image, int type){
		this.image = image;
		this.type = type;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
}
