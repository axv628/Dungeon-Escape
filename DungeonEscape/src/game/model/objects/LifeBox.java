package game.model.objects;

import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

public class LifeBox{
	
	
	private int yGridPos;
	private int xGridPos;
	private int[][] intMap;
	private static final int RESPAWN_INTERVAL = 180; //this is in seconds
	
	public LifeBox(int yGridPos, int xGridPos, int[][] intMap){
		this.yGridPos = yGridPos;
		this.xGridPos = xGridPos;
		this.intMap = intMap;
	}
	
	public void randomNewPosL(){ //will return new position, method will be different to AttackBox reposition
		//will contain way of assigning a new position such that it respawns in an appropriate place
		//method depends on structure of map (platforms etc)
		
		//alternatively we will do something simpler that may not require this method
	}
	
	public void respawn(){
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  intMap[yGridPos][xGridPos] = 28;
			  }
			}, RESPAWN_INTERVAL*1000); 
	}
	
	public void render(Graphics2D g){
		//Draw a colored square for now; we'll sort out sprites later.
	}
}