package game.model.objects;

import java.util.Timer;
import java.util.TimerTask;

public class Coin{
	
	
	private int yGridPos;
	private int xGridPos; //these are the positions of the object in the tileMap
	private int[][] intMap;
	private static final int RESPAWN_INTERVAL = 30;
	
	public Coin(int yGridPos, int xGridPos, int[][] intMap){
		this.yGridPos = yGridPos;
		this.xGridPos = xGridPos;
		this.intMap = intMap;
	}
	
	public void respawn(){
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  intMap[yGridPos][xGridPos] = 27;
			  }
			}, RESPAWN_INTERVAL*1000); //Respawns after 30 seconds
	}
}
